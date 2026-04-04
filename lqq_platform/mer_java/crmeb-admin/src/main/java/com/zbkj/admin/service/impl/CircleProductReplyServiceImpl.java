package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleProductReplyService;
import com.zbkj.common.request.ProductReplySearchRequest;
import com.zbkj.common.response.ProductReplyResponse;
import com.zbkj.service.service.ProductReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleProductReplyServiceImpl implements CircleProductReplyService {

    @Autowired
    private ProductReplyService productReplyService;
    @Autowired
    private CircleHomeService circleHomeService;



    /**
     * 圈层端商品评论列表
     * @param request 请求参数
     * @return PageInfo
     */
    @Override
    public PageInfo<ProductReplyResponse> getCircleProductReplyPage(ProductReplySearchRequest request) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new PageInfo<>(new ArrayList<ProductReplyResponse>());
        }
        return productReplyService.getCircleProductReplyPage(request, merIdList);
    }
}
