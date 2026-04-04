package com.zbkj.service.service.impl;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.CosService;
import com.zbkj.service.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;


/**
 * 腾讯云COS服务实现类
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
public class CosServiceImpl implements CosService {

    private static final Logger logger = LoggerFactory.getLogger(CosServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void uploadFile(String webPth, String localFile, File file) {
        if (!file.exists()) {
            logger.error("上传文件" + localFile + "不存在：");
            throw new CrmebException("上传文件" + localFile + "不存在：");
        }
        String bucketName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_TX_STORAGE_NAME);

        COSClient cosClient = getCosClient();

        try {
            if (!cosClient.doesBucketExist(bucketName)) {
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                // 设置 bucket 的权限为 Private(私有读写), 其他可选有公有读私有写, 公有读写
                createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
                try {
                    cosClient.createBucket(createBucketRequest);
                } catch (CosClientException serverException) {
                    serverException.printStackTrace();
                }
            }

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, webPth, file);
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        } catch (Exception e) {
            logger.error("腾讯云上传文件失败！", e);
            throw new CrmebException(e.getMessage());
        } finally {
            cosClient.shutdown();
        }
    }

    private COSClient getCosClient() {
        systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_TX_UPLOAD_URL);
        String accessKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_TX_ACCESS_KEY);
        String secretKey = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_TX_SECRET_KEY);

        String region = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_TX_STORAGE_REGION);
        // 1 初始化用户身份信息(secretId, secretKey)
        COSCredentials cred = new BasicCOSCredentials(accessKey, secretKey);
        // 2 设置bucket的区域, COS地域的简称请参照 https://cloud.tencent.com/document/product/436/6224
        ClientConfig clientConfig = new ClientConfig(new com.qcloud.cos.region.Region(region));
        // 3 生成 cos 客户端。
        return new COSClient(cred, clientConfig);
    }

}

