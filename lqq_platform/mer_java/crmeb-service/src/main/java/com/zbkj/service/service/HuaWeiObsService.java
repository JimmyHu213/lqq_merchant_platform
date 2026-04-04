package com.zbkj.service.service;

import java.io.File;

/**
 * 类的详细说明
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/6/23
 */
public interface HuaWeiObsService {

    /**
     * 文件上传
     *
     * @param fileName      文件名称
     * @param localFilePath 本地文件地址
     */
    void uploadFile(String fileName, String localFilePath, File file);

}
