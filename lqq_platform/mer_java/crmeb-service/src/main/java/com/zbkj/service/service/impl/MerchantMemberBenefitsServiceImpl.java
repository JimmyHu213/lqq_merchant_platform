package com.zbkj.service.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.merchant.MerchantMemberBenefits;
import com.zbkj.service.dao.MerchantMemberBenefitsDao;
import com.zbkj.service.service.MerchantMemberBenefitsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description MerchantMemberBenefitsServiceImpl 接口实现
 * @date 2025-12-01
 */
@Service
public class MerchantMemberBenefitsServiceImpl extends ServiceImpl<MerchantMemberBenefitsDao, MerchantMemberBenefits> implements MerchantMemberBenefitsService {

    @Resource
    private MerchantMemberBenefitsDao dao;

    /**
     * 获取会员权益列表
     *
     * @param merId 商户ID
     */
    @Override
    public List<MerchantMemberBenefits> getList(Integer merId) {
        LambdaQueryWrapper<MerchantMemberBenefits> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberBenefits::getMerId, merId);
        lqw.eq(MerchantMemberBenefits::getIsDel, 0);
        lqw.orderByAsc(MerchantMemberBenefits::getSort);
        return dao.selectList(lqw);
    }

    /**
     * 获取会员权益预置数据
     * @param merId 商户ID
     */
    @Override
    public List<MerchantMemberBenefits> getPresetData(Integer merId) {
        LambdaQueryWrapper<MerchantMemberBenefits> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberBenefits::getMerId, merId);
        lqw.eq(MerchantMemberBenefits::getCanDel, 0);
        return dao.selectList(lqw);
    }

    /**
     * 删除会员权益
     * @param merId 商户ID
     */
    @Override
    public Boolean deleteByMerId(Integer merId) {
        LambdaUpdateWrapper<MerchantMemberBenefits> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(MerchantMemberBenefits::getIsDel, 1);
        wrapper.eq(MerchantMemberBenefits::getMerId, merId);
        wrapper.eq(MerchantMemberBenefits::getIsDel, 0);
        wrapper.eq(MerchantMemberBenefits::getCanDel, 1);
        return update(wrapper);
    }

    /**
     * 获取权益列表
     * @param benefitsIdList 权益ID列表
     * @param merId 商户ID
     */
    @Override
    public List<MerchantMemberBenefits> findListByIdList(List<Integer> benefitsIdList, Integer merId) {
        LambdaQueryWrapper<MerchantMemberBenefits> lqw = Wrappers.lambdaQuery();
        lqw.in(MerchantMemberBenefits::getId, benefitsIdList);
        lqw.eq(MerchantMemberBenefits::getMerId, merId);
        lqw.eq(MerchantMemberBenefits::getIsDel, 0);
        lqw.orderByAsc(MerchantMemberBenefits::getSort);
        return dao.selectList(lqw);
    }
}

