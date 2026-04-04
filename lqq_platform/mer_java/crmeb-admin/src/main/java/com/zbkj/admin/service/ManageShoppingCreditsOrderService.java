package com.zbkj.admin.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.admin.request.ShoppingCreditsOrderSearchRequest;
import com.zbkj.common.response.ShoppingCreditsOrderPageResponse;

/**
 * 管理端购物金订单控制器
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/5
 */
public interface ManageShoppingCreditsOrderService {

    /**
     * 购物金订单分页列表
     */
    PageInfo<ShoppingCreditsOrderPageResponse> findPage(ShoppingCreditsOrderSearchRequest request);
}
