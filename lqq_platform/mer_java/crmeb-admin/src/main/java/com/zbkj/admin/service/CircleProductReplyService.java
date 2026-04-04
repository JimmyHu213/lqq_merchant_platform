package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.response.ProductReplyResponse;

public interface CircleProductReplyService {

    /**
     * 圈层端商品评论列表
     * @param request 请求参数
     * @return PageInfo
     */
    PageInfo<ProductReplyResponse> getCircleProductReplyPage(ProductReplySearchRequest request);
}
