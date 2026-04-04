package com.zbkj.admin.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleMerchantManageService;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.MerchantCategory;
import com.zbkj.common.model.merchant.MerchantType;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.common.request.merchant.MerchantSearchRequest;
import com.zbkj.common.response.MerchantHeaderNumResponse;
import com.zbkj.common.response.MerchantPageResponse;
import com.zbkj.common.response.MerchantPlatformDetailResponse;
import com.zbkj.service.service.MerchantCategoryService;
import com.zbkj.service.service.MerchantService;
import com.zbkj.service.service.MerchantTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CircleMerchantManageServiceImpl implements CircleMerchantManageService {

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantTypeService typeService;
    @Autowired
    private MerchantCategoryService categoryService;
    @Autowired
    private CircleHomeService circleHomeService;

    /**
     * 圈层下商户分页列表
     * @param searchRequest 搜索参数
     * @param pageParamRequest 分页参数
     * @return 分页数据
     */
    @Override
    public PageInfo<MerchantPageResponse> getCircleMerchantPage(MerchantSearchRequest searchRequest, PageParamRequest pageParamRequest) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new PageInfo<>(new ArrayList<>());
        }
        return merchantService.getCircleMerchantPage(merIdList, searchRequest, pageParamRequest);
    }

    /**
     * 圈层下商户数量统计
     * @param searchRequest 搜索参数
     * @return 商户数量统计数据
     */
    @Override
    public MerchantHeaderNumResponse getCircleMerchantNum(MerchantSearchRequest searchRequest) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList)) {
            return new MerchantHeaderNumResponse(0, 0);
        }
        return merchantService.getCircleMerchantNum(merIdList, searchRequest);
    }

    /**
     * 圈层下商户详情
     * @param id 商户ID
     * @return 商户详情数据
     */
    @Override
    public MerchantPlatformDetailResponse getCircleMerchantDetail(Integer id) {
        List<Integer> merIdList = circleHomeService.getMerIds();
        if (CollUtil.isEmpty(merIdList) || !merIdList.contains(id)) {
            throw new CrmebException("该商户不存在于该圈层");
        }
        return merchantService.getPlatformDetail(id);
    }

    /**
     * 获取商户类型列表
     * @return 商户类型列表数据
     */
    @Override
    public List<MerchantType> allList() {
        return typeService.allList();
    }

    /**
     * 获取商户分类列表
     *
     * @return 商户分类列表数据
     */
    @Override
    public List<MerchantCategory> categoryList() {
        return categoryService.allList();
    }
}
