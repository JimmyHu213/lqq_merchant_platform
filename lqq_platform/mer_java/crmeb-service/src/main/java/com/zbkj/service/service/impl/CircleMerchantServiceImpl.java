package com.zbkj.service.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zbkj.common.model.circle.CircleMerchant;
import com.zbkj.common.utils.SecurityUtil;
import com.zbkj.common.vo.LoginUserVo;
import com.zbkj.service.dao.circle.CircleMerchantDao;
import com.zbkj.service.service.CircleMerchantService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author zzp
* @description CircleMerchantServiceImpl 接口实现
* @date 2025-08-04
*/
@Service
public class CircleMerchantServiceImpl extends ServiceImpl<CircleMerchantDao, CircleMerchant> implements CircleMerchantService {

    @Resource
    private CircleMerchantDao dao;

    /**
     * 根据圈层id集合获取商户id列表
     *
     * @param circleIdList 圈层id集合
     * @return 商户id列表
     */
    @Override
    public List<Integer> getMerIdListByCirCleIds(List<Integer> circleIdList) {
        LambdaQueryWrapper<CircleMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.select(CircleMerchant::getMerId);
        wrapper.in(CircleMerchant::getCircleId, circleIdList);
        List<CircleMerchant> list = dao.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        return list.stream().map(CircleMerchant::getMerId).distinct().collect(Collectors.toList());
    }

    /**
     * 根据商户id集合和日期获取商户数量
     *
     * @param merIds 商户id集合
     * @param date 日期，格式为yyyy-MM-dd
     * @return 商户数量
     */
    @Override
    public Integer getMerchantNumByMerIdsAndDate(List<Integer> merIds, String date) {
        LoginUserVo loginUserVo = SecurityUtil.getLoginUserVo();
        Integer circleId = loginUserVo.getUser().getCircleId();
        LambdaQueryWrapper<CircleMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CircleMerchant::getMerId, merIds);
        wrapper.eq(CircleMerchant::getCircleId, circleId);
        // 这里需要根据实际数据库表结构调整查询条件，例如可能需要加上日期范围的筛选等。
        wrapper.apply("DATE_FORMAT(create_time,'%Y-%m-%d') = {0}", date);
        return dao.selectCount(wrapper);
    }

    /**
     * 根据圈层id获取该圈层下绑定商户id列表
     *
     * @param circleId 圈层id
     * @return 商户id列表
     */
    @Override
    public List<Integer> getMerIdsByCircleId(Integer circleId) {
        LambdaQueryWrapper<CircleMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CircleMerchant::getCircleId, circleId);
        List<CircleMerchant> list = dao.selectList(wrapper);
        if (CollUtil.isEmpty(list)) {
            return CollUtil.newArrayList();
        }
        return list.stream().map(CircleMerchant::getMerId).collect(Collectors.toList());
    }

    @Override
    public Boolean removeMer(List<Integer> circleIds, List<Integer> merchantIds) {
        LambdaQueryWrapper<CircleMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CircleMerchant::getCircleId, circleIds);
        wrapper.in(CircleMerchant::getMerId, merchantIds);
        remove(wrapper);
        return Boolean.TRUE;
    }

    /**
     * 根据圈层id集合获取绑定关系列表
     *
     * @param circleIds 圈层id集合
     * @return 绑定关系列表
     */
    @Override
    public List<CircleMerchant> getByCircleIds(List<Integer> circleIds) {
        LambdaQueryWrapper<CircleMerchant> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(CircleMerchant::getCircleId, circleIds);
        return dao.selectList(wrapper);
    }
}

