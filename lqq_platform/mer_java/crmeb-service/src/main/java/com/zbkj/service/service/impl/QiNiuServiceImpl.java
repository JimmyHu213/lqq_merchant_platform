package com.zbkj.service.service.impl;

import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.QiNiuService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * 七牛云KODO服务实现类
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
public class QiNiuServiceImpl implements QiNiuService {
    private static final Logger logger = LoggerFactory.getLogger(QiNiuServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void uploadFile(String webPth, String localFile, File file) {
        if (!file.exists()) {
            logger.error("上传文件" + localFile + "不存在：");
            throw new CrmebException("上传文件" + localFile + "不存在：");
        }
        UploadManager uploadManager = getUploadManager();
        // 生成上传凭证，然后准备上传
        String uploadToken = getUploadToken();
        try {
            Response put = uploadManager.put(file, webPth, uploadToken);
            put.close();
        } catch (QiniuException ex) {
            logger.error("七牛云上传文件失败！", ex);
            throw new CrmebException(ex.getMessage());
        }
    }

    private String getUploadToken() {
//        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_QN_UPLOAD_URL);
        String accessKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_QN_ACCESS_KEY);
        String secretKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_QN_SECRET_KEY);
        String bucketName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_QN_STORAGE_NAME);
//        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_QN_STORAGE_REGION);
        Auth auth = Auth.create(accessKey, secretKey);
        return auth.uploadToken(bucketName);
    }

    private UploadManager getUploadManager() {
        // 构造一个带指定Zone对象的配置类, 默认华东
        Configuration cfg = new Configuration(Region.autoRegion());
        // 其他参数参考类注释
        return new UploadManager(cfg);
    }
}

