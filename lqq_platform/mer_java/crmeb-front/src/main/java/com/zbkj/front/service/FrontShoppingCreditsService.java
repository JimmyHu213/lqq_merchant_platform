package com.zbkj.front.service;

import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.merchant.ShoppingCreditsOrder;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.response.OrderPayResultResponse;
import com.zbkj.front.request.PayShoppingCreditsPackageRecordRequest;
import com.zbkj.front.request.PayShoppingCreditsPackageRequest;
import com.zbkj.front.request.ShoppingCreditsOrderRefundApplyRequest;
import com.zbkj.front.request.UserShoppingCreditsRecordPageRequest;
import com.zbkj.front.response.MerchantMemberShoppingCreditsAssetResponse;
import com.zbkj.front.response.UserShoppingCreditsMonthRecordResponse;

import java.util.List;

/**
 * 购物金服务类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/15
 */
public interface FrontShoppingCreditsService {

    /**
     * 获取购物金套餐列表
     */
    List<ShoppingCreditsPackage> findPackageList(Integer merId);

    /**
     * 获取商户会员购物金资产
     */
    MerchantMemberShoppingCreditsAssetResponse getMerchantMemberAsset(Integer merId);

    /**
     * 购物金套餐列表-根据id集合加载
     */
    List<ShoppingCreditsPackage> findPackageListByIds(String ids);

    /**
     * 购买购物金套餐
     */
    OrderPayResultResponse payShoppingCreditsPackage(PayShoppingCreditsPackageRequest request);

    /**
     * 购物金购买记录
     *y
     */
    PageInfo<ShoppingCreditsOrder> payShoppingCreditsPackageRecord(PayShoppingCreditsPackageRecordRequest request);

    /**
     * 用户购物金记录分页列表
     */
    PageInfo<UserShoppingCreditsMonthRecordResponse> userShoppingCreditsPageRecord(UserShoppingCreditsRecordPageRequest request);

    /**
     * 购物金订单退款申请
     */
    Boolean orderRefundApply(ShoppingCreditsOrderRefundApplyRequest request);

    /**
     * 购物金退款订单分页列表
     */
    PageInfo<ShoppingCreditsRefundOrder> shoppingCreditsRefundOrderPage(PayShoppingCreditsPackageRecordRequest request);
}
