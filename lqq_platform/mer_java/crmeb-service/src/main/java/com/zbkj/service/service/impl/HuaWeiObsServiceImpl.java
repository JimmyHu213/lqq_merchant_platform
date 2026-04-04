package com.zbkj.service.service.impl;

import com.obs.services.ObsClient;
import com.obs.services.exception.ObsException;
import com.obs.services.model.PutObjectRequest;
import com.zbkj.common.constants.SysConfigConstants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.service.service.HuaWeiObsService;
import com.zbkj.service.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/6/23
 */
@Slf4j
@Service
public class HuaWeiObsServiceImpl implements HuaWeiObsService {

    @Autowired
    private SystemConfigService systemConfigService;

    /**
     * 文件上传
     *
     * @param fileName      文件名称
     * @param localFilePath 本地文件地址
     */
    @Override
    public void uploadFile(String fileName, String localFilePath, File file) {
        if (!file.exists()) {
            log.error("华为云存储上传文件，不存在，fileName = {}， localFilePath = {}", fileName, localFilePath);
            return;
        }
        String bucketName = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_HW_OBS_BUCKET_NAME);

        ObsClient obsClient = getObsClient();
        try {
            // 文件上传
            // localfile 为待上传的本地文件路径，需要指定到具体的文件名
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(bucketName);
            request.setObjectKey(fileName);
            request.setFile(file);
            obsClient.putObject(request);
            log.info("putObject successfully");
        } catch (ObsException e) {
            log.error("upload huaweiObs putObject failed");
            // 请求失败,打印http状态码
            log.error("HTTP Code:" + e.getResponseCode());
            // 请求失败,打印服务端错误码
            log.error("Error Code:" + e.getErrorCode());
            // 请求失败,打印详细错误信息
            log.error("Error Message:" + e.getErrorMessage());
            // 请求失败,打印请求id
            log.error("Request ID:" + e.getErrorRequestId());
            log.error("Host ID:" + e.getErrorHostId());
            throw new CrmebException(e.getErrorMessage());
        } catch (Exception e) {
            log.error("upload huaweiObs putObject failed", e);
            // 其他异常信息打印
            throw new CrmebException("upload huaweiObs putObject failed");
        } finally {
            try {
                obsClient.close();
            } catch (IOException e) {
                log.error("upload huaweiObs close failed", e);
            }
        }
    }

    private ObsClient getObsClient() {

        String endPoint = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_HW_OBS_ENDPOINT);
        String ak = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_HW_OBS_ACCESS_KEY);
        String sk = systemConfigService.getValueByKeyException(SysConfigConstants.CONFIG_HW_OBS_SECRET_KEY);
        // 创建ObsClient实例
        ObsClient obsClient = new ObsClient(ak, sk, endPoint);
        return obsClient;
    }
}
