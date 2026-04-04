package com.zbkj.service.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.OssService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * 阿里云OSS服务实现类
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
public class OssServiceImpl implements OssService {

    private static final Logger logger = LoggerFactory.getLogger(OssServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void upload(String webPth, String localFile, File file) {
        if (!file.exists()) {
            logger.error("上传文件" + localFile + "不存在：");
            throw new CrmebException("上传文件" + localFile + "不存在：");
        }

        String bucketName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_STORAGE_NAME);

        OSS ossClient = getOssClent();
        try {
            //判断bucket是否存在
            if (!ossClient.doesBucketExist(bucketName)) {
                ossClient.createBucket(bucketName);
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, webPth, file);
            // 上传文件。
            PutObjectResult putObjectResult = ossClient.putObject(putObjectRequest);
        } catch (Exception e) {
            logger.error("阿里云上传文件失败！", e);
            throw new CrmebException(e.getMessage());
        } finally {
            ossClient.shutdown();
        }
    }

    private OSS getOssClent() {
        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_UPLOAD_URL);
        String accessKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_ACCESS_KEY);
        String secretKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_SECRET_KEY);
        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_STORAGE_NAME);
        String region = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_AL_STORAGE_REGION);
        return new OSSClientBuilder().build(region, accessKey, secretKey);
    }


}

