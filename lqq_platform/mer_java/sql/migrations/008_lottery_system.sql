-- ============================================================
-- [LQQ-迁移] 008: 抽奖系统 — 新建活动表 + 参与记录表 + 注册定时任务
-- 日期: 2026-04-04
-- ============================================================

-- 抽奖活动表
CREATE TABLE IF NOT EXISTS `eb_lottery_activity` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mer_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户ID',
    `name` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '活动名称',
    `image` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '活动图片',
    `description` TEXT COMMENT '活动规则描述',
    `points_cost` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '每次参与所需积分',
    `participant_threshold` INT UNSIGNED NOT NULL DEFAULT 10 COMMENT '开奖所需人数',
    `winner_count` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '每期中奖人数',
    `prize_name` VARCHAR(200) NOT NULL DEFAULT '' COMMENT '奖品名称',
    `prize_value` DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '奖品价值',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0=关闭, 1=开启',
    `audit_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '审核状态: 0=待审核, 1=通过, 2=拒绝',
    `audit_reason` VARCHAR(500) DEFAULT NULL COMMENT '审核拒绝原因',
    `current_period` INT UNSIGNED NOT NULL DEFAULT 1 COMMENT '当前期号',
    `is_del` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除: 0=否, 1=是',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_mer_id` (`mer_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='[LQQ] 抽奖活动表';

-- 抽奖参与记录表
CREATE TABLE IF NOT EXISTS `eb_lottery_record` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `activity_id` INT UNSIGNED NOT NULL COMMENT '活动ID',
    `mer_id` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户ID',
    `uid` INT UNSIGNED NOT NULL COMMENT '参与用户ID',
    `period_number` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '期号',
    `points_cost` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '消耗积分',
    `is_winner` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否中奖: 0=否, 1=是',
    `is_redeemed` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否兑奖: 0=否, 1=已兑',
    `redeem_time` DATETIME DEFAULT NULL COMMENT '兑奖时间',
    `draw_time` DATETIME DEFAULT NULL COMMENT '开奖时间(NULL=未开奖)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '参与时间',
    PRIMARY KEY (`id`),
    INDEX `idx_activity_period` (`activity_id`, `period_number`),
    INDEX `idx_uid` (`uid`),
    INDEX `idx_draw_time` (`draw_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='[LQQ] 抽奖参与记录表';

-- 注册开奖定时任务 (每5分钟执行)
INSERT INTO `eb_schedule_job` (`bean_name`, `method_name`, `params`, `cron_expression`, `status`, `remark`, `is_delete`, `create_time`)
VALUES ('LotteryDrawTask', 'executeDraw', '', '0 */5 * * * ?', 0, '[LQQ] 抽奖自动开奖任务', 0, NOW());
