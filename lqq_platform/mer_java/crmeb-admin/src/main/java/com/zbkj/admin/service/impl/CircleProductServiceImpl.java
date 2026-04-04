package com.zbkj.admin.service.impl;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.service.CircleHomeService;
import com.zbkj.admin.service.CircleProductService;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.request.PlatProductSearchRequest;
import com.zbkj.common.request.PlatProductTabsHeaderRequest;
import com.zbkj.common.response.PlatformProductListResponse;
import com.zbkj.common.response.ProductInfoResponse;
import com.zbkj.common.response.ProductTabsHeaderResponse;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CircleProductServiceImpl implements CircleProductService {

    @Autowired
    private ProductService productService;
    @Autowired
    private CircleHomeService circleHomeService;



    /**
     * 圈层端查询商品列表
     * @param request 查询条件
     * @return 商品列表分页信息
     */
    @Override
    public PageInfo<PlatformProductListResponse> getAdminList(PlatProductSearchRequest request) {
        List<Integer> merIds = circleHomeService.getMerIds();
        return productService.getCircleProductList(request, merIds);
    }

    /**
     * 获取商品表头数量
     */
    @Override
    public List<ProductTabsHeaderResponse> getTabsHeader(PlatProductTabsHeaderRequest request) {
        List<Integer> merIds = circleHomeService.getMerIds();
        return productService.getCircleProductTabsHeader(request, merIds);
    }

    @Override
    public ProductInfoResponse getInfo(Integer id) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return productService.getInfo(id, admin);
    }
}
