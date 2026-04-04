package com.zbkj.service.service;

import com.zbkj.common.vo.FileResultVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * UploadService 接口
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
public interface UploadService {

    /**
     * 图片上传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @return FileResultVo
     */
    FileResultVo imageUploadV2(MultipartFile multipartFile, Integer pid);

    /**
     * 文件长传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @return FileResultVo
     */
    FileResultVo fileUploadV2(MultipartFile multipartFile, Integer pid);

    /**
     * 图片上传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @param ownerId       归属方
     * @return FileResultVo
     */
    FileResultVo imageUploadV2(MultipartFile multipartFile, Integer pid, Integer ownerId);

    /**
     * 文件长传V2
     *
     * @param multipartFile 文件
     * @param pid           分类ID
     * @param ownerId       归属方
     * @return FileResultVo
     */
    FileResultVo fileUploadV2(MultipartFile multipartFile, Integer pid, Integer ownerId);

    /**
     * base64图片上传V2
     *
     * @param base64 base64地址
     * @return FileResultVo
     */
    FileResultVo base64UploadV2(String base64);
}
