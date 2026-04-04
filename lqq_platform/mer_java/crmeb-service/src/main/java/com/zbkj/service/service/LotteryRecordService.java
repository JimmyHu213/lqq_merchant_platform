package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.request.PageParamRequest;

/**
 * 抽奖参与记录 Service
 * [LQQ-迁移] 抽奖系统
 */
public interface LotteryRecordService extends IService<LotteryRecord> {

    /**
     * 参与抽奖（扣积分 + 创建记录）
     * @param activityId 活动ID
     * @return Boolean
     */
    Boolean participate(Integer activityId);

    /**
     * 获取我的抽奖记录
     */
    PageInfo<LotteryRecord> getMyRecords(PageParamRequest pageParamRequest);

    /**
     * 获取活动当前期参与人数
     */
    Integer getCurrentPeriodCount(Integer activityId, String periodNumber);

    /**
     * 执行开奖（定时任务调用）
     */
    void executeDraw();

    /**
     * 商户核销兑奖
     * @param recordId 记录ID
     * @param merId 商户ID
     */
    Boolean redeemPrize(Integer recordId, Integer merId);
}
