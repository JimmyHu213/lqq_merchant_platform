package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.MerchantCategory;
import com.zbkj.common.model.merchant.MerchantType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantSearchRequest;
import com.zbkj.common.response.MerchantHeaderNumResponse;
import com.zbkj.common.response.MerchantPageResponse;
import com.zbkj.common.response.MerchantPlatformDetailResponse;

import java.util.List;

public interface CircleMerchantManageService {

    /**
     * 圈层下商户分页列表
     * @param searchRequest 搜索参数
     * @param pageParamRequest 分页参数
     * @return 分页数据
     */
    PageInfo<MerchantPageResponse> getCircleMerchantPage(MerchantSearchRequest searchRequest, PageParamRequest pageParamRequest);

    /**
     * 圈层下商户数量统计
     * @param searchRequest 搜索参数
     * @return 商户数量统计数据
     */
    MerchantHeaderNumResponse getCircleMerchantNum(MerchantSearchRequest searchRequest);

    /**
     * 圈层下商户详情
     * @param id 商户ID
     * @return 商户详情数据
     */
    MerchantPlatformDetailResponse getCircleMerchantDetail(Integer id);

    /**
     * 获取商户类型列表
     */
    List<MerchantType> allList();

    /**
     * 获取商户分类列表
     *
     * @return 商户分类列表数据
     */
    List<MerchantCategory> categoryList();
}
