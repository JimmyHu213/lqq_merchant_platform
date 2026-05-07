-- ============================================================
-- [LQQ-迁移] 007: 优惠券转赠 — 增加转赠来源字段
-- 日期: 2026-04-04
-- 说明: 在 eb_coupon_user 表增加转赠来源用户ID字段
-- ============================================================

ALTER TABLE eb_coupon_user
    ADD COLUMN `transfer_from_uid` INT DEFAULT NULL COMMENT '[LQQ] 转赠来源用户ID，NULL表示非转赠获得' AFTER `status`;
