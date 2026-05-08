-- ============================================================
-- [LQQ-迁移] 015: 佣金冻结与解冻 — 分账记录增加冻结字段 + 注册解冻定时任务
-- 日期: 2026-05-08
-- 说明: 分账成功后冻结N天，冻结期满后将佣金转入用户余额
-- ============================================================

-- 分账记录表增加冻结字段
ALTER TABLE `eb_wechat_profit_sharing_record`
    ADD COLUMN `frozen_until` DATETIME DEFAULT NULL COMMENT '冻结截止时间(到期后可解冻佣金)' AFTER `service_mch_id`,
    ADD COLUMN `is_unfrozen` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已解冻佣金: 0=否, 1=已解冻' AFTER `frozen_until`,
    ADD INDEX `idx_unfreeze` (`status`, `frozen_until`, `is_unfrozen`);

-- 佣金冻结天数配置（默认3天）
INSERT INTO `eb_system_config` (`name`, `title`, `value`, `status`)
VALUES ('lqq_commission_freeze_days', '溜圈圈佣金冻结天数', '3', 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 注册佣金解冻定时任务 (每小时执行)
INSERT INTO `eb_schedule_job` (`bean_name`, `method_name`, `params`, `cron_expression`, `status`, `remark`, `is_delete`, `create_time`)
VALUES ('ProfitSharingUnfreezeTask', 'execute', '', '0 0 * * * ?', 0, '[LQQ] 佣金冻结解冻任务(每小时)', 0, NOW());
