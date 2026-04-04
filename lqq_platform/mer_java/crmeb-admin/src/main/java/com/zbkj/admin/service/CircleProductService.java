package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.request.PlatProductSearchRequest;
import com.zbkj.common.request.PlatProductTabsHeaderRequest;
import com.zbkj.common.response.PlatformProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;

import java.util.List;

public interface CircleProductService {


    /**
     * 查询商品列表
     * @param request 查询条件
     * @return 商品列表分页信息
     */
    PageInfo<PlatformProductListResponse> getAdminList(PlatProductSearchRequest request);

    /**
     * 获取商品表头数量
     */
    List<ProductTabsHeaderResponse> getTabsHeader(PlatProductTabsHeaderRequest request);

    /**
     * 获取商品详情
     * @param id 商品id
     * @return 商品详情信息
     */
    ProductInfoResponse getInfo(Integer id);
}
