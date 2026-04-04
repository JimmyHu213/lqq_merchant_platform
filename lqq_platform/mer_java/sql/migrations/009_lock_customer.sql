-- ============================================================
-- [LQQ-迁移] 009: 自动锁客 — eb_user 增加锁客字段
-- 日期: 2026-04-04
-- 说明: 用户首次消费自动绑定到消费商户
-- ============================================================

ALTER TABLE eb_user ADD COLUMN `locked_merchant_id` INT DEFAULT NULL COMMENT '[LQQ] 锁客商户ID' AFTER `spread_count`;
ALTER TABLE eb_user ADD COLUMN `locked_merchant_time` DATETIME DEFAULT NULL COMMENT '[LQQ] 锁客时间' AFTER `locked_merchant_id`;
