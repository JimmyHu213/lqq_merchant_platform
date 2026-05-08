package com.zbkj.admin.task.order;

import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.LqqProfitSharingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * [LQQ-迁移] 微信多方分账定时任务
 * 每5分钟执行一次，处理待分账订单
 *
 * 参考 mlqApi: DsTask.mdFz() — 每5分钟执行一次线上订单分账
 */
@Component("LqqProfitSharingTask")
public class LqqProfitSharingTask {

    private static final Logger logger = LoggerFactory.getLogger(LqqProfitSharingTask.class);

    @Autowired
    private LqqProfitSharingService lqqProfitSharingService;

    /**
     * 执行分账
     * cron: 0 *\/5 * * * ?
     */
    public void execute() {
        logger.info("---LqqProfitSharingTask--- 开始执行分账任务: {}", CrmebDateUtil.nowDateTime());
        try {
            lqqProfitSharingService.executePendingProfitSharing();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("LqqProfitSharingTask.execute | msg : " + e.getMessage());
        }
        logger.info("---LqqProfitSharingTask--- 分账任务执行完成: {}", CrmebDateUtil.nowDateTime());
    }
}
