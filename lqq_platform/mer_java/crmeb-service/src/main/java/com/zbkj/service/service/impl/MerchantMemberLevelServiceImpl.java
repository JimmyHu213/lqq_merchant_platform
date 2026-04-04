package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.dto.LevelMemberNumDto;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.merchant.MerchantMemberLevel;
import com.zbkj.common.result.CommonResultCode;
import com.zbkj.service.dao.MerchantMemberLevelDao;
import com.zbkj.service.service.MerchantMemberLevelService;
import com.zbkj.service.service.MerchantMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author HZW
 * @description MerchantMemberLevelServiceImpl 接口实现
 * @date 2025-12-01
 */
@Service
public class MerchantMemberLevelServiceImpl extends ServiceImpl<MerchantMemberLevelDao, MerchantMemberLevel> implements MerchantMemberLevelService {

    @Resource
    private MerchantMemberLevelDao dao;

    @Autowired
    private MerchantMemberService merchantMemberService;

    /**
     * 会员等级列表
     *
     * @param merId 商户ID
     */
    @Override
    public List<MerchantMemberLevel> getList(Integer merId) {
        LambdaQueryWrapper<MerchantMemberLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberLevel::getMerId, merId);
        lqw.eq(MerchantMemberLevel::getIsDel, 0);
        lqw.orderByAsc(MerchantMemberLevel::getLevel);
        return dao.selectList(lqw);
    }

    /**
     * 获取最后一级会员等级
     * @param merId 商户ID
     */
    @Override
    public MerchantMemberLevel getLastLevelByMerId(Integer merId) {
        LambdaQueryWrapper<MerchantMemberLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberLevel::getMerId, merId);
        lqw.eq(MerchantMemberLevel::getIsDel, 0);
        lqw.orderByDesc(MerchantMemberLevel::getLevel);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    @Override
    public MerchantMemberLevel getByIdException(Integer id) {
        MerchantMemberLevel merchantMemberLevel = getById(id);
        if (ObjectUtil.isNull(merchantMemberLevel) || merchantMemberLevel.getIsDel()) {
            throw new CrmebException(CommonResultCode.VALIDATE_FAILED, "商户会员等级不存在");
        }
        return merchantMemberLevel;
    }

    /**
     * 等级名称是否存在
     * @param name 会员等级名称
     * @param merId 商户ID
     */
    @Override
    public Boolean isExistName(String name, Integer merId) {
        LambdaQueryWrapper<MerchantMemberLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberLevel::getName, name);
        lqw.eq(MerchantMemberLevel::getMerId, merId);
        lqw.eq(MerchantMemberLevel::getIsDel, 0);
        List<MerchantMemberLevel> list = dao.selectList(lqw);
        return CollUtil.isNotEmpty(list) && list.size() > 0;
    }

    /**
     * 获取会员等级
     * @param level 等级
     * @param merId 商户ID
     */
    @Override
    public MerchantMemberLevel getByLevel(Integer level, Integer merId) {
        LambdaQueryWrapper<MerchantMemberLevel> lqw = Wrappers.lambdaQuery();
        lqw.eq(MerchantMemberLevel::getLevel, level);
        lqw.eq(MerchantMemberLevel::getMerId, merId);
        lqw.eq(MerchantMemberLevel::getIsDel, 0);
        lqw.last(" limit 1");
        return dao.selectOne(lqw);
    }

    /**
     * 获取会员人数Map
     */
    @Override
    public Map<Integer, Integer> getMembershipMap(List<Integer> levelList, Integer merId) {
        Map<Integer, Integer> membershipMap = new HashMap<>();
        for (Integer level : levelList) {
            Integer count = merchantMemberService.getCountByLevel(level, merId);
            membershipMap.put(level, count);
        }
        return membershipMap;
    }

    /**
     * 获取商会会员等级商户数量列表
     */
    @Override
    public List<LevelMemberNumDto> findMerchantNumListByMerId(Integer merId) {
        return dao.findMerchantNumListByMerId(merId);
    }
}

