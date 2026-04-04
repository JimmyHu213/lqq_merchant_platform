package com.zbkj.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.reservation.ProductAttrValueSupplementReservation;
import com.zbkj.service.dao.reservation.ProductAttrValueSupplementReservationDao;
import com.zbkj.service.service.ProductAttrValueSupplementReservationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description ProductAttrValueSupplementReservationServiceImpl 接口实现
 * @date 2025-07-08
 */
@Service
public class ProductAttrValueSupplementReservationServiceImpl extends ServiceImpl<ProductAttrValueSupplementReservationDao, ProductAttrValueSupplementReservation> implements ProductAttrValueSupplementReservationService {

    @Resource
    private ProductAttrValueSupplementReservationDao dao;

    /**
     * 删除预约商品规格值信息
     * @param proId 商品id
     * @return 删除结果
     */
    @Override
    public Boolean deleteByProductId(Integer proId) {
        QueryWrapper<ProductAttrValueSupplementReservation> wrapper = new QueryWrapper<>();
        wrapper.eq("pro_id", proId);
        return remove(wrapper);
    }

    /**
     * 查询预约商品规格值信息
     * @param proId 商品id
     * @param attValueId 规格值id
     * @return List<ProductAttrValueSupplementReservation> 预约商品规格值信息列表
     */
    @Override
    public List<ProductAttrValueSupplementReservation> getByProIdAndAttValueId(Integer proId, Integer attValueId) {
        LambdaQueryWrapper<ProductAttrValueSupplementReservation> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(ProductAttrValueSupplementReservation::getProId, proId);
        queryWrapper.eq(ProductAttrValueSupplementReservation::getAttValueId, attValueId);
        return dao.selectList(queryWrapper);
    }
    /**
     * 获取sku对应时段列表
     *
     * @param skuId skuID
     */
    @Override
    public List<ProductAttrValueSupplementReservation> findBySkuId(Integer skuId) {
        LambdaQueryWrapper<ProductAttrValueSupplementReservation> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProductAttrValueSupplementReservation::getAttValueId, skuId);
        return dao.selectList(lqw);
    }
}

