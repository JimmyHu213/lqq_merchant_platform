package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.dto.LevelMemberNumDto;
import com.zbkj.common.model.merchant.MerchantMemberLevel;

import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description MerchantMemberLevelService 接口
 * @date 2025-12-01
 */
public interface MerchantMemberLevelService extends IService<MerchantMemberLevel> {

    /**
     * 会员等级列表
     *
     * @param merId 商户ID
     */
    List<MerchantMemberLevel> getList(Integer merId);

    /**
     * 获取最后一级会员等级
     * @param merId 商户ID
     */
    MerchantMemberLevel getLastLevelByMerId(Integer merId);

    MerchantMemberLevel getByIdException(Integer id);

    /**
     * 等级名称是否存在
     * @param name 会员等级名称
     * @param merId 商户ID
     */
    Boolean isExistName(String name, Integer merId);

    /**
     * 获取会员等级
     * @param level 等级
     * @param merId 商户ID
     */
    MerchantMemberLevel getByLevel(Integer level, Integer merId);

    /**
     * 获取会员人数Map
     */
    Map<Integer, Integer> getMembershipMap(List<Integer> levelList, Integer merId);

    /**
     * 获取商会会员等级商户数量列表
     */
    List<LevelMemberNumDto> findMerchantNumListByMerId(Integer merId);
}