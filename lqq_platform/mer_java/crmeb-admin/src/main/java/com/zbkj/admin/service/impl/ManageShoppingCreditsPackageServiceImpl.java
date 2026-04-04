package com.zbkj.admin.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zbkj.admin.request.ShoppingCreditsPackageSaveRequest;
import com.zbkj.admin.service.ManageShoppingCreditsPackageService;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.admin.SystemAdmin;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.service.service.ShoppingCreditsPackageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端购物金套餐服务实现类
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/4
 */
@Service
public class ManageShoppingCreditsPackageServiceImpl implements ManageShoppingCreditsPackageService {

    @Autowired
    private ShoppingCreditsPackageService shoppingCreditsPackageService;

    /**
     * 购物金套餐列表
     */
    @Override
    public List<ShoppingCreditsPackage> getList() {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        return shoppingCreditsPackageService.findList(admin.getMerId(), null);
    }

    /**
     * 购物金套餐添加
     */
    @Override
    public Boolean add(ShoppingCreditsPackageSaveRequest request) {
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsPackage shoppingCreditsPackage = new ShoppingCreditsPackage();
        shoppingCreditsPackage.setMerId(admin.getMerId());
        shoppingCreditsPackage.setRechargeAmount(request.getRechargeAmount());
        shoppingCreditsPackage.setGiftAmount(request.getGiftAmount());
        shoppingCreditsPackage.setSort(request.getSort());
        shoppingCreditsPackage.setShowStatus(request.getShowStatus());
        return shoppingCreditsPackageService.save(shoppingCreditsPackage);
    }

    /**
     * 购物金套餐编辑
     */
    @Override
    public Boolean updatePackage(ShoppingCreditsPackageSaveRequest request) {
        validationPackageId(request.getId());
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsPackage shoppingCreditsPackage = shoppingCreditsPackageService.getByIdException(request.getId());
        if (!shoppingCreditsPackage.getMerId().equals(admin.getMerId()))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐不存在");

        shoppingCreditsPackage.setRechargeAmount(request.getRechargeAmount());
        shoppingCreditsPackage.setGiftAmount(request.getGiftAmount());
        shoppingCreditsPackage.setSort(request.getSort());
        shoppingCreditsPackage.setShowStatus(request.getShowStatus());
        shoppingCreditsPackage.setUpdateTime(DateUtil.date());
        return shoppingCreditsPackageService.updateById(shoppingCreditsPackage);
    }

    /**
     * 购物金套餐删除
     *
     * @param id 套餐ID
     */
    @Override
    public Boolean delete(Integer id) {
        validationPackageId(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsPackage shoppingCreditsPackage = shoppingCreditsPackageService.getByIdException(id);
        if (!shoppingCreditsPackage.getMerId().equals(admin.getMerId()))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐不存在");
        return shoppingCreditsPackageService.removeById(id);
    }

    /**
     * 购物金套餐显示状态变更
     * @param id 套餐ID
     */
    @Override
    public Boolean updateShowStatus(Integer id) {
        validationPackageId(id);
        SystemAdmin admin = SecurityUtil.getLoginUserVo().getUser();
        ShoppingCreditsPackage shoppingCreditsPackage = shoppingCreditsPackageService.getByIdException(id);
        if (!shoppingCreditsPackage.getMerId().equals(admin.getMerId()))
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐不存在");
        return shoppingCreditsPackageService.updateShowStatus(shoppingCreditsPackage.getId(), shoppingCreditsPackage.getShowStatus().equals(1) ? 0 : 1);
    }

    private void validationPackageId(Integer id) {
        if (ObjectUtil.isNull(id) || id.equals(0)) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "购物金套餐ID不能为空");
        }
    }
}
