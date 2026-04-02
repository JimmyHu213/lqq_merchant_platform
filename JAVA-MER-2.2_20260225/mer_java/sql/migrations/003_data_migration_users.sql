-- ============================================================
-- [LQQ-迁移] 003: 用户数据迁移 (t_customer_buyer → eb_user + eb_wechat_user)
-- 日期: 2026-04-03
-- 说明: 从 mlqApi 旧数据库(zhjy)迁移用户数据到 JAVA-MER(java_mer_trip)
-- 执行前提: 两个数据库在同一 MySQL 实例，或已通过 FEDERATED/导入中间表
-- ============================================================

-- 步骤1: 迁移用户基本信息
-- 注意: mlqApi 的 userId 是 String，JAVA-MER 的 id 是 Integer(AUTO_INCREMENT)
-- 需要维护一个 ID 映射表用于后续关联数据迁移

CREATE TABLE IF NOT EXISTS `lqq_id_mapping` (
    `table_name` VARCHAR(50) NOT NULL COMMENT '原表名',
    `old_id` VARCHAR(50) NOT NULL COMMENT '旧系统ID',
    `new_id` INT NOT NULL COMMENT '新系统ID',
    PRIMARY KEY (`table_name`, `old_id`),
    INDEX `idx_new_id` (`table_name`, `new_id`)
) ENGINE=InnoDB COMMENT='[LQQ] 数据迁移ID映射表';

-- 步骤2: 迁移用户表
-- 执行时需替换 zhjy 为实际旧数据库名
INSERT INTO `eb_user` (
    `account`, `pwd`, `real_name`, `nickname`, `phone`, `avatar`,
    `sex`, `birthday`, `integral`, `now_money`, `spread_uid`,
    `register_type`, `add_ip`, `last_ip`, `last_login_time`,
    `status`, `create_time`, `update_time`
)
SELECT
    IFNULL(b.username, CONCAT('lqq_', b.id)),   -- account
    IFNULL(b.password, ''),                       -- pwd (需后续重置)
    IFNULL(b.name, ''),                           -- real_name
    IFNULL(b.nickname, CONCAT('用户', b.id)),     -- nickname
    IFNULL(b.mobile, ''),                         -- phone
    IFNULL(b.avatar, ''),                         -- avatar
    CASE b.gender
        WHEN '男' THEN 1
        WHEN '女' THEN 2
        ELSE 0
    END,                                          -- sex (0=未知,1=男,2=女)
    DATE_FORMAT(b.birthday, '%Y-%m-%d'),          -- birthday
    CAST(IFNULL(b.jf, '0') AS UNSIGNED),          -- integral (积分)
    0.00,                                         -- now_money (余额需单独迁移)
    0,                                            -- spread_uid (推荐关系需单独处理)
    'routine',                                    -- register_type (小程序注册)
    IFNULL(b.register_ip, ''),                    -- add_ip
    IFNULL(b.last_login_ip, ''),                  -- last_ip
    b.last_login_time,                            -- last_login_time
    CASE b.status WHEN '1' THEN 1 ELSE 0 END,    -- status
    IFNULL(b.add_time, NOW()),                    -- create_time
    NOW()                                         -- update_time
FROM `zhjy`.`t_customer_buyer` b;

-- 步骤3: 记录 ID 映射
INSERT INTO `lqq_id_mapping` (`table_name`, `old_id`, `new_id`)
SELECT 'user', b.id, u.id
FROM `zhjy`.`t_customer_buyer` b
JOIN `eb_user` u ON u.account = IFNULL(b.username, CONCAT('lqq_', b.id));

-- 步骤4: 迁移微信绑定信息 (需要 eb_wechat_user 表存在)
-- JAVA-MER 的微信用户信息存储在单独的表中
INSERT INTO `eb_wechat_user` (
    `uid`, `routine_openid`, `unionid`, `nickname`, `headimgurl`, `sex`,
    `create_time`, `update_time`
)
SELECT
    m.new_id,                                     -- uid (映射后的新ID)
    IFNULL(b.wx_openid, ''),                      -- routine_openid (小程序openid)
    IFNULL(b.wx_unionid, ''),                     -- unionid
    IFNULL(b.nickname, ''),                       -- nickname
    IFNULL(b.avatar, ''),                         -- headimgurl
    CASE b.gender WHEN '男' THEN 1 WHEN '女' THEN 2 ELSE 0 END,
    IFNULL(b.add_time, NOW()),
    NOW()
FROM `zhjy`.`t_customer_buyer` b
JOIN `lqq_id_mapping` m ON m.table_name = 'user' AND m.old_id = b.id
WHERE b.wx_openid IS NOT NULL AND b.wx_openid != '';

-- 步骤5: 迁移推荐人关系 (yqBuyerId → spread_uid)
UPDATE `eb_user` u
JOIN `lqq_id_mapping` m1 ON m1.table_name = 'user' AND m1.new_id = u.id
JOIN `zhjy`.`t_customer_buyer` b ON b.id = m1.old_id
JOIN `lqq_id_mapping` m2 ON m2.table_name = 'user' AND m2.old_id = b.yq_buyer_id
SET u.spread_uid = m2.new_id,
    u.spread_time = b.add_time
WHERE b.yq_buyer_id IS NOT NULL AND b.yq_buyer_id != '';
