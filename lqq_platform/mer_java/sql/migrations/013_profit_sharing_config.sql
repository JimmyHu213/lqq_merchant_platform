-- ============================================================
-- [LQQ-迁移] 013: 分账配置 — 首单系数+裂变佣金比例
-- 日期: 2026-05-08
-- 说明: 平台管理员可在系统配置中调整这些值
-- ============================================================

-- 首单系数（默认1.5倍）
INSERT INTO `eb_system_config` (`name`, `title`, `value`, `status`)
VALUES ('lqq_first_order_coefficient', '溜圈圈首单翻倍系数', '1.5', 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);

-- 裂变佣金比例（默认5%）
INSERT INTO `eb_system_config` (`name`, `title`, `value`, `status`)
VALUES ('lqq_referral_commission_rate', '溜圈圈裂变佣金比例(%)', '5', 1)
ON DUPLICATE KEY UPDATE `title` = VALUES(`title`);
