-- ============================================================
-- [LQQ-迁移] 011: 微信多方分账模块 (B2)
-- 日期: 2026-05-08
-- 说明: 创建微信多方分账记录表，注册分账定时任务
-- [需审核] 金额计算涉及资金分配
-- ============================================================

-- 微信多方分账记录表
CREATE TABLE IF NOT EXISTS `eb_wechat_profit_sharing_record` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `order_no` VARCHAR(64) NOT NULL COMMENT '平台订单号',
    `merchant_order_no` VARCHAR(64) DEFAULT NULL COMMENT '商户订单号',
    `transaction_id` VARCHAR(64) DEFAULT NULL COMMENT '微信支付订单号(transaction_id)',
    `profit_sharing_order_no` VARCHAR(64) DEFAULT NULL COMMENT '分账单号(out_order_no)',
    `order_mer_id` INT DEFAULT NULL COMMENT '订单商户ID',
    `user_id` INT DEFAULT NULL COMMENT '消费用户ID',
    `receiver_user_id` INT DEFAULT NULL COMMENT '分账接收方用户ID(系统内)',
    `receiver_name` VARCHAR(100) DEFAULT NULL COMMENT '分账接收方名称',
    `receiver_openid` VARCHAR(64) DEFAULT NULL COMMENT '分账接收方微信openid',
    `receiver_type` VARCHAR(32) NOT NULL COMMENT '接收方类型: LOCKED_MERCHANT-锁客商户, REFERRER-推荐人, REFERRER_PARENT-推荐人上级, PROMOTER-提成人, PLATFORM-平台',
    `amount` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '分账金额(元)',
    `rate` DECIMAL(5,2) DEFAULT 0.00 COMMENT '分账比例(%)',
    `base_amount` DECIMAL(10,2) DEFAULT 0.00 COMMENT '分账基数金额(元)',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '分账描述',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '分账状态: 0-待分账, 1-分账成功, 2-分账失败, 3-已解冻',
    `fail_reason` VARCHAR(500) DEFAULT NULL COMMENT '失败原因',
    `sub_mch_id` VARCHAR(64) DEFAULT NULL COMMENT '微信子商户号',
    `service_mch_id` VARCHAR(64) DEFAULT NULL COMMENT '微信服务商商户号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_order_no` (`order_no`),
    KEY `idx_order_mer_id` (`order_mer_id`),
    KEY `idx_status` (`status`),
    KEY `idx_receiver_user_id` (`receiver_user_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='[LQQ] 微信多方分账记录表';

-- 注册分账定时任务
INSERT INTO `eb_schedule_job` (`bean_name`, `method_name`, `params`, `cron_expression`, `status`, `remark`, `is_delete`, `create_time`)
VALUES ('LqqProfitSharingTask', 'execute', '', '0 */5 * * * ?', 0, '[LQQ] 微信多方分账定时任务 - 每5分钟执行', 0, NOW());
