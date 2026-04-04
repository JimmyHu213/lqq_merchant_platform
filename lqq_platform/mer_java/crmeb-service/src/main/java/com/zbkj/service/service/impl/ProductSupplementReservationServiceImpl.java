package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.reservation.ProductSupplementReservation;
import com.zbkj.service.dao.reservation.ProductSupplementReservationDao;
import com.zbkj.service.service.ProductSupplementReservationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author HZW
 * @description ProductSupplementReservationServiceImpl 接口实现
 * @date 2025-07-08
 */
@Service
public class ProductSupplementReservationServiceImpl extends ServiceImpl<ProductSupplementReservationDao, ProductSupplementReservation> implements ProductSupplementReservationService {

    @Resource
    private ProductSupplementReservationDao dao;

    /**
     * 通过商品ID获取预约补充信息
     * @param proId 商品ID
     * @return 预约补充信息
     */
    @Override
    public ProductSupplementReservation getByProId(Integer proId) {
        LambdaQueryWrapper<ProductSupplementReservation> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductSupplementReservation::getProId, proId);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }
}

