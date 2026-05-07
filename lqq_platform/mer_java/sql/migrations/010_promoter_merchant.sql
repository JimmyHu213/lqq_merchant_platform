-- ============================================================
-- [LQQ-迁移] 010: 推广员代理 — 推广员-商户绑定关系表
-- 日期: 2026-04-05
-- 说明: 单代理模式，一个商户只能绑一个推广员，一个推广员可代理多个商户
-- ============================================================

CREATE TABLE IF NOT EXISTS `eb_promoter_merchant` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `uid` INT UNSIGNED NOT NULL COMMENT '推广员用户ID',
    `mer_id` INT UNSIGNED NOT NULL COMMENT '绑定商户ID',
    `commission_rate` DECIMAL(5,2) NOT NULL DEFAULT 0.00 COMMENT '代理佣金比例(%)',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0=停用, 1=启用',
    `audit_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '审核状态: 0=待审核, 1=通过, 2=拒绝',
    `audit_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `is_del` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '绑定时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE INDEX `uk_mer_id` (`mer_id`),
    INDEX `idx_uid` (`uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='[LQQ] 推广员-商户绑定关系表';
