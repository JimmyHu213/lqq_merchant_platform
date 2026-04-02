-- ============================================================
-- [LQQ-迁移] 001: 扩展商户表 — 增加溜圈圈业务字段
-- 日期: 2026-04-03
-- 说明: 在 JAVA-MER eb_merchant 表上增加溜圈圈特有的业务字段
-- ============================================================

-- 分账相关字段
ALTER TABLE eb_merchant ADD COLUMN `fszk` DECIMAL(5,2) DEFAULT 0.00 COMMENT '[LQQ] 返商折扣(百分比)，用于分润基础金额计算' AFTER `balance`;
ALTER TABLE eb_merchant ADD COLUMN `qyzk` DECIMAL(5,2) DEFAULT 0.00 COMMENT '[LQQ] 签约折扣(百分比)' AFTER `fszk`;
ALTER TABLE eb_merchant ADD COLUMN `skzk` DECIMAL(5,2) DEFAULT 0.00 COMMENT '[LQQ] 锁客折扣(百分比)，锁客商铺分润比例' AFTER `qyzk`;
ALTER TABLE eb_merchant ADD COLUMN `is_profit_sharing` TINYINT(1) DEFAULT 0 COMMENT '[LQQ] 是否启用微信分账: 0-否, 1-是' AFTER `skzk`;

-- 微信支付路由字段
ALTER TABLE eb_merchant ADD COLUMN `wx_sub_mch_id` VARCHAR(64) DEFAULT NULL COMMENT '[LQQ] 微信子商户号(特约商户)' AFTER `is_profit_sharing`;
ALTER TABLE eb_merchant ADD COLUMN `wx_service_mch_id` VARCHAR(64) DEFAULT NULL COMMENT '[LQQ] 微信服务商商户号' AFTER `wx_sub_mch_id`;

-- 业务运营字段
ALTER TABLE eb_merchant ADD COLUMN `qr_code_id` VARCHAR(64) DEFAULT NULL COMMENT '[LQQ] 商铺二维码编号' AFTER `wx_service_mch_id`;
ALTER TABLE eb_merchant ADD COLUMN `coupon_per_order` INT DEFAULT 3 COMMENT '[LQQ] 每笔订单发券数量' AFTER `qr_code_id`;
ALTER TABLE eb_merchant ADD COLUMN `business_hours_start` VARCHAR(10) DEFAULT NULL COMMENT '[LQQ] 营业开始时间(HH:mm)' AFTER `coupon_per_order`;
ALTER TABLE eb_merchant ADD COLUMN `business_hours_end` VARCHAR(10) DEFAULT NULL COMMENT '[LQQ] 营业结束时间(HH:mm)' AFTER `business_hours_start`;
ALTER TABLE eb_merchant ADD COLUMN `business_status` TINYINT DEFAULT 1 COMMENT '[LQQ] 营业状态: 0-休息, 1-营业' AFTER `business_hours_end`;
ALTER TABLE eb_merchant ADD COLUMN `business_district` VARCHAR(100) DEFAULT NULL COMMENT '[LQQ] 所属商圈' AFTER `business_status`;
ALTER TABLE eb_merchant ADD COLUMN `business_scope` VARCHAR(500) DEFAULT NULL COMMENT '[LQQ] 经营范围' AFTER `business_district`;
ALTER TABLE eb_merchant ADD COLUMN `interior_image` VARCHAR(1000) DEFAULT NULL COMMENT '[LQQ] 店内图(多张逗号分隔)' AFTER `business_scope`;

-- 推荐人字段
ALTER TABLE eb_merchant ADD COLUMN `referrer_id` INT DEFAULT NULL COMMENT '[LQQ] 推荐人ID(推广员userId)' AFTER `interior_image`;
ALTER TABLE eb_merchant ADD COLUMN `referrer_name` VARCHAR(100) DEFAULT NULL COMMENT '[LQQ] 推荐人名称' AFTER `referrer_id`;

-- KYC 扩展字段 (溜圈圈特有的证照)
ALTER TABLE eb_merchant ADD COLUMN `license_number` VARCHAR(50) DEFAULT NULL COMMENT '[LQQ] 统一社会信用代码/注册号' AFTER `referrer_name`;
ALTER TABLE eb_merchant ADD COLUMN `license_image` VARCHAR(500) DEFAULT NULL COMMENT '[LQQ] 营业执照图片URL' AFTER `license_number`;
ALTER TABLE eb_merchant ADD COLUMN `legal_person_id_number` VARCHAR(20) DEFAULT NULL COMMENT '[LQQ] 法人身份证号' AFTER `license_image`;
ALTER TABLE eb_merchant ADD COLUMN `legal_person_id_front` VARCHAR(500) DEFAULT NULL COMMENT '[LQQ] 法人身份证正面照URL' AFTER `legal_person_id_number`;
ALTER TABLE eb_merchant ADD COLUMN `legal_person_id_back` VARCHAR(500) DEFAULT NULL COMMENT '[LQQ] 法人身份证反面照URL' AFTER `legal_person_id_front`;

-- 统计字段
ALTER TABLE eb_merchant ADD COLUMN `review_score` DECIMAL(3,1) DEFAULT 5.0 COMMENT '[LQQ] 评论平均分(1-5)' AFTER `legal_person_id_back`;
ALTER TABLE eb_merchant ADD COLUMN `review_count` INT DEFAULT 0 COMMENT '[LQQ] 评论数量' AFTER `review_score`;
ALTER TABLE eb_merchant ADD COLUMN `view_count` INT DEFAULT 0 COMMENT '[LQQ] 浏览量' AFTER `review_count`;
