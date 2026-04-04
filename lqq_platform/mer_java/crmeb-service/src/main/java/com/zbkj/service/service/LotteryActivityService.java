package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
 * 抽奖活动 Service
 * [LQQ-迁移] 抽奖系统
 */
public interface LotteryActivityService extends IService<LotteryActivity> {

    /**
     * 获取开启中的活动列表（用户端）
     */
    PageInfo<LotteryActivity> getActiveList(PageParamRequest pageParamRequest);

    /**
     * 获取活动详情
     */
    LotteryActivity getByIdException(Integer id);

    /**
     * 获取所有开启中的活动（定时任务用）
     */
    List<LotteryActivity> getAllActiveActivities();
}
