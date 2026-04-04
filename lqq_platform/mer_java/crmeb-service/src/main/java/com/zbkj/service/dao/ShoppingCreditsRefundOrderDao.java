package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.merchant.ShoppingCreditsRefundOrder;
import com.zbkj.common.response.ShoppingCreditsRefundOrderPageResponse;

import java.util.HashMap;
import java.util.List;

/**
 * <p>
 * 购物金退款单表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2026-01-08
 */
public interface ShoppingCreditsRefundOrderDao extends BaseMapper<ShoppingCreditsRefundOrder> {

    List<ShoppingCreditsRefundOrderPageResponse> findPageByMerchant(HashMap<String, Object> map);
}
