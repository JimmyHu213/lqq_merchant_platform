package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.reservation.ProductAttrValueSupplementReservation;

import java.util.List;

/**
 * @author HZW
 * @description ProductAttrValueSupplementReservationService 接口
 * @date 2025-07-08
 */
public interface ProductAttrValueSupplementReservationService extends IService<ProductAttrValueSupplementReservation> {

    /**
     * 删除预约商品规格值信息
     * @param proId 商品id
     * @return 删除结果
     */
    Boolean deleteByProductId(Integer proId);


    /**
     * 查询预约商品规格值信息
     * @param proId 商品id
     * @param attValueId 规格值id
     * @return List<ProductAttrValueSupplementReservation> 预约商品规格值信息列表
     */
    List<ProductAttrValueSupplementReservation> getByProIdAndAttValueId(Integer proId, Integer attValueId);
    /**
     * 获取sku对应时段列表
     *
     * @param skuId skuID
     */
    List<ProductAttrValueSupplementReservation> findBySkuId(Integer skuId);
}