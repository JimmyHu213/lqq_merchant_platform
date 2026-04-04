package com.zbkj.service.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.constants.UploadConstants;
import com.zbkj.common.enums.RoleEnum;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.system.SystemAttachment;
import com.zbkj.common.page.CommonPage;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.SystemAttachmentMoveRequest;
import com.zbkj.common.request.SystemAttachmentSearchRequest;
import com.zbkj.common.request.SystemAttachmentUpdateNameRequest;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.CrmebUtil;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.dao.SystemAttachmentDao;
import com.zbkj.service.service.SystemAttachmentService;
import com.zbkj.service.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * SystemAttachmentServiceImpl 接口实现
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
public class SystemAttachmentServiceImpl extends ServiceImpl<SystemAttachmentDao, SystemAttachment>
        implements SystemAttachmentService {

    @Resource
    private SystemAttachmentDao dao;

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 附件分页
     *
     * @param pid              Integer pid
     * @param pageParamRequest PageParamRequest 分页参数
     * @return List<SystemAttachment>
     */
    @Override
    public List<SystemAttachment> getList(Integer pid, String attType, PageParamRequest pageParamRequest) {
        PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<SystemAttachment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SystemAttachment::getOwner, getOwnerByCurrentAdmin());
        if (StrUtil.isNotBlank(attType)) {
            lambdaQueryWrapper.in(SystemAttachment::getAttType, StrUtil.split(attType, ','));
        }
        if (ObjectUtil.isNotNull(pid) && pid > 0) {
            lambdaQueryWrapper.eq(SystemAttachment::getPid, pid);
        }
        lambdaQueryWrapper.orderByDesc(SystemAttachment::getAttId);
        return dao.selectList(lambdaQueryWrapper);
    }

    /**
     * 附件分页列表
     */
    @Override
    public PageInfo<SystemAttachment> findPageList(SystemAttachmentSearchRequest request) {
        LambdaQueryWrapper<SystemAttachment> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAttachment::getOwner, getOwnerByCurrentAdmin());
        if (ObjectUtil.isNotNull(request.getPid()) && request.getPid() > 0) {
            lqw.eq(SystemAttachment::getPid, request.getPid());
        }
        if (StrUtil.isNotBlank(request.getType())) {
            switch (request.getType()) {
                case "image":
                    lqw.in(SystemAttachment::getAttType, StrUtil.split("jpg,jpeg,gif,png,bmp,PNG,JPG,webp", ','));
                    break;
                case "video":
                    lqw.in(SystemAttachment::getAttType, StrUtil.split("video/mp4,avi,mov,wmv,mkv,flv,rmvb", ','));
                    break;
            }
        }
        if (StrUtil.isNotBlank(request.getKeywords())) {
            lqw.like(SystemAttachment::getAlias, URLUtil.decode(request.getKeywords()).trim());
        }
        lqw.orderByDesc(SystemAttachment::getAttId);
        Page<SystemAttachment> page = PageHelper.startPage(request.getPage(), request.getLimit());
        List<SystemAttachment> attachmentList = dao.selectList(lqw);
        return CommonPage.copyPageInfo(page, attachmentList);
    }

    /**
     * 给图片加前缀
     *
     * @param path String 路径
     * @return String
     */
    @Override
    public String prefixImage(String path) {
        if (path.contains(UploadConstants.UPLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_FILE_TYPE_IMAGE)) {
            if (path.contains(UploadConstants.UPLOAD_FILE_KEYWORD + "/public")) { // 兼容历史数据处理
                path = path.replace(UploadConstants.UPLOAD_FILE_KEYWORD + "/public", getCdnUrl() + "/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/public");
            }
            return path.replace(UploadConstants.UPLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_FILE_TYPE_IMAGE, getCdnUrl() + "/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/" + UploadConstants.UPLOAD_FILE_TYPE_IMAGE);
        }
        // 如果那些域名不需要加，则跳过
        return path.replace(UploadConstants.UPLOAD_FILE_KEYWORD + "/", getCdnUrl() + "/" + UploadConstants.UPLOAD_FILE_KEYWORD + "/");
    }

    @Override
    public String prefixUploadf(String path) {
        if (path.contains("crmebimage")) {
            // 如果那些域名不需要加，则跳过
            return path.replace("crmebimage/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/", getCdnUrl() + "/" + "crmebimage/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/");
        }
        return path.replace(UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/", getCdnUrl() + "/" + UploadConstants.UPLOAD_AFTER_FILE_KEYWORD + "/");
    }

    /**
     * 给文件加前缀
     *
     * @param path String 路径
     * @return String
     */
    @Override
    public String prefixFile(String path) {
        if (path.contains("downloadf/excel")) {
            String cdnUrl = systemConfigService.getValueByKey("local" + "UploadUrl");
            return path.replace("crmebimage/downloadf/", cdnUrl + "/crmebimage/downloadf/");
        }
        return path.replace("crmebimage/file/", getCdnUrl() + "/crmebimage/file/");
    }

    /**
     * 清除 cdn url， 在保存数据的时候使用
     *
     * @param path String 文件路径
     * @return String
     */
    @Override
    public String clearPrefix(String path) {
        if (StrUtil.isBlank(path)) {
            return path;
        }
        if (path.contains(getCdnUrl() + "/")) {
            if (path.contains("callback/alipay")) {
                return path;
            }
            return path.replace(getCdnUrl() + "/", "");
        }

        return path;
    }

    /**
     * 清除 cdn url， 在保存数据的时候使用
     *
     * @param path String 文件路径
     * @return String
     */
    @Override
    public String clearPrefix(String path, String cdnUrl) {
        if (StrUtil.isBlank(path)) {
            return path;
        }
        if (path.contains(cdnUrl + "/")) {
            return path.replace(cdnUrl + "/", "");
        }

        return path;
    }

    /**
     * 更改图片目录
     *
     * @param move 参数
     */
    @Override
    public Boolean updateAttrId(SystemAttachmentMoveRequest move) {
        LambdaUpdateWrapper<SystemAttachment> lup = new LambdaUpdateWrapper<>();
        lup.set(SystemAttachment::getPid, move.getPid());
        lup.in(SystemAttachment::getAttId, CrmebUtil.stringToArray(move.getAttrId()));
        lup.eq(SystemAttachment::getOwner, getOwnerByCurrentAdmin());
        return update(lup);
    }

    /**
     * 获取cdn url
     *
     * @return String
     */
    @Override
    public String getCdnUrl() {
        String uploadType = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_UPLOAD_TYPE);
        //获取配置信息
        int type = Integer.parseInt(uploadType);
        String uploadUrl = SysConfigConstants.CONFIG_LOCAL_UPLOAD_URL;
        switch (type) {
            case 2:
                uploadUrl = SysConfigConstants.CONFIG_QN_UPLOAD_URL;
                break;
            case 3:
                uploadUrl = SysConfigConstants.CONFIG_AL_UPLOAD_URL;
                break;
            case 4:
                uploadUrl = SysConfigConstants.CONFIG_TX_UPLOAD_URL;
                break;
            case 5:
                uploadUrl = SysConfigConstants.CONFIG_JD_UPLOAD_URL;
                break;
            case 6:
                uploadUrl = SysConfigConstants.CONFIG_HW_OBS_UPLOAD_URL;
                break;
            default:
                break;
        }
        return systemConfigService.getValueByKey(uploadUrl);
    }

    /**
     * 删除附件
     *
     * @param idList 附件ID列表
     */
    @Override
    public Boolean deleteByIds(List<Integer> idList) {
        Integer owner = getOwnerByCurrentAdmin();
        LambdaUpdateWrapper<SystemAttachment> wrapper = Wrappers.lambdaUpdate();
        wrapper.eq(SystemAttachment::getOwner, owner);
        wrapper.in(SystemAttachment::getAttId, idList);
        return remove(wrapper);
    }

    /**
     * 编辑附件别名
     */
    @Override
    public Boolean updateAlias(SystemAttachmentUpdateNameRequest request) {
        SystemAttachment systemAttachment = getByIdException(request.getAttId(), getOwnerByCurrentAdmin());
        String alias = URLUtil.decode(request.getAlias()).trim();
        if (systemAttachment.getAlias().equals(alias)) return true;

        int dotIndex = alias.lastIndexOf(".");
        String extension = alias.substring(dotIndex + 1);

        String oldExtension = systemAttachment.getName().substring(systemAttachment.getName().lastIndexOf(".") + 1);
        if (!oldExtension.equals(extension)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "素材文件拓展名不能修改");
        }
        return updateAttachmentAlias(request.getAttId(), alias);
    }

    private Boolean updateAttachmentAlias(Integer attId, String alias) {
        LambdaUpdateWrapper<SystemAttachment> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(SystemAttachment::getAlias, alias);
        wrapper.eq(SystemAttachment::getAttId, attId);
        return update(wrapper);
    }

    private SystemAttachment getByIdException(Integer id, Integer owner) {
        LambdaQueryWrapper<SystemAttachment> lqw = Wrappers.lambdaQuery();
        lqw.eq(SystemAttachment::getAttId, id);
        lqw.eq(SystemAttachment::getOwner, owner);
        lqw.last(" limit 1");
        SystemAttachment systemAttachment = dao.selectOne(lqw);
        if (ObjectUtil.isNull(systemAttachment)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "素材文件不存在");
        }
        return systemAttachment;
    }

    public Integer getOwnerByCurrentAdmin() {
        SystemAdmin currentAdmin = SecurityUtil.getLoginUserVo().getUser();
        if (currentAdmin.getType().equals(RoleEnum.SUPER_MERCHANT.getValue()) ||
                currentAdmin.getType().equals(RoleEnum.MERCHANT_ADMIN.getValue())) {
            return currentAdmin.getMerId();
        }
        return -1;
    }
}

