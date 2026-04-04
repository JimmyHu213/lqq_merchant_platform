package com.zbkj.admin.service;

import com.zbkj.admin.request.ShoppingCreditsPackageSaveRequest;
import com.zbkj.common.model.merchant.ShoppingCreditsPackage;

import java.util.List;

/**
 * 管理端购物金套餐服务
 *
 * @author Han
 * @version 1.0.0
 * @Date 2025/12/4
 */
public interface ManageShoppingCreditsPackageService {

    /**
     * 购物金套餐列表
     */
    List<ShoppingCreditsPackage> getList();

    /**
     * 购物金套餐添加
     */
    Boolean add(ShoppingCreditsPackageSaveRequest request);

    /**
     * 购物金套餐编辑
     */
    Boolean updatePackage(ShoppingCreditsPackageSaveRequest request);

    /**
     * 购物金套餐删除
     * @param id 套餐ID
     */
    Boolean delete(Integer id);

    /**
     * 购物金套餐显示状态变更
     * @param id 套餐ID
     */
    Boolean updateShowStatus(Integer id);
}
