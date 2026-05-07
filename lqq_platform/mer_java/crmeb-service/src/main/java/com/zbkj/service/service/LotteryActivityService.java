package com.zbkj.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.request.LotteryActivityRequest;
import com.zbkj.common.request.PageParamRequest;

import java.util.List;

/**
 * 抽奖活动 Service
 * [LQQ-迁移] 抽奖系统
 */
public interface LotteryActivityService extends IService<LotteryActivity> {

    /**
     * 获取开启中且审核通过的活动列表（用户端）
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

    // [LQQ-迁移] 商户端 CRUD
    /**
     * 商户创建活动
     */
    Boolean createActivity(LotteryActivityRequest request, Integer merId);

    /**
     * 商户编辑活动
     */
    Boolean updateActivity(LotteryActivityRequest request, Integer merId);

    /**
     * 商户开关活动
     */
    Boolean switchActivity(Integer id, Integer merId);

    /**
     * 商户删除活动（软删除）
     */
    Boolean deleteActivity(Integer id, Integer merId);

    /**
     * 商户活动列表
     */
    PageInfo<LotteryActivity> getMerchantList(Integer merId, PageParamRequest pageParamRequest);

    // [LQQ-迁移] 平台端管理
    /**
     * 平台活动列表（全部）
     */
    PageInfo<LotteryActivity> getPlatformList(PageParamRequest pageParamRequest);

    /**
     * 平台审核活动
     */
    Boolean auditActivity(Integer activityId, Integer auditStatus, String reason);

    /**
     * 平台强制关闭活动
     */
    Boolean forceCloseActivity(Integer id);

    /**
     * 平台删除活动
     */
    Boolean platformDeleteActivity(Integer id);
}
