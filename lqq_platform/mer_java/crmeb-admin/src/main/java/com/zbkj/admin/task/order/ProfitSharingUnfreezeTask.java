package com.zbkj.admin.task.order;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.zbkj.common.constants.BrokerageRecordConstants;
import com.zbkj.common.constants.Constants;
import com.zbkj.common.model.order.WechatProfitSharingRecord;
import com.zbkj.common.model.user.User;
import com.zbkj.common.model.user.UserBrokerageRecord;
import com.zbkj.common.utils.CrmebDateUtil;
import com.zbkj.service.service.UserBrokerageRecordService;
import com.zbkj.service.service.UserService;
import com.zbkj.service.service.WechatProfitSharingRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * [LQQ-迁移] 佣金解冻定时任务
 * 每小时执行一次，将冻结期满的分账佣金转入用户余额
 *
 * 流程:
 * 1. 查询 status=1(分账成功) + frozen_until<=NOW() + is_unfrozen=0 的记录
 * 2. 通过 UserBrokerageRecordService 写入佣金记录
 * 3. 通过 UserService.updateBrokerage 增加用户佣金余额
 * 4. 标记 is_unfrozen=1
 */
@Component("ProfitSharingUnfreezeTask")
public class ProfitSharingUnfreezeTask {

    private static final Logger logger = LoggerFactory.getLogger(ProfitSharingUnfreezeTask.class);

    @Autowired
    private WechatProfitSharingRecordService recordService;
    @Autowired
    private UserBrokerageRecordService userBrokerageRecordService;
    @Autowired
    private UserService userService;
    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 执行佣金解冻
     * cron: 0 0 * * * ? (每小时)
     */
    public void execute() {
        logger.info("---ProfitSharingUnfreezeTask--- 开始执行佣金解冻: {}", CrmebDateUtil.nowDateTime());
        try {
            List<WechatProfitSharingRecord> records = recordService.getUnfreezeReadyRecords(200);
            if (CollUtil.isEmpty(records)) {
                logger.info("---ProfitSharingUnfreezeTask--- 无需解冻的记录");
                return;
            }

            int successCount = 0;
            for (WechatProfitSharingRecord record : records) {
                try {
                    unfreezeRecord(record);
                    successCount++;
                } catch (Exception e) {
                    logger.error("佣金解冻失败: recordId={}, orderNo={}, error={}",
                            record.getId(), record.getOrderNo(), e.getMessage());
                }
            }
            logger.info("---ProfitSharingUnfreezeTask--- 解冻完成: 总数={}, 成功={}", records.size(), successCount);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error("ProfitSharingUnfreezeTask.execute | msg : " + e.getMessage());
        }
    }

    /**
     * 解冻单条分账记录的佣金
     * [需审核] 涉及用户余额变动
     */
    private void unfreezeRecord(WechatProfitSharingRecord record) {
        Integer receiverUserId = record.getReceiverUserId();
        if (ObjectUtil.isNull(receiverUserId) || receiverUserId <= 0) {
            // 无接收用户（如平台记录），直接标记已解冻
            record.setIsUnfrozen(1);
            record.setUpdateTime(new Date());
            recordService.updateById(record);
            return;
        }

        User user = userService.getById(receiverUserId);
        if (ObjectUtil.isNull(user)) {
            logger.warn("佣金解冻跳过: 用户不存在, uid={}", receiverUserId);
            record.setIsUnfrozen(1);
            record.setUpdateTime(new Date());
            recordService.updateById(record);
            return;
        }

        // 构建佣金记录
        UserBrokerageRecord brokerageRecord = new UserBrokerageRecord();
        brokerageRecord.setUid(receiverUserId);
        brokerageRecord.setSubUid(record.getUserId()); // 消费用户为下级
        brokerageRecord.setLinkNo(record.getOrderNo());
        brokerageRecord.setLinkType(BrokerageRecordConstants.BROKERAGE_RECORD_LINK_TYPE_ORDER);
        brokerageRecord.setType(BrokerageRecordConstants.BROKERAGE_RECORD_TYPE_ADD);
        brokerageRecord.setTitle(buildBrokerageTitle(record.getReceiverType()));
        brokerageRecord.setPrice(record.getAmount());
        // [需审核] 余额 = 当前佣金余额 + 本次金额
        BigDecimal balance = user.getBrokeragePrice().add(record.getAmount());
        brokerageRecord.setBalance(balance);
        brokerageRecord.setMark(record.getDescription() + " (订单:" + record.getOrderNo() + ")");
        // 直接设为完成状态，因为冻结期已过
        brokerageRecord.setStatus(BrokerageRecordConstants.BROKERAGE_RECORD_STATUS_COMPLETE);
        brokerageRecord.setFrozenTime(0);
        brokerageRecord.setThawTime(0L);
        brokerageRecord.setBrokerageLevel(getBrokerageLevel(record.getReceiverType()));
        brokerageRecord.setCreateTime(new Date());
        brokerageRecord.setUpdateTime(new Date());

        // 事务: 写入佣金记录 + 更新用户佣金余额 + 标记已解冻
        Boolean result = transactionTemplate.execute(e -> {
            userBrokerageRecordService.save(brokerageRecord);
            userService.updateBrokerage(receiverUserId, record.getAmount(), Constants.OPERATION_TYPE_ADD);
            record.setIsUnfrozen(1);
            record.setUpdateTime(new Date());
            recordService.updateById(record);
            return Boolean.TRUE;
        });

        if (Boolean.TRUE.equals(result)) {
            logger.info("佣金解冻成功: uid={}, amount={}, type={}, orderNo={}",
                    receiverUserId, record.getAmount(), record.getReceiverType(), record.getOrderNo());
        }
    }

    /**
     * 根据接收方类型构建佣金标题
     */
    private String buildBrokerageTitle(String receiverType) {
        switch (receiverType) {
            case "REFERRER":
                return "裂变佣金收入";
            case "AGENT":
                return "代理佣金收入";
            case "LOCKED_MERCHANT":
                return "锁客分润收入";
            default:
                return "分账佣金收入";
        }
    }

    /**
     * 根据接收方类型获取佣金等级
     * 1-一级推广, 2-二级推广, 3-代理佣金
     */
    private Integer getBrokerageLevel(String receiverType) {
        switch (receiverType) {
            case "REFERRER":
                return 1; // 裂变=一级推广
            case "AGENT":
                return 3; // 代理佣金
            case "LOCKED_MERCHANT":
                return 1; // 锁客分润归为一级
            default:
                return 1;
        }
    }
}
