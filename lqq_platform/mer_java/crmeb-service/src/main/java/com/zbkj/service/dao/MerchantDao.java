package com.zbkj.service.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zbkj.common.model.merchant.Merchant;
import com.zbkj.common.response.circle.CircleMerchantResponse;

import java.util.List;

/**
 * <p>
 * 商户表 Mapper 接口
 * </p>
 *
 * @author HZW
 * @since 2022-07-20
 */
public interface MerchantDao extends BaseMapper<Merchant> {

    List<CircleMerchantResponse> selectCircleMerchantsUnBind(Integer circleId);
}
