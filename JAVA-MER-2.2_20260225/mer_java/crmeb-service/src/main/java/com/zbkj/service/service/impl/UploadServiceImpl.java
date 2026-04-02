package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.zbkj.common.config.CrmebConfig;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.UploadUtil;
import com.zbkj.common.vo.FileResultVo;
import com.zbkj.common.vo.MyRecord;
import com.zbkj.service.service.*;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.Base64; // [LQQ-迁移] 替换 sun.misc.BASE64Decoder，兼容 Java 11+

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;


/**
 * UploadServiceImpl 接口实现
 * +----------------------------------------------------------------------
 * | CRMEB [ CRMEB赋能开发者，助力企业发展 ]
 * +----------------------------------------------------------------------
 * | Copyright (c) 2016~2026 https://www.crmeb.com All rights reserved.
 * +----------------------------------------------------------------------
 * | Licensed CRMEB并不是自由软件，未经许可不能去掉CRMEB相关版权
 * +----------------------------------------------------------------------
 * | Author: CRMEB Team <admin@crmeb.com>
 * +----------------------------------------------------------------------
 */
@Service
public class UploadServiceImpl implements UploadService {

    private static final Logger logger = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private SystemAttachmentService systemAttachmentService;
    @Autowired
    private QiNiuService qiNiuService;
    @Autowired
    private OssService ossService;
    @Autowired
    private CosService cosService;
    @Autowired
    private CrmebConfig crmebConfig;
    @Autowired
    private SystemRoleService systemRoleService;
    @Autowired
    private JdCloudService jdCloudService;
    @Autowired
    private HuaWeiObsService huaWeiObsService;

    /**
     * 图片上传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @return FileResultVo
     */
    @Override
    public FileResultVo imageUploadV2(MultipartFile multipartFile, Integer pid) {
        Integer ownerId = systemRoleService.getOwnerByCurrentAdmin();
        return imageUploadV2(multipartFile, pid, ownerId);
    }

    /**
     * 文件长传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @return FileResultVo
     */
    @Override
    public FileResultVo fileUploadV2(MultipartFile multipartFile, Integer pid) {
        Integer ownerId = systemRoleService.getOwnerByCurrentAdmin();
        return fileUploadV2(multipartFile, pid, ownerId);
    }

    /**
     * 图片上传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @param ownerId       归属方
     * @return FileResultVo
     */
    @Override
    public FileResultVo imageUploadV2(MultipartFile multipartFile, Integer pid, Integer ownerId) {
        FileResultVo fileResultVo = new FileResultVo();
        try {
            fileResultVo = commonUploadV2(multipartFile, pid, UploadConstants.UPLOAD_FILE_TYPE_IMAGE, ownerId);
        } catch (IOException e) {
            logger.error("图片上传IO异常，{}", e.getMessage());
            throw new CrmebException("图片上传 IO异常");
        }
        return fileResultVo;
    }

    /**
     * 文件长传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @param ownerId       归属方
     * @return FileResultVo
     */
    @Override
    public FileResultVo fileUploadV2(MultipartFile multipartFile, Integer pid, Integer ownerId) {
        FileResultVo fileResultVo = new FileResultVo();
        try {
            fileResultVo = commonUploadV2(multipartFile, pid, UploadConstants.UPLOAD_FILE_TYPE_FILE, ownerId);
        } catch (IOException e) {
            logger.error("文件上传IO异常，{}", e.getMessage());
            throw new CrmebException("文件上传 IO异常");
        }
        return fileResultVo;
    }

