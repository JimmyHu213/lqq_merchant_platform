package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.request.LotteryActivityRequest;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.LotteryActivityDao;
import com.zbkj.service.service.LotteryActivityService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 抽奖活动 Service 实现
 * [LQQ-迁移] 抽奖系统
 */
@Service
public class LotteryActivityServiceImpl extends ServiceImpl<LotteryActivityDao, LotteryActivity>
        implements LotteryActivityService {

    @Resource
    private LotteryActivityDao dao;

    @Override
    public PageInfo<LotteryActivity> getActiveList(PageParamRequest pageParamRequest) {
        Page<LotteryActivity> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getStatus, 1);
        lqw.eq(LotteryActivity::getAuditStatus, 1); // [LQQ-迁移] 用户端只显示审核通过的
        lqw.eq(LotteryActivity::getIsDel, false);
        lqw.orderByDesc(LotteryActivity::getId);
        List<LotteryActivity> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    @Override
    public LotteryActivity getByIdException(Integer id) {
        LotteryActivity activity = dao.selectById(id);
        if (ObjectUtil.isNull(activity) || activity.getIsDel()) {
            throw new CrmebException("抽奖活动不存在");
        }
        return activity;
    }

    @Override
    public List<LotteryActivity> getAllActiveActivities() {
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getStatus, 1);
        lqw.eq(LotteryActivity::getAuditStatus, 1); // [LQQ-迁移] 只开奖审核通过的活动
        lqw.eq(LotteryActivity::getIsDel, false);
        return dao.selectList(lqw);
    }

    // ==================== 商户端 ====================

    @Override
    public Boolean createActivity(LotteryActivityRequest request, Integer merId) {
        if (request.getWinnerCount() > request.getParticipantThreshold()) {
            throw new CrmebException("中奖人数不能超过开奖所需人数");
        }
        LotteryActivity activity = new LotteryActivity();
        activity.setMerId(merId);
        activity.setName(request.getName());
        activity.setImage(request.getImage());
        activity.setDescription(request.getDescription());
        activity.setPointsCost(request.getPointsCost());
        activity.setParticipantThreshold(request.getParticipantThreshold());
        activity.setWinnerCount(request.getWinnerCount());
        activity.setPrizeName(request.getPrizeName());
        activity.setPrizeValue(request.getPrizeValue());
        activity.setStatus(0); // 默认关闭，审核通过后商户手动开启
        activity.setAuditStatus(0); // 待审核
        activity.setCurrentPeriod(1);
        activity.setIsDel(false);
        activity.setCreateTime(DateUtil.date());
        activity.setUpdateTime(DateUtil.date());
        return save(activity);
    }

    @Override
    public Boolean updateActivity(LotteryActivityRequest request, Integer merId) {
        if (ObjectUtil.isNull(request.getId())) {
            throw new CrmebException("活动ID不能为空");
        }
        LotteryActivity activity = getByIdException(request.getId());
        if (!activity.getMerId().equals(merId)) {
            throw new CrmebException("无权操作此活动");
        }
        if (request.getWinnerCount() > request.getParticipantThreshold()) {
            throw new CrmebException("中奖人数不能超过开奖所需人数");
        }
        activity.setName(request.getName());
        activity.setImage(request.getImage());
        activity.setDescription(request.getDescription());
        activity.setPointsCost(request.getPointsCost());
        activity.setParticipantThreshold(request.getParticipantThreshold());
        activity.setWinnerCount(request.getWinnerCount());
        activity.setPrizeName(request.getPrizeName());
        activity.setPrizeValue(request.getPrizeValue());
        activity.setAuditStatus(0); // 编辑后需重新审核
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }

    @Override
    public Boolean switchActivity(Integer id, Integer merId) {
        LotteryActivity activity = getByIdException(id);
        if (!activity.getMerId().equals(merId)) {
            throw new CrmebException("无权操作此活动");
        }
        if (!activity.getAuditStatus().equals(1)) {
            throw new CrmebException("活动未通过审核，无法开启");
        }
        activity.setStatus(activity.getStatus().equals(1) ? 0 : 1);
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }

    @Override
    public Boolean deleteActivity(Integer id, Integer merId) {
        LotteryActivity activity = getByIdException(id);
        if (!activity.getMerId().equals(merId)) {
            throw new CrmebException("无权操作此活动");
        }
        activity.setIsDel(true);
        activity.setStatus(0);
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }

    @Override
    public PageInfo<LotteryActivity> getMerchantList(Integer merId, PageParamRequest pageParamRequest) {
        Page<LotteryActivity> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getMerId, merId);
        lqw.eq(LotteryActivity::getIsDel, false);
        lqw.orderByDesc(LotteryActivity::getId);
        List<LotteryActivity> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    // ==================== 平台端 ====================

    @Override
    public PageInfo<LotteryActivity> getPlatformList(PageParamRequest pageParamRequest) {
        Page<LotteryActivity> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryActivity> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryActivity::getIsDel, false);
        lqw.orderByDesc(LotteryActivity::getId);
        List<LotteryActivity> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    @Override
    public Boolean auditActivity(Integer activityId, Integer auditStatus, String reason) {
        LotteryActivity activity = getByIdException(activityId);
        if (!activity.getAuditStatus().equals(0)) {
            throw new CrmebException("该活动不在待审核状态");
        }
        activity.setAuditStatus(auditStatus);
        if (auditStatus.equals(2)) {
            activity.setAuditReason(reason);
        }
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }

    @Override
    public Boolean forceCloseActivity(Integer id) {
        LotteryActivity activity = getByIdException(id);
        activity.setStatus(0);
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }

    @Override
    public Boolean platformDeleteActivity(Integer id) {
        LotteryActivity activity = getByIdException(id);
        activity.setIsDel(true);
        activity.setStatus(0);
        activity.setUpdateTime(DateUtil.date());
        return updateById(activity);
    }
}
