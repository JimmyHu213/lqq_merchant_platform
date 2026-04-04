package com.zbkj.service.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.circle.CircleMerchant;

import java.util.List;

/**
* @author zzp
* @description CircleMerchantService 接口
* @date 2025-08-04
*/
public interface CircleMerchantService extends IService<CircleMerchant> {


    /**
     * 根据圈层id集合获取商户id列表
     *
     * @param circleIdList 圈层id集合
     * @return 商户id列表
     */
    List<Integer> getMerIdListByCirCleIds(List<Integer> circleIdList);

    /**
     * 根据商户id集合和日期获取商户数量
     *
     * @param merIds 商户id集合
     * @param date 日期，格式为yyyy-MM-dd
     * @return 商户数量
     */
    Integer getMerchantNumByMerIdsAndDate(List<Integer> merIds, String date);

    /**
     * 根据圈层id获取该圈层下绑定商户id列表
     *
     * @param circleId 圈层id
     * @return 商户id列表
     */
    List<Integer> getMerIdsByCircleId(Integer circleId);

    /**
     * 根据圈层id和商户id列表解绑商户
     *
     * @param circleIds 圈层id列表
     * @param merchantIds 商户id列表
     * @return 解绑结果
     */
    Boolean removeMer(List<Integer> circleIds, List<Integer> merchantIds);

    /**
     * 根据圈层id集合获取绑定关系列表
     *
     * @param circleIds 圈层id集合
     * @return 绑定关系列表
     */
    List<CircleMerchant> getByCircleIds(List<Integer> circleIds);
}