package com.zbkj.admin.task.lottery;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.LotteryRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 抽奖自动开奖定时任务
 * [LQQ-迁移] 抽奖系统
 * 每5分钟执行一次，检查所有活动是否满足开奖条件
 */
@Component("LotteryDrawTask")
public class LotteryDrawTask {

    private static final Logger logger = LoggerFactory.getLogger(LotteryDrawTask.class);

    @Autowired
    private LotteryRecordService lotteryRecordService;

    /**
     * 执行开奖
     * cron: 0 *\/5 * * * ?
     */
    public void executeDraw() {
        logger.info("---LotteryDrawTask--- 开始执行开奖任务: {}", CrmebDateUtil.nowDateTime());
        try {
            lotteryRecordService.executeDraw();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LotteryDrawTask.executeDraw | msg : " + e.getMessage());
        }
        logger.info("---LotteryDrawTask--- 开奖任务执行完成: {}", CrmebDateUtil.nowDateTime());
    }
}
