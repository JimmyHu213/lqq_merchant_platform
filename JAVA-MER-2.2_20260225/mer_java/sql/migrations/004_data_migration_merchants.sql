-- ============================================================
-- [LQQ-迁移] 004: 商户数据迁移 (t_business_shop → eb_merchant + eb_merchant_info)
-- 日期: 2026-04-03
-- 前提: 先执行 001_extend_merchant_for_lqq.sql 增加扩展字段
-- ============================================================

-- 步骤1: 迁移商户基本信息
INSERT INTO `eb_merchant` (
    -- 基础字段
    `name`, `real_name`, `email`, `phone`, `keywords`,
    `province`, `city`, `district`, `address_detail`,
    `latitude`, `longitude`,
    -- 图片
    `cover_image`, `interior_image`, `intro`,
    -- 开关
    `is_self`, `is_recommend`, `is_switch`, `is_take_their`,
    `star_level`, `sort`,
    `create_type`, `is_del`, `create_time`, `update_time`,
    -- [LQQ] 分账相关
    `fszk`, `qyzk`, `skzk`, `is_profit_sharing`,
    -- [LQQ] 微信支付
    `wx_sub_mch_id`, `wx_service_mch_id`,
    -- [LQQ] 业务运营
    `qr_code_id`, `coupon_per_order`,
    `business_hours_start`, `business_hours_end`, `business_status`,
    `business_district`, `business_scope`,
    -- [LQQ] 推荐人
    `referrer_id`, `referrer_name`,
    -- [LQQ] KYC
    `license_number`, `license_image`,
    `legal_person_id_number`, `legal_person_id_front`, `legal_person_id_back`,
    -- [LQQ] 统计
    `review_score`, `review_count`, `view_count`
)
SELECT
    s.name,                                                    -- name
    IFNULL(s.lxr, s.name),                                     -- real_name (联系人或商户名)
    IFNULL(s.email, ''),                                       -- email
    IFNULL(s.mobile, ''),                                      -- phone
    IFNULL(s.keywords, ''),                                    -- keywords
    IFNULL(s.prv, ''),                                         -- province
    IFNULL(s.city, ''),                                        -- city
    IFNULL(s.cnty, ''),                                        -- district
    IFNULL(s.addr, ''),                                        -- address_detail
    IFNULL(s.wd, ''),                                          -- latitude (纬度)
    IFNULL(s.jd, ''),                                          -- longitude (经度)
    IFNULL(s.mmt, ''),                                         -- cover_image (门面图)
    IFNULL(s.dnt, ''),                                         -- interior_image (店内图)
    IFNULL(s.jyjs, ''),                                        -- intro (简介)
    0,                                                         -- is_self (非自营)
    0,                                                         -- is_recommend
    CASE WHEN s.status = '1' THEN 1 ELSE 0 END,               -- is_switch
    1,                                                         -- is_take_their (到店自提)
    LEAST(GREATEST(ROUND(IFNULL(s.pf, 5)), 1), 5),            -- star_level (1-5)
    0,                                                         -- sort
    'apply',                                                   -- create_type
    0,                                                         -- is_del
    IFNULL(s.add_time, NOW()),                                 -- create_time
    NOW(),                                                     -- update_time
    -- [LQQ] 分账
    IFNULL(s.fszk, 0),                                         -- fszk
    IFNULL(s.qyzk, 0),                                         -- qyzk
    CAST(IFNULL(s.skzk, '0') AS DECIMAL(5,2)),                 -- skzk
    CASE WHEN s.isfz = '1' THEN 1 ELSE 0 END,                 -- is_profit_sharing
    -- [LQQ] 微信支付
    s.submid,                                                  -- wx_sub_mch_id
    s.fwsmid,                                                  -- wx_service_mch_id
    -- [LQQ] 运营
    s.ewmid,                                                   -- qr_code_id
    IFNULL(s.yhqnum, 3),                                       -- coupon_per_order
    s.yysjs,                                                   -- business_hours_start
    s.yysje,                                                   -- business_hours_end
    IFNULL(s.yyzt, 1),                                         -- business_status
    s.sq,                                                      -- business_district
    s.jyfw,                                                    -- business_scope
    -- [LQQ] 推荐人 (tjr 是旧系统ID，需后续通过映射表更新)
    NULL,                                                      -- referrer_id (待映射)
    s.tjrn,                                                    -- referrer_name
    -- [LQQ] KYC
    s.lis_num,                                                 -- license_number
    s.yyzz,                                                    -- license_image
    s.sfzh,                                                    -- legal_person_id_number
    s.frsfz,                                                   -- legal_person_id_front
    s.frsff,                                                   -- legal_person_id_back
    -- [LQQ] 统计
    IFNULL(s.pf, 5.0),                                         -- review_score
    IFNULL(s.plnum, 0),                                        -- review_count
    IFNULL(s.looknum, 0)                                       -- view_count
FROM `zhjy`.`t_business_shop` s;

-- 步骤2: 记录商户 ID 映射
INSERT INTO `lqq_id_mapping` (`table_name`, `old_id`, `new_id`)
SELECT 'merchant', s.id, m.id
FROM `zhjy`.`t_business_shop` s
JOIN `eb_merchant` m ON m.wx_sub_mch_id = s.submid
    OR (m.name = s.name AND m.phone = IFNULL(s.mobile, ''));

-- 步骤3: 迁移商户结算信息到 eb_merchant_info
INSERT INTO `eb_merchant_info` (
    `mer_id`, `settlement_type`, `bank_user_name`, `bank_name`,
    `bank_card`, `bank_address`, `wechat_code`, `real_name`,
    `alipay_code`, `create_time`, `update_time`
)
SELECT
    m.new_id,                                                  -- mer_id
    'bank',                                                    -- settlement_type
    IFNULL(s.yhr, ''),                                         -- bank_user_name (持卡人)
    IFNULL(s.yhm, ''),                                         -- bank_name (银行名)
    IFNULL(s.yhkh, ''),                                        -- bank_card (卡号)
    IFNULL(s.yhdz, ''),                                        -- bank_address (开户地址)
    IFNULL(s.wechat, ''),                                      -- wechat_code
    IFNULL(s.lxr, s.name),                                     -- real_name
    IFNULL(s.zfb, ''),                                         -- alipay_code
    IFNULL(s.add_time, NOW()),                                 -- create_time
    NOW()                                                      -- update_time
FROM `zhjy`.`t_business_shop` s
JOIN `lqq_id_mapping` m ON m.table_name = 'merchant' AND m.old_id = s.id;