    /**
     * base64图片上传V2
     *
     * @param base64 base64地址
     * @return FileResultVo
     */
    @Override
    public FileResultVo base64UploadV2(String base64) {

        MyRecord fileRecord = createFileRecord(UploadConstants.UPLOAD_FILE_TYPE_IMAGE, "jpg");
        String filePath = fileRecord.getStr("filePath");
        String localFile = fileRecord.getStr("localFile");
        String destPath = fileRecord.getStr("destPath");
        String newFileName = fileRecord.getStr("newFileName");

        File file = base64ToFile(base64, destPath);

        // 拼装返回的数据
        FileResultVo resultFile = new FileResultVo();
        resultFile.setFileSize(file.length());
        resultFile.setFileName(newFileName);
        resultFile.setExtName("jpg");
        resultFile.setUrl(filePath);
        resultFile.setType("jpg");

        SystemAttachment systemAttachment = new SystemAttachment();
        systemAttachment.setName(resultFile.getFileName());
        systemAttachment.setSattDir(resultFile.getUrl());
        systemAttachment.setAttSize(resultFile.getFileSize().toString());
        systemAttachment.setAttType(resultFile.getType());
        systemAttachment.setImageType(1);
        systemAttachment.setPid(0);
        systemAttachment.setOwner(-2);

        //图片上传类型 1本地 2七牛云 3OSS 4COS, 默认本地
        String uploadType = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_UPLOAD_TYPE);
        Integer uploadTypeInt = Integer.parseInt(uploadType);
        if (uploadTypeInt > 1) {// 第三方文件上传
            systemAttachment.setImageType(uploadTypeInt);
            thirdPartyUpload(uploadTypeInt, filePath, localFile, file);
            // 判断是否保存本地
            String fileIsSave = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_FILE_IS_SAVE);
            if (!fileIsSave.equals("1")) {
                // 删除本地文件
                file.delete();
            }
        }
        systemAttachmentService.save(systemAttachment);
        return resultFile;
    }

    /**
     * 生成文件上传相关信息
     */
    private MyRecord createFileRecord(String fileType, String extName) {
        // 服务器存储地址
        String rootPath = crmebConfig.getImagePath().trim();// /www/wwwroot/java/admin/
        // 文件类型地址
        String fileTypePath = UploadConstants.UPLOAD_FILE_KEYWORD + "/" + fileType + "/";// crmebimage/file/ or crmebimage/image/
        // 生成唯一的文件名（带拓展名）
        String newFileName = UploadUtil.fileName(extName);
        // 日期地址
        String datePath = CrmebDateUtil.nowDate("yyyy/MM/dd") + "/";
        // 图片地址
        String webPath = fileTypePath + datePath;

        // 服务器存放文件绝对地址
        String path = rootPath + webPath;
        // 文件分隔符转化为当前系统的格式
        String destPath = FilenameUtils.separatorsToSystem(path) + newFileName;
        MyRecord record = new MyRecord();
        String filePath = webPath + newFileName;
        record.set("filePath", filePath);// 以crmebimage开头的地址
        record.set("localFile", rootPath + "/" + filePath);// 文件绝路路径
        record.set("destPath", destPath);// 转换过格式的文件绝对地址
        record.set("newFileName", newFileName);// 转换过格式的文件绝对地址
        return record;
    }

    private File base64ToFile(String base64, String destPath) {
        File file = null;
        OutputStream out = null;
        try {
            file = UploadUtil.createFile(destPath);
            // 去掉base64前缀 data:image/jpeg;base64,
            base64 = base64.substring(base64.indexOf(",", 1) + 1);
            // 解密，解密的结果是一个byte数组
            // [LQQ-迁移] 兼容 Java 11+
            byte[] imgbytes = Base64.getDecoder().decode(base64);
            for (int i = 0; i < imgbytes.length; ++i) {
                if (imgbytes[i] < 0) {
                    imgbytes[i] += 256;
                }
            }
            logger.info("base64图片地址：=========================================================================" + destPath);
            // 保存图片
            out = new FileOutputStream(file);
            out.write(imgbytes);
            out.flush();
        } catch (IOException e) {
            logger.error("base64上传——转图片异常", e);
            throw new CrmebException("base64上传——转图片异常:" + e.getMessage());
        } finally {
            try {
                assert out != null;
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("base64上传——关闭OutputStream流异常");
            }
        }
        return file;
    }

    /**
     * 上传校验
     *
     * @param fileName 文件名称
     * @param fileSize 文件大小
     * @return 后缀名
     */
    private String uploadValidate(String fileName, float fileSize, String fileType, String contentType) {
        // 文件后缀名
        String extName = FilenameUtils.getExtension(fileName).toLowerCase();
        if (StrUtil.isNotBlank(extName)) {
            if (StrUtil.isNotBlank(contentType)) {
                extName = contentType.split("/")[1];
            } else {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "文件类型未定义，无法上传...");
            }
        }

        String extStr = systemConfigService.getValueByKey(fileType.equals(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD) ? SysConfigConstants.UPLOAD_FILE_EXT_STR_CONFIG_KEY : SysConfigConstants.UPLOAD_IMAGE_EXT_STR_CONFIG_KEY);
        // 判断文件的后缀名是否符合规则
        if (StrUtil.isNotBlank(extStr)) {
            // 切割文件扩展名
            List<String> extensionList = CrmebUtil.stringToArrayStr(extStr);
            if (CollUtil.isNotEmpty(extensionList)) {
                //判断
                if (!extensionList.contains(extName)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上载文件类型只能为：" + extStr);
                }
            } else {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上载文件类型只能为：" + extStr);
            }
        }
        // 文件大小验证
        int size = Integer.parseInt(systemConfigService.getValueByKey(fileType.equals(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD) ? SysConfigConstants.UPLOAD_FILE_MAX_SIZE_CONFIG_KEY : SysConfigConstants.UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY));
        String fs = String.format("%.2f", fileSize);
        if (fileSize > size) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("最大允许上传 {} MB文件，当前文件大小为 {} MB", size, fs));
        }
        return extName;
    }

    /**
     * 公共上传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @param fileType      文件类型 image-图片，file-文件
     * @param owner         归属方 用户:-2, 平台：-1，商户：>0
     * @return FileResultVo
     * @throws IOException IOE异常
     */
    private FileResultVo commonUploadV2(MultipartFile multipartFile, Integer pid, String fileType, Integer owner) throws IOException {
        if (ObjectUtil.isNull(multipartFile) || multipartFile.isEmpty()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上载的文件对象不存在...");
        }
        // 获取上传文件的原始文件名（包含扩展名）
        String fileName = multipartFile.getOriginalFilename();
        // 获取上传文件的原始大小转为MB
        float fileSize = (float) multipartFile.getSize() / 1024 / 1024;
        // 获取上传文件的MIME类型
        String contentType = multipartFile.getContentType();
        // 素材文件校验，并返回文件后缀名
        String extName = uploadValidateV2(fileName, fileSize, fileType, contentType);
        if (fileName.length() > 99) {
            fileName = StrUtil.subPre(fileName, 90).concat(".").concat(extName);
        }

        MyRecord fileRecord = createFileRecord(fileType, extName);
        String filePath = fileRecord.getStr("filePath");
        String localFile = fileRecord.getStr("localFile");
        String destPath = fileRecord.getStr("destPath");

        // 创建文件
        File file = UploadUtil.createFile(destPath);

        // 拼装返回的数据
        FileResultVo resultFile = new FileResultVo();
        resultFile.setFileSize(multipartFile.getSize());
        resultFile.setFileName(fileName);
        resultFile.setExtName(extName);
        resultFile.setUrl(filePath);// 以crmebimage开头的地址
        resultFile.setType(multipartFile.getContentType());
        if (fileType.equals(UploadConstants.UPLOAD_FILE_TYPE_IMAGE)) {
            resultFile.setType(resultFile.getType().replace("image/", ""));
        } else {
            resultFile.setType(resultFile.getType().replace("file/", ""));
        }

        SystemAttachment systemAttachment = new SystemAttachment();
        systemAttachment.setName(resultFile.getFileName());
        systemAttachment.setAlias(resultFile.getFileName());
        systemAttachment.setSattDir(resultFile.getUrl());// 以crmebimage开头的文件地址
        systemAttachment.setAttSize(resultFile.getFileSize().toString());
        systemAttachment.setAttType(resultFile.getType());
        systemAttachment.setImageType(1);
        systemAttachment.setPid(pid);
        systemAttachment.setOwner(owner);

        //图片上传类型 1本地 2七牛云 3阿里云OSS 4腾讯云COS 5京东云 6华为云OBS, 默认本地
        String uploadType = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_UPLOAD_TYPE);
        Integer uploadTypeInt = Integer.parseInt(uploadType);
        multipartFile.transferTo(file);
        if (uploadTypeInt > 1) {// 第三方文件上传
            systemAttachment.setImageType(uploadTypeInt);
            thirdPartyUpload(uploadTypeInt, filePath, localFile, file);
            // 判断是否保存本地
            String fileIsSave = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_FILE_IS_SAVE);
            if (!fileIsSave.equals("1")) {
                // 删除本地文件
                file.delete();
            }
        }
        systemAttachmentService.save(systemAttachment);
        return resultFile;
    }

    /**
     * 第三方上传
     *
     * @param uploadType 上传类型 2七牛云 3阿里云OSS 4腾讯云COS 5京东云 6华为云OBS
     * @param filePth    文件路径（crmebimage开头）
     * @param localFile  文件绝路路径
     * @param file       上传的文件
     */
    private void thirdPartyUpload(Integer uploadType, String filePth, String localFile, File file) {
        switch (uploadType) {
            case 2:
                try {
                    qiNiuService.uploadFile(filePth, localFile, file);
                } catch (Exception e) {
                    logger.error("upload qiniu kodo fail " + e.getMessage());
                }
                break;
            case 3:
                try {
                    ossService.upload(filePth, localFile, file);
                } catch (Exception e) {
                    logger.error("upload ali oss fail " + e.getMessage());
                }
                break;
            case 4:
                try {
                    cosService.uploadFile(filePth, localFile, file);
                } catch (Exception e) {
                    logger.error("upload tx cos fail " + e.getMessage());
                }
                break;
            case 5: // 京东云存储
                try {
                    jdCloudService.uploadFile(filePth, localFile, file);
                } catch (Exception e) {
                    logger.error("AsyncServiceImpl.cos.fail " + e.getMessage());
                }
                break;
            case 6: // 华为云OBS
                try {
                    huaWeiObsService.uploadFile(filePth, localFile, file);
                } catch (Exception e) {
                    logger.error("upload huawei obs fail " + e.getMessage());
                }
                break;
        }
    }

    /**
     * 上传校验
     *
     * @param fileName    文件的原始文件名（包含扩展名）
     * @param fileSize    文件的大小MB
     * @param fileType    文件类型: image-图片，file-文件
     * @param contentType 文件的MIME类型
     * @return 后缀名
     */
    private String uploadValidateV2(String fileName, float fileSize, String fileType, String contentType) {
        // 文件后缀名
        String extName = FilenameUtils.getExtension(fileName).toLowerCase();
        if (StrUtil.isNotBlank(extName)) {
            if (StrUtil.isNotBlank(contentType)) {
                extName = contentType.split("/")[1];
            } else {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "文件类型未定义，无法上传...");
            }
        }

        String extStr = systemConfigService.getValueByKey(fileType.equals(UploadConstants.UPLOAD_FILE_TYPE_FILE) ? SysConfigConstants.UPLOAD_FILE_EXT_STR_CONFIG_KEY : SysConfigConstants.UPLOAD_IMAGE_EXT_STR_CONFIG_KEY);
        // 判断文件的后缀名是否符合规则
        if (StrUtil.isNotBlank(extStr)) {
            // 切割文件扩展名
            List<String> extensionList = CrmebUtil.stringToArrayStr(extStr);
            if (CollUtil.isNotEmpty(extensionList)) {
                //判断
                if (!extensionList.contains(extName)) {
                    throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "上载文件类型只能为：" + extStr);
                }
            } else {
                throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "请先配置上载文件类型");
            }
        }
        // 文件大小验证
        int size = Integer.parseInt(systemConfigService.getValueByKey(fileType.equals(UploadConstants.UPLOAD_FILE_TYPE_FILE) ? SysConfigConstants.UPLOAD_FILE_MAX_SIZE_CONFIG_KEY : SysConfigConstants.UPLOAD_IMAGE_MAX_SIZE_CONFIG_KEY));
        String fs = String.format("%.2f", fileSize);
        if (fileSize > size) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, StrUtil.format("最大允许上传 {} MB文件，当前文件大小为 {} MB", size, fs));
        }
        return extName;
    }
}

