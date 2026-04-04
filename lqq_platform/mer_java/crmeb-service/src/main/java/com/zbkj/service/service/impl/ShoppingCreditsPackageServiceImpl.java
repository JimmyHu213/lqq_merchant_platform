package com.zbkj.service.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.ShoppingCreditsPackageDao;
import com.zbkj.service.service.ShoppingCreditsPackageService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author HZW
 * @description ShoppingCreditsPackageServiceImpl 接口实现
 * @date 2025-12-04
 */
@Service
public class ShoppingCreditsPackageServiceImpl extends ServiceImpl<ShoppingCreditsPackageDao, ShoppingCreditsPackage> implements ShoppingCreditsPackageService {

    @Resource
    private ShoppingCreditsPackageDao dao;

    /**
     * 购物金套餐列表
     */
    @Override
    public List<ShoppingCreditsPackage> findList(Integer merId, Integer showStatus) {
        LambdaQueryWrapper<ShoppingCreditsPackage> lqw = Wrappers.lambdaQuery();
        lqw.eq(ShoppingCreditsPackage::getMerId, merId);
        if (ObjectUtil.isNotNull(showStatus)) {
            lqw.eq(ShoppingCreditsPackage::getShowStatus, 1);
        }
        lqw.orderByDesc(ShoppingCreditsPackage::getSort, ShoppingCreditsPackage::getId);
        return dao.selectList(lqw);
    }

    @Override
    public ShoppingCreditsPackage getByIdException(Integer id) {
        ShoppingCreditsPackage shoppingCreditsPackage = getById(id);
        if (ObjectUtil.isNull(shoppingCreditsPackage))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐不存在");
        return shoppingCreditsPackage;
    }

    /**
     * 购物金套餐显示状态变更
     *
     * @param id 套餐ID
     */
    @Override
    public Boolean updateShowStatus(Integer id, Integer showStatus) {
        LambdaUpdateWrapper<ShoppingCreditsPackage> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(ShoppingCreditsPackage::getShowStatus, showStatus);
        wrapper.eq(ShoppingCreditsPackage::getId, id);
        return update(wrapper);
    }

    /**
     * 购物金套餐列表-根据id列表
     */
    @Override
    public List<ShoppingCreditsPackage> findByIdList(List<Integer> idList) {
        LambdaQueryWrapper<ShoppingCreditsPackage> lqw = Wrappers.lambdaQuery();
        lqw.in(ShoppingCreditsPackage::getId, idList);
        lqw.eq(ShoppingCreditsPackage::getShowStatus, 1);
        lqw.orderByDesc(ShoppingCreditsPackage::getSort, ShoppingCreditsPackage::getId);
        return dao.selectList(lqw);
    }
}

