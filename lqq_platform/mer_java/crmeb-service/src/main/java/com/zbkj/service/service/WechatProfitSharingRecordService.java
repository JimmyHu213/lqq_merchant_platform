package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zbkj.common.model.order.WechatProfitSharingRecord;

import java.util.List;

/**
 * [LQQ-迁移] 微信多方分账记录 Service 接口
 */
public interface WechatProfitSharingRecordService extends IService<WechatProfitSharingRecord> {

    /**
     * 根据平台订单号获取分账记录
     */
    List<WechatProfitSharingRecord> getByOrderNo(String orderNo);

    /**
     * 获取待分账记录(status=0)
     */
    List<WechatProfitSharingRecord> getPendingRecords(int limit);

    /**
     * 根据商户ID获取分账记录
     */
    List<WechatProfitSharingRecord> getByMerId(Integer merId, String date);

    /**
     * [LQQ-迁移] 根据订单号+商户ID获取分账记录（商户数据隔离）
     */
    List<WechatProfitSharingRecord> getByOrderNoAndMerId(String orderNo, Integer merId);

    /**
     * [LQQ-迁移] 获取可解冻的分账记录（status=1, frozen_until<=NOW(), is_unfrozen=0）
     */
    List<WechatProfitSharingRecord> getUnfreezeReadyRecords(int limit);
}
