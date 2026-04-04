package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.reservation.ProductSupplementReservation;

/**
 * @author HZW
 * @description ProductSupplementReservationService 接口
 * @date 2025-07-08
 */
public interface ProductSupplementReservationService extends IService<ProductSupplementReservation> {

    /**
     * 通过商品ID获取预约补充信息
     * @param proId 商品ID
     * @return 预约补充信息
     */
    ProductSupplementReservation getByProId(Integer proId);

}