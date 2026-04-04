package com.zbkj.service.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.exception.CrmebException;
import com.zbkj.common.model.lottery.LotteryActivity;
import com.zbkj.common.model.lottery.LotteryRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.request.PageParamRequest;
import com.zbkj.service.dao.LotteryRecordDao;
import com.zbkj.service.service.LotteryActivityService;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 抽奖参与记录 Service 实现
 * [LQQ-迁移] 抽奖系统
 */
@Service
public class LotteryRecordServiceImpl extends ServiceImpl<LotteryRecordDao, LotteryRecord>
        implements LotteryRecordService {

    private static final Logger logger = LoggerFactory.getLogger(LotteryRecordServiceImpl.class);

    @Resource
    private LotteryRecordDao dao;

    @Autowired
    private UserService userService;
    @Autowired
    private LotteryActivityService lotteryActivityService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Override
    public Boolean participate(Integer activityId) {
        // 1. 获取当前用户
        Integer uid = userService.getUserIdException();
        User user = userService.getById(uid);
        if (ObjectUtil.isNull(user)) {
            throw new CrmebException("用户不存在");
        }

        // 2. 验证活动存在且开启
        LotteryActivity activity = lotteryActivityService.getByIdException(activityId);
        if (!activity.getStatus().equals(1)) {
            throw new CrmebException("该活动已关闭");
        }

        // 3. 验证用户积分足够
        if (user.getIntegral() < activity.getPointsCost()) {
            throw new CrmebException("积分不足，需要" + activity.getPointsCost() + "积分");
        }

        // 4. 构建期号
        String periodNumber = activityId + "-" + String.format("%06d", activity.getCurrentPeriod());

        // 5. 检查当前期用户是否已参与
        Integer userCount = dao.selectCount(Wrappers.<LotteryRecord>lambdaQuery()
                .eq(LotteryRecord::getActivityId, activityId)
                .eq(LotteryRecord::getUid, uid)
                .eq(LotteryRecord::getPeriodNumber, periodNumber)
                .isNull(LotteryRecord::getDrawTime));
        if (userCount > 0) {
            throw new CrmebException("本期已参与，请等待开奖");
        }

        // 6. 检查当前期是否已满，满了则进入下期
        Integer currentCount = getCurrentPeriodCount(activityId, periodNumber);
        if (currentCount >= activity.getParticipantThreshold()) {
            // 当前期已满，递增期号
            activity.setCurrentPeriod(activity.getCurrentPeriod() + 1);
            lotteryActivityService.updateById(activity);
            periodNumber = activityId + "-" + String.format("%06d", activity.getCurrentPeriod());
        }

        // 7. 创建参与记录 + 扣减积分（事务）
        LotteryRecord record = new LotteryRecord();
        record.setActivityId(activityId);
        record.setMerId(activity.getMerId());
        record.setUid(uid);
        record.setPeriodNumber(periodNumber);
        record.setPointsCost(activity.getPointsCost());
        record.setIsWinner(0);
        record.setIsRedeemed(0);
        record.setCreateTime(DateUtil.date());

        Boolean result = transactionTemplate.execute(e -> {
            save(record);
            userService.updateIntegral(uid, activity.getPointsCost(), Constants.OPERATION_TYPE_SUBTRACT);
            return Boolean.TRUE;
        });

        return result;
    }

    @Override
    public PageInfo<LotteryRecord> getMyRecords(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getUid, uid);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = dao.selectList(lqw);
        return new PageInfo<>(list);
    }

    @Override
    public Integer getCurrentPeriodCount(Integer activityId, String periodNumber) {
        return dao.selectCount(Wrappers.<LotteryRecord>lambdaQuery()
                .eq(LotteryRecord::getActivityId, activityId)
                .eq(LotteryRecord::getPeriodNumber, periodNumber));
    }

    @Override
    public void executeDraw() {
        // 获取所有开启中的活动
        List<LotteryActivity> activities = lotteryActivityService.getAllActiveActivities();
        for (LotteryActivity activity : activities) {
            try {
                drawForActivity(activity);
            } catch (Exception e) {
                logger.error("抽奖开奖失败，活动ID: {}, 错误: {}", activity.getId(), e.getMessage());
            }
        }
    }

    /**
     * 对单个活动执行开奖
     */
    private void drawForActivity(LotteryActivity activity) {
        String periodNumber = activity.getId() + "-" + String.format("%06d", activity.getCurrentPeriod());

        // 查询当前期未开奖的参与记录
        List<LotteryRecord> records = dao.selectList(Wrappers.<LotteryRecord>lambdaQuery()
                .eq(LotteryRecord::getActivityId, activity.getId())
                .eq(LotteryRecord::getPeriodNumber, periodNumber)
                .isNull(LotteryRecord::getDrawTime));

        // 人数不够，不开奖
        if (records.size() < activity.getParticipantThreshold()) {
            return;
        }

        logger.info("开奖: 活动={}, 期号={}, 参与人数={}", activity.getName(), periodNumber, records.size());

        // 随机选取中奖者
        int winnerCount = Math.min(activity.getWinnerCount(), records.size());
        Collections.shuffle(records);
        List<LotteryRecord> winners = records.subList(0, winnerCount);
        Set<Integer> winnerIds = winners.stream().map(LotteryRecord::getId).collect(Collectors.toSet());

        Date now = DateUtil.date();

        transactionTemplate.execute(e -> {
            // 标记中奖者
            if (!winnerIds.isEmpty()) {
                LambdaUpdateWrapper<LotteryRecord> winnerWrapper = Wrappers.lambdaUpdate();
                winnerWrapper.set(LotteryRecord::getIsWinner, 1);
                winnerWrapper.set(LotteryRecord::getDrawTime, now);
                winnerWrapper.in(LotteryRecord::getId, winnerIds);
                update(winnerWrapper);
            }

            // 标记所有参与者的开奖时间
            LambdaUpdateWrapper<LotteryRecord> allWrapper = Wrappers.lambdaUpdate();
            allWrapper.set(LotteryRecord::getDrawTime, now);
            allWrapper.eq(LotteryRecord::getActivityId, activity.getId());
            allWrapper.eq(LotteryRecord::getPeriodNumber, periodNumber);
            allWrapper.isNull(LotteryRecord::getDrawTime);
            update(allWrapper);

            // 递增活动期号
            activity.setCurrentPeriod(activity.getCurrentPeriod() + 1);
            lotteryActivityService.updateById(activity);
            return Boolean.TRUE;
        });

        logger.info("开奖完成: 活动={}, 中奖人数={}", activity.getName(), winnerIds.size());
    }

    @Override
    public Boolean redeemPrize(Integer recordId, Integer merId) {
        LotteryRecord record = dao.selectById(recordId);
        if (ObjectUtil.isNull(record)) {
            throw new CrmebException("记录不存在");
        }
        if (!record.getMerId().equals(merId)) {
            throw new CrmebException("无权操作此记录");
        }
        if (!record.getIsWinner().equals(1)) {
            throw new CrmebException("该记录未中奖");
        }
        if (record.getIsRedeemed().equals(1)) {
            throw new CrmebException("已兑奖，请勿重复操作");
        }

        LambdaUpdateWrapper<LotteryRecord> wrapper = Wrappers.lambdaUpdate();
        wrapper.set(LotteryRecord::getIsRedeemed, 1);
        wrapper.set(LotteryRecord::getRedeemTime, DateUtil.date());
        wrapper.eq(LotteryRecord::getId, recordId);
        wrapper.eq(LotteryRecord::getIsWinner, 1);
        wrapper.eq(LotteryRecord::getIsRedeemed, 0);
        return update(wrapper);
    }
}
