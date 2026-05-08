package com.zbkj.service.service.impl;

import cn.hutool.core.collection.CollUtil;
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
import com.zbkj.common.response.LotteryRecordResponse;
import com.zbkj.service.dao.LotteryRecordDao;
import com.zbkj.service.service.LotteryActivityService;
import com.zbkj.service.service.LotteryRecordService;
import com.zbkj.service.service.UserService;
import org.springframework.beans.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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

        // 4. 获取当前期号，如果满了则推进到下期（最多重试一次）
        // [LQQ-迁移] 期号推进统一由 advancePeriodIfFull 处理
        String periodNumber = buildPeriodNumber(activityId, activity.getCurrentPeriod());
        Integer currentCount = getCurrentPeriodCount(activityId, periodNumber);
        if (currentCount >= activity.getParticipantThreshold()) {
            Integer newPeriod = advancePeriodIfFull(activityId, activity.getCurrentPeriod());
            periodNumber = buildPeriodNumber(activityId, newPeriod);
        }

        // 5. 创建参与记录 + 扣减积分（事务）
        // [LQQ-迁移] 依赖 UNIQUE INDEX uk_activity_uid_period 防止并发重复参与
        final Integer pointsCost = activity.getPointsCost();
        LotteryRecord record = new LotteryRecord();
        record.setActivityId(activityId);
        record.setMerId(activity.getMerId());
        record.setUid(uid);
        record.setPeriodNumber(periodNumber);
        record.setPointsCost(pointsCost);
        record.setIsWinner(0);
        record.setIsRedeemed(0);
        record.setCreateTime(DateUtil.date());

        try {
            Boolean result = transactionTemplate.execute(e -> {
                save(record);
                userService.updateIntegral(uid, pointsCost, Constants.OPERATION_TYPE_SUBTRACT);
                return Boolean.TRUE;
            });
            return result;
        } catch (DuplicateKeyException e) {
            // 并发情况下唯一索引拦截重复参与
            throw new CrmebException("本期已参与，请等待开奖");
        }
    }

    @Override
    public PageInfo<LotteryRecordResponse> getMyRecords(PageParamRequest pageParamRequest) {
        Integer uid = userService.getUserIdException();
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getUid, uid);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = dao.selectList(lqw);
        PageInfo<LotteryRecord> rawPage = new PageInfo<>(list);
        return convertToResponsePage(rawPage);
    }

    @Override
    public Integer getCurrentPeriodCount(Integer activityId, String periodNumber) {
        return dao.selectCount(Wrappers.<LotteryRecord>lambdaQuery()
                .eq(LotteryRecord::getActivityId, activityId)
                .eq(LotteryRecord::getPeriodNumber, periodNumber));
    }

    /**
     * [LQQ-迁移] 期号推进 — 唯一入口，使用乐观锁CAS防止竞态
     * @return 推进后的新期号数字（如果CAS失败则重新读取最新值）
     */
    private Integer advancePeriodIfFull(Integer activityId, Integer expectedPeriod) {
        boolean updated = lotteryActivityService.update(Wrappers.<LotteryActivity>lambdaUpdate()
                .set(LotteryActivity::getCurrentPeriod, expectedPeriod + 1)
                .set(LotteryActivity::getUpdateTime, DateUtil.date())
                .eq(LotteryActivity::getId, activityId)
                .eq(LotteryActivity::getCurrentPeriod, expectedPeriod));
        if (updated) {
            return expectedPeriod + 1;
        }
        // CAS失败，其他线程已推进，读取最新值
        LotteryActivity latest = lotteryActivityService.getByIdException(activityId);
        return latest.getCurrentPeriod();
    }

    private String buildPeriodNumber(Integer activityId, Integer period) {
        return activityId + "-" + String.format("%06d", period);
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
     * [LQQ-迁移] 扫描所有已满但未开奖的期次，而非仅当前期
     * 期号推进由 participate() 中的 advancePeriodIfFull() 统一负责
     */
    private void drawForActivity(LotteryActivity activity) {
        // 扫描从第1期到当前期，找到已满且未开奖的期次
        for (int period = 1; period <= activity.getCurrentPeriod(); period++) {
            String periodNumber = buildPeriodNumber(activity.getId(), period);

            // 查询该期未开奖的参与记录
            List<LotteryRecord> records = dao.selectList(Wrappers.<LotteryRecord>lambdaQuery()
                    .eq(LotteryRecord::getActivityId, activity.getId())
                    .eq(LotteryRecord::getPeriodNumber, periodNumber)
                    .isNull(LotteryRecord::getDrawTime));

            // 人数不够，跳过
            if (records.size() < activity.getParticipantThreshold()) {
                continue;
            }

            logger.info("开奖: 活动={}, 期号={}, 参与人数={}", activity.getName(), periodNumber, records.size());

            // 随机选取中奖者
            int winnerCount = Math.min(activity.getWinnerCount(), records.size());
            Collections.shuffle(records);
            List<LotteryRecord> winners = records.subList(0, winnerCount);
            Set<Integer> winnerIds = winners.stream().map(LotteryRecord::getId).collect(Collectors.toSet());

            Date now = DateUtil.date();
            final String pn = periodNumber;

            transactionTemplate.execute(e -> {
                // 标记中奖者
                if (!winnerIds.isEmpty()) {
                    LambdaUpdateWrapper<LotteryRecord> winnerWrapper = Wrappers.lambdaUpdate();
                    winnerWrapper.set(LotteryRecord::getIsWinner, 1);
                    winnerWrapper.set(LotteryRecord::getDrawTime, now);
                    winnerWrapper.in(LotteryRecord::getId, winnerIds);
                    update(winnerWrapper);
                }

                // 标记未中奖参与者的开奖时间
                LambdaUpdateWrapper<LotteryRecord> allWrapper = Wrappers.lambdaUpdate();
                allWrapper.set(LotteryRecord::getDrawTime, now);
                allWrapper.eq(LotteryRecord::getActivityId, activity.getId());
                allWrapper.eq(LotteryRecord::getPeriodNumber, pn);
                allWrapper.isNull(LotteryRecord::getDrawTime);
                update(allWrapper);

                return Boolean.TRUE;
            });

            logger.info("开奖完成: 活动={}, 期号={}, 中奖人数={}", activity.getName(), periodNumber, winnerIds.size());
        }
    }

    @Override
    public PageInfo<LotteryRecordResponse> getParticipants(Integer activityId, PageParamRequest pageParamRequest) {
        LotteryActivity activity = lotteryActivityService.getByIdException(activityId);
        String periodNumber = activityId + "-" + String.format("%06d", activity.getCurrentPeriod());
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getActivityId, activityId);
        lqw.eq(LotteryRecord::getPeriodNumber, periodNumber);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = dao.selectList(lqw);
        PageInfo<LotteryRecord> rawPage = new PageInfo<>(list);
        return convertToResponsePage(rawPage);
    }

    @Override
    public PageInfo<LotteryRecordResponse> getPlatformRecords(PageParamRequest pageParamRequest) {
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = dao.selectList(lqw);
        PageInfo<LotteryRecord> rawPage = new PageInfo<>(list);
        return convertToResponsePage(rawPage);
    }

    @Override
    public PageInfo<LotteryRecordResponse> getPlatformWinners(PageParamRequest pageParamRequest) {
        Page<LotteryRecord> page = PageHelper.startPage(pageParamRequest.getPage(), pageParamRequest.getLimit());
        LambdaQueryWrapper<LotteryRecord> lqw = Wrappers.lambdaQuery();
        lqw.eq(LotteryRecord::getIsWinner, 1);
        lqw.orderByDesc(LotteryRecord::getCreateTime);
        List<LotteryRecord> list = dao.selectList(lqw);
        PageInfo<LotteryRecord> rawPage = new PageInfo<>(list);
        return convertToResponsePage(rawPage);
    }

    // [LQQ-迁移] 将 LotteryRecord 分页结果转换为 LotteryRecordResponse，关联查询 nickname 和 activityName
    private PageInfo<LotteryRecordResponse> convertToResponsePage(PageInfo<LotteryRecord> rawPage) {
        List<LotteryRecord> records = rawPage.getList();
        if (CollUtil.isEmpty(records)) {
            PageInfo<LotteryRecordResponse> emptyPage = new PageInfo<>(new ArrayList<>());
            emptyPage.setTotal(rawPage.getTotal());
            emptyPage.setPageNum(rawPage.getPageNum());
            emptyPage.setPageSize(rawPage.getPageSize());
            return emptyPage;
        }

        // 批量查询关联的用户和活动，避免 N+1
        Set<Integer> uidSet = records.stream().map(LotteryRecord::getUid).collect(Collectors.toSet());
        Set<Integer> activityIdSet = records.stream().map(LotteryRecord::getActivityId).collect(Collectors.toSet());

        Map<Integer, String> userNicknameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(uidSet)) {
            List<User> users = userService.listByIds(uidSet);
            userNicknameMap = users.stream().collect(Collectors.toMap(User::getId, u -> ObjectUtil.isNotNull(u.getNickname()) ? u.getNickname() : "", (a, b) -> a));
        }

        Map<Integer, String> activityNameMap = new HashMap<>();
        if (CollUtil.isNotEmpty(activityIdSet)) {
            List<LotteryActivity> activities = lotteryActivityService.listByIds(activityIdSet);
            activityNameMap = activities.stream().collect(Collectors.toMap(LotteryActivity::getId, a -> ObjectUtil.isNotNull(a.getName()) ? a.getName() : "", (a, b) -> a));
        }

        final Map<Integer, String> nickMap = userNicknameMap;
        final Map<Integer, String> actMap = activityNameMap;
        List<LotteryRecordResponse> responseList = records.stream().map(record -> {
            LotteryRecordResponse resp = new LotteryRecordResponse();
            BeanUtils.copyProperties(record, resp);
            resp.setNickname(nickMap.getOrDefault(record.getUid(), ""));
            resp.setActivityName(actMap.getOrDefault(record.getActivityId(), ""));
            return resp;
        }).collect(Collectors.toList());

        PageInfo<LotteryRecordResponse> responsePage = new PageInfo<>(responseList);
        responsePage.setTotal(rawPage.getTotal());
        responsePage.setPageNum(rawPage.getPageNum());
        responsePage.setPageSize(rawPage.getPageSize());
        responsePage.setPages(rawPage.getPages());
        return responsePage;
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
