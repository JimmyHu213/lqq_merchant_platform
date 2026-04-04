-- ----------------------------
-- Table structure for eb_merchant_member_benefits
-- ----------------------------
DROP TABLE IF EXISTS `eb_merchant_member_benefits`;
CREATE TABLE `eb_merchant_member_benefits`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '会员权益ID',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '权益名称',
  `selected_icon` varchar(256) NOT NULL DEFAULT '' COMMENT '选中图标',
  `unselected_icon` varchar(256) NOT NULL DEFAULT '' COMMENT '未选中图标',
  `link` varchar(256) NOT NULL DEFAULT '' COMMENT '跳转链接',
  `sort` smallint NOT NULL DEFAULT 0 COMMENT '排序',
  `can_del` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否能够删除',
  `is_del` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户会员权益表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_merchant_member_level
-- ----------------------------
DROP TABLE IF EXISTS `eb_merchant_member_level`;
CREATE TABLE `eb_merchant_member_level`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '会员等级ID',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `level` tinyint(2) UNSIGNED NOT NULL DEFAULT 0 COMMENT '等级',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '会员名称',
  `threshold_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '门槛金额',
  `benefits` varchar(255) NOT NULL DEFAULT '' COMMENT '会员权益',
  `coupon_ids` varchar(64) NOT NULL DEFAULT '' COMMENT '优惠券ids',
  `is_del` tinyint(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否删除',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE,
  INDEX `level`(`level` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户会员等级表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_merchant_member
-- ----------------------------
DROP TABLE IF EXISTS `eb_merchant_member`;
CREATE TABLE `eb_merchant_member`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '会员ID',
  `uid` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户uid',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `level` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '会员等级',
  `financial_status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '财务状态:0-冻结，1-正常',
  `is_logoff` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否注销',
  `logoff_time` timestamp NULL DEFAULT NULL COMMENT '注销时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `level`(`level` ASC) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户会员表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_shopping_credits_package
-- ----------------------------
DROP TABLE IF EXISTS `eb_shopping_credits_package`;
CREATE TABLE `eb_shopping_credits_package`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '套餐ID',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `recharge_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '充值金额',
  `gift_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
  `sort` smallint NOT NULL DEFAULT 0 COMMENT '排序',
  `show_status` tinyint(1) UNSIGNED NOT NULL DEFAULT 1 COMMENT '显示状态:0-关闭，1-展示',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物金套餐' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_shopping_credits_order
-- ----------------------------
DROP TABLE IF EXISTS `eb_shopping_credits_order`;
CREATE TABLE `eb_shopping_credits_order`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `uid` int NULL DEFAULT NULL COMMENT '充值用户UID',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '订单号',
  `recharge_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '充值金额',
  `gift_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
  `pay_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付方式:weixin,alipay',
  `pay_channel` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付渠道：public-公众号,mini-小程序，h5-网页支付,wechatIos-微信Ios，wechatAndroid-微信Android,alipay-支付包，alipayApp-支付宝App',
  `paid` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否支付',
  `pay_time` timestamp NULL DEFAULT NULL COMMENT '充值支付时间',
  `out_trade_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '支付服务方订单号',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '充值时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_wechat_shipping` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否微信小程序发货',
  `refund_status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '退款状态：0 未退款 1 申请中 2 已退款',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `pay_type`(`pay_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物金订单' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for eb_shopping_credits_refund_order
-- ----------------------------
DROP TABLE IF EXISTS `eb_shopping_credits_refund_order`;
CREATE TABLE `eb_shopping_credits_refund_order`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `refund_order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '退款订单号',
  `order_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '订单号',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户ID',
  `uid` int UNSIGNED NOT NULL COMMENT '用户id',
  `refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款原因',
  `is_all` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否整单退款',
  `refund_status` tinyint NOT NULL DEFAULT 0 COMMENT '售后状态：0:待审核 1:商家拒绝 2：退款中 3:已退款 4:用户撤销',
  `refusing_refund_reason` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '拒绝退款说明',
  `refund_amount` decimal(8, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '退款金额',
  `refund_gift_amount` decimal(8, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '退赠送金额',
  `audit_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '审核员id',
  `audit_type` int NOT NULL DEFAULT 0 COMMENT '0-系统，1-平台管理员，2-商户管理员，3-移动端商户管理员',
  `audit_time` timestamp NULL DEFAULT NULL COMMENT '审核时间',
  `refund_time` timestamp NULL DEFAULT NULL COMMENT '退款时间',
  `refund_pay_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '退款渠道类型:weixin,alipay',
  `mer_remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商户备注',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `refund_order_no`(`refund_order_no` ASC) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE =  InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '购物金退款单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_merchant_user
-- ----------------------------
DROP TABLE IF EXISTS `eb_merchant_user`;
CREATE TABLE `eb_merchant_user`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT,
  `uid` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '用户uid',
  `mer_id` int UNSIGNED NOT NULL DEFAULT 0 COMMENT '商户id',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商户用户表' ROW_FORMAT = DYNAMIC;


-- ----------------------------
-- Table structure for eb_user_shopping_credits_record
-- ----------------------------
DROP TABLE IF EXISTS `eb_user_shopping_credits_record`;
CREATE TABLE `eb_user_shopping_credits_record`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '记录id',
  `uid` int NOT NULL DEFAULT 0 COMMENT '用户uid',
  `mer_id` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `link_no` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '关联单号（订单号、充值单号、退款单号）',
  `link_type` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '0' COMMENT '关联类型（order,recharge,refund）',
  `type` int NOT NULL DEFAULT 1 COMMENT '类型：1-增加，2-扣减',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '标题',
  `recharge_amount` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '充值金额',
  `gift_amount` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE,
  INDEX `type`(`type` ASC) USING BTREE,
  INDEX `link_no`(`link_no` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户购物金记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Table structure for eb_user_shopping_credits
-- ----------------------------
DROP TABLE IF EXISTS `eb_user_shopping_credits`;
CREATE TABLE `eb_user_shopping_credits`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT 'id',
  `uid` int NOT NULL DEFAULT 0 COMMENT '用户uid',
  `mer_id` int NOT NULL DEFAULT 0 COMMENT '商户ID',
  `recharge_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '充值金额',
  `gift_amount` decimal(10, 2) UNSIGNED NOT NULL DEFAULT 0.00 COMMENT '赠送金额',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `uid`(`uid` ASC) USING BTREE,
  INDEX `mer_id`(`mer_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '用户购物金表' ROW_FORMAT = DYNAMIC;

ALTER TABLE `eb_product` CHANGE `is_paid_member` `vip_price_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '会员价类型：0-无，1-svip价，2-商户会员价';
ALTER TABLE `eb_order_detail` CHANGE `is_paid_member_product` `vip_price_type` tinyint(2) NOT NULL DEFAULT 0 COMMENT '会员价类型：0-无，1-svip价，2-商户会员价';
ALTER TABLE `eb_order` ADD COLUMN `merchant_member_discount_price` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '商户会员优惠金额';
ALTER TABLE `eb_merchant_order` ADD COLUMN `merchant_member_discount_price` decimal(8, 2) NOT NULL DEFAULT 0.00 COMMENT '商户会员优惠金额';
ALTER TABLE `eb_coupon` ADD COLUMN `identity_type` int(3) NOT NULL DEFAULT 0 COMMENT '身份类型：0-普通，1-商户会员';
ALTER TABLE `eb_system_config` ADD COLUMN `mer_id` int NOT NULL DEFAULT 0 COMMENT '商户ID';

ALTER TABLE `eb_user_closing` ADD COLUMN `is_merchant_transfer` int(2) NOT NULL DEFAULT 0 COMMENT '是否商家转账：0-否 1-是';

-- ----------------------------
-- Table structure for eb_product_unit
-- ----------------------------
DROP TABLE IF EXISTS `eb_product_unit`;
CREATE TABLE `eb_product_unit`  (
  `id` int UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `mer_id` int UNSIGNED NOT NULL COMMENT '商户ID',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '商品单位',
  `sort` int NOT NULL DEFAULT 1 COMMENT '排序',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品单位表' ROW_FORMAT = DYNAMIC;

-- 次卡模块
ALTER TABLE `eb_product` MODIFY COLUMN `type` int NOT NULL DEFAULT 0 COMMENT '基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品,7-预约商品，8-次卡商品';
ALTER TABLE `eb_product_attr_value` MODIFY COLUMN `type` int NOT NULL DEFAULT 0 COMMENT '基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品,7-预约商品，8-次卡商品';
ALTER TABLE `eb_product_description` MODIFY COLUMN `type` int NOT NULL DEFAULT 0 COMMENT '基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品,7-预约商品，8-次卡商品';
ALTER TABLE `eb_order` MODIFY COLUMN `second_type` int NOT NULL DEFAULT 0 COMMENT '订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单,7-预约订单，8-次卡订单';
ALTER TABLE `eb_merchant_order` MODIFY COLUMN `second_type` int NOT NULL DEFAULT 0 COMMENT '订单二级类型:0-普通订单，1-积分订单，2-虚拟订单，4-视频号订单，5-云盘订单，6-卡密订单,7-预约订单，8-次卡订单';
ALTER TABLE `eb_order_detail` MODIFY COLUMN `product_type` int NOT NULL DEFAULT 0 COMMENT '基础类型：0=普通商品,1-积分商品,2-虚拟商品,4=视频号,5-云盘商品,6-卡密商品,7-预约商品，8-次卡商品';

ALTER TABLE `eb_product` ADD COLUMN `verify_quantity` int(11) NOT NULL DEFAULT 0 COMMENT '核销次数';
ALTER TABLE `eb_product` ADD COLUMN `verify_time_limit_type` int(2) NOT NULL DEFAULT 1 COMMENT '核销期限类型：1-无期限，2-几天后，3-日期范围';
ALTER TABLE `eb_product` ADD COLUMN `verify_time_limit_day` int(11) NOT NULL DEFAULT 0 COMMENT '核销期限天数';
ALTER TABLE `eb_product` ADD COLUMN `verify_time_limit_start_date` varchar(64) NOT NULL DEFAULT 0 COMMENT '核销期限日期范围-开始日期';
ALTER TABLE `eb_product` ADD COLUMN `verify_time_limit_end_date` varchar(64) NOT NULL DEFAULT 0 COMMENT '核销期限日期范围-结束日期';

ALTER TABLE `eb_order_detail` ADD COLUMN `verify_total_times` int(11) NOT NULL DEFAULT 0 COMMENT '次卡总次数';
ALTER TABLE `eb_order_detail` ADD COLUMN `verify_remaining_times` int(11) NOT NULL DEFAULT 0 COMMENT '次卡剩余次数';
ALTER TABLE `eb_order_detail` ADD COLUMN `verify_start_date` varchar(64) NOT NULL DEFAULT ''COMMENT '次卡生效开始日期';
ALTER TABLE `eb_order_detail` ADD COLUMN `verify_end_date` varchar(64) NOT NULL DEFAULT ''COMMENT '次卡生效结束日期';

ALTER TABLE `eb_verification_record` ADD COLUMN `verify_times` int(11) NOT NULL DEFAULT 1 COMMENT '本次核销消耗的次数';

ALTER TABLE `eb_refund_order_info` ADD COLUMN `refund_verify_times` int(11) NOT NULL DEFAULT 0 COMMENT '退款次卡剩余次数';

-- 统计模块
ALTER TABLE `eb_shopping_product_day_record` ADD COLUMN `refund_product_num` int DEFAULT NULL COMMENT '退款商品数';

ALTER TABLE `eb_trading_day_record` ADD COLUMN `product_order_coupon_fee` decimal(8,2)  DEFAULT NULL COMMENT '订单用劵金额';

ALTER TABLE `eb_product_day_record` CHANGE COLUMN `order_product_num` `order_success_product_num` int COMMENT '下单成功商品数（销售件数）';
ALTER TABLE `eb_product_day_record` ADD COLUMN `order_product_num` int DEFAULT NULL COMMENT '下单商品总数';
ALTER TABLE `eb_product_day_record` ADD COLUMN `refund_product_num` int DEFAULT NULL COMMENT '退款商品数';

INSERT INTO `eb_merchant_member_benefits` (`id`, `mer_id`, `name`, `selected_icon`, `unselected_icon`, `sort`, `can_del`, `is_del`, `create_time`, `update_time`, `link`) VALUES (1, 0, '会员专享价', 'crmebimage/presets/icon/会员专享价-选中@3x.png', 'crmebimage/presets/icon/会员专享价-未选中@3x.png', 1, 0, 0, '2025-12-01 16:11:31', '2026-01-20 14:17:22', '');
INSERT INTO `eb_merchant_member_benefits` (`id`, `mer_id`, `name`, `selected_icon`, `unselected_icon`, `sort`, `can_del`, `is_del`, `create_time`, `update_time`, `link`) VALUES (2, 0, '会员专享券', 'crmebimage/presets/icon/会员专享券-选中@3x.png', 'crmebimage/presets/icon/会员专享券-未选中@3x.png', 2, 0, 0, '2025-12-01 16:11:46', '2026-01-20 14:18:10', '');
INSERT INTO `eb_merchant_member_benefits` (`id`, `mer_id`, `name`, `selected_icon`, `unselected_icon`, `sort`, `can_del`, `is_del`, `create_time`, `update_time`, `link`) VALUES (3, 0, '购物金', 'crmebimage/presets/icon/入会有礼-选中@3x-2.png', 'crmebimage/presets/icon/入会有礼-未选中@3x-2.png', 3, 0, 0, '2025-12-01 16:12:16', '2026-01-20 14:18:19', '');
INSERT INTO `eb_merchant_member_benefits` (`id`, `mer_id`, `name`, `selected_icon`, `unselected_icon`, `sort`, `can_del`, `is_del`, `create_time`, `update_time`, `link`) VALUES (4, 0, '入会/升级有礼', 'crmebimage/presets/icon/入会有礼-选中@3x.png', 'crmebimage/presets/icon/入会有礼-未选中@3x.png', 4, 0, 0, '2025-12-01 16:12:25', '2026-01-20 14:18:41', '');

INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1237, 90, '分销基础配置信息获取', '', 'platform:retail:store:base:config:get', '', 'A', 0, 1, 0, 3, '2025-11-26 15:43:26', '2025-11-26 15:43:26');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1238, 90, '分销基础配置信息保存', '', 'platform:retail:store:base:config:save', '', 'A', 0, 1, 0, 3, '2025-11-26 15:43:39', '2025-11-26 15:43:39');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1239, 90, '分销提现配置信息获取', '', 'platform:retail:store:extract:config:get', '', 'A', 0, 1, 0, 3, '2025-11-26 15:43:51', '2025-11-26 15:43:51');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1240, 90, '分销提现配置信息保存', '', 'platform:retail:store:extract:config:save', '', 'A', 0, 1, 0, 3, '2025-11-26 15:44:05', '2025-11-26 15:44:05');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1262, 61, '店铺会员注册与授权协议保存', '', 'platform:system:agreement:shop:member:card:registration:authorization:save', '', 'A', 0, 1, 0, 3, '2025-12-09 10:50:13', '2025-12-09 10:50:13');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1263, 61, '店铺会员注册与授权协议详情', '', 'platform:system:agreement:shop:member:card:registration:authorization:info', '', 'A', 0, 1, 0, 3, '2025-12-09 10:50:30', '2025-12-09 10:50:30');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1265, 1, '商品统计', '', '', '/dashboard/statistic/product', 'C', 1, 1, 0, 3, '2025-12-11 15:04:33', '2025-12-12 09:03:57');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1266, 1265, '顶部核心数据统计', '', 'platform:product:statistics:top', '', 'A', 0, 1, 0, 3, '2025-12-11 15:04:58', '2025-12-11 15:04:58');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1267, 1265, '折线趋势图统计', '', 'platform:product:statistics:chart', '', 'A', 0, 1, 0, 3, '2025-12-11 15:05:19', '2025-12-11 15:05:19');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1268, 1265, '商品分类统计', '', 'platform:product:statistics:category', '', 'A', 0, 1, 0, 3, '2025-12-11 15:05:37', '2025-12-11 15:05:37');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1269, 1265, '商品类型统计', '', 'platform:product:statistics:type', '', 'A', 0, 1, 0, 3, '2025-12-11 15:05:55', '2025-12-11 15:05:55');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1270, 1, '控制台', '', '', '/dashboard', 'C', 9, 1, 0, 3, '2025-12-12 09:36:01', '2025-12-12 09:36:01');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1271, 1, '订单统计', '', '', '/dashboard/statistic/order', 'C', 1, 1, 0, 3, '2025-12-17 17:58:49', '2025-12-17 17:59:22');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1272, 1271, '顶部核心数据统计', '', 'platform:order:statistics:top', '', 'A', 0, 1, 0, 3, '2025-12-17 17:59:51', '2025-12-17 17:59:51');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1273, 1271, '折线趋势图统计', '', 'platform:order:statistics:chart', '', 'A', 0, 1, 0, 3, '2025-12-17 18:00:17', '2025-12-17 18:00:17');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1274, 1271, '订单类型统计', '', 'platform:order:statistics:type', '', 'A', 0, 1, 0, 3, '2025-12-17 18:00:36', '2025-12-17 18:01:31');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1275, 1271, '订单发货统计', '', 'platform:order:statistics:shipping', '', 'A', 0, 1, 0, 3, '2025-12-17 18:00:56', '2025-12-17 18:01:17');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1276, 1, '用户统计', '', '', '/dashboard/statistic/user', 'C', 1, 1, 0, 3, '2025-12-23 16:59:14', '2025-12-23 17:00:29');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1277, 1276, '顶部核心数据统计', '', 'platform:user:statistics:top', '', 'A', 0, 1, 0, 3, '2025-12-23 17:01:20', '2025-12-23 17:01:20');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1278, 1276, '新增用户数量统计', '', 'platform:user:statistics:add', '', 'A', 0, 1, 0, 3, '2025-12-23 17:01:43', '2025-12-23 17:01:43');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1279, 1276, '活跃用户数量统计', '', 'platform:user:statistics:alive', '', 'A', 0, 1, 0, 3, '2025-12-23 17:02:03', '2025-12-23 17:02:03');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1280, 1276, '新增付费会员数量统计', '', 'platform:user:statistics:member', '', 'A', 0, 1, 0, 3, '2025-12-23 17:02:23', '2025-12-23 17:02:23');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1281, 1276, '成交用户数量统计', '', 'platform:user:statistics:bought', '', 'A', 0, 1, 0, 3, '2025-12-23 17:02:43', '2025-12-23 17:02:43');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1314, 61, '消费者权益保障说明 保存', '', 'platform:system:agreement:consumer:rights:protection:save', '', 'A', 0, 1, 0, 3, '2026-01-26 15:35:01', '2026-01-26 15:35:01');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1315, 61, '消费者权益保障说明 详情', '', 'platform:system:agreement:consumer:rights:protection:info', '', 'A', 0, 1, 0, 3, '2026-01-26 15:35:21', '2026-01-26 15:35:21');

INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1230, 172, '商品单位', '', '', '/product/unit', 'C', 1, 1, 0, 4, '2025-11-22 10:51:12', '2025-11-22 10:51:26');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1231, 174, '会员', '', '', '/member', 'M', 6000, 1, 0, 4, '2025-11-25 11:11:51', '2026-01-21 10:18:30');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1232, 1231, '会员概览', '', '', '/member/overview', 'C', 10, 1, 0, 4, '2025-11-25 11:22:22', '2025-11-28 16:33:45');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1233, 1231, '用户列表', '', '', '/member/user', 'C', 1, 0, 0, 4, '2025-11-25 14:31:53', '2026-01-12 11:20:11');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1234, 1231, '会员等级', '', '', '/member/level', 'C', 9, 1, 0, 4, '2025-11-25 14:32:37', '2025-11-28 16:33:55');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1236, 1231, '会员协议', '', '', '/member/agreement', 'C', 6, 1, 0, 4, '2025-11-25 14:37:36', '2025-11-28 16:41:50');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1241, 1230, '商品单位列表', '', 'merchant:product:unit:list', '', 'A', 1, 1, 0, 4, '2025-11-27 09:22:08', '2025-11-27 09:22:08');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1242, 1230, '新增', '', 'merchant:product:unit:save', '', 'A', 1, 1, 0, 4, '2025-11-27 09:22:32', '2025-11-27 09:22:32');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1243, 1230, '删除', '', 'merchant:product:unit:delete', '', 'A', 1, 1, 0, 4, '2025-11-27 09:22:53', '2025-11-27 09:22:53');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1244, 1230, '修改', '', 'merchant:product:unit:update', '', 'A', 1, 1, 0, 4, '2025-11-27 09:23:13', '2025-11-27 09:23:13');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1245, 1230, '详情', '', 'merchant:product:unit:info', '', 'A', 1, 1, 0, 4, '2025-11-27 09:23:33', '2025-11-27 09:23:33');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1246, 1231, '会员权益', '', '', '/member/benefit', 'C', 8, 1, 0, 4, '2025-11-28 16:36:23', '2025-11-28 16:38:23');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1247, 174, '购物金', '', '', '/member/shoppingCredit', 'M', 7, 1, 0, 4, '2025-11-28 16:38:12', '2026-01-22 15:23:49');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1248, 1247, '充值套餐', '', '', '/member/shoppingCredit/settings', 'C', 1, 1, 0, 4, '2025-11-28 16:39:49', '2025-12-01 17:17:47');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1249, 1247, '充值订单', '', '', '/member/shoppingCredit/order', 'C', 1, 1, 0, 4, '2025-11-28 16:40:36', '2025-11-28 16:40:36');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1250, 1247, '充值退款', '', '', '/member/shoppingCredit/refund', 'C', 1, 1, 0, 4, '2025-11-28 16:41:28', '2025-11-28 16:41:28');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1251, 1246, '会员权益列表', '', 'merchant:member:benefits:list', '', 'A', 1, 1, 0, 4, '2025-12-02 16:48:50', '2025-12-02 16:48:50');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1252, 1246, '会员权益保存', '', 'merchant:member:benefits:save', '', 'A', 1, 1, 0, 4, '2025-12-02 16:49:05', '2025-12-02 16:49:05');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1253, 1234, '会员等级列表', '', 'merchant:member:level:list', '', 'A', 1, 1, 0, 4, '2025-12-03 16:41:25', '2025-12-03 16:41:25');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1254, 1234, '会员等级新增', '', 'merchant:member:level:add', '', 'A', 1, 1, 0, 4, '2025-12-03 16:41:41', '2025-12-03 16:41:41');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1255, 1234, '会员等级编辑', '', 'merchant:member:level:update', '', 'A', 1, 1, 0, 4, '2025-12-03 16:41:58', '2025-12-03 16:41:58');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1256, 1234, '会员等级删除', '', 'merchant:member:level:delete', '', 'A', 1, 1, 0, 4, '2025-12-03 16:42:11', '2025-12-03 16:42:11');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1257, 1248, '购物金套餐列表', '', 'merchant:shipping:credits:package:list', '', 'A', 1, 1, 0, 4, '2025-12-05 09:40:00', '2025-12-05 09:40:00');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1258, 1248, '购物金套餐添加', '', 'merchant:shipping:credits:package:add', '', 'A', 1, 1, 0, 4, '2025-12-05 09:40:17', '2025-12-05 09:40:17');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1259, 1248, '购物金套餐编辑', '', 'merchant:shipping:credits:package:update', '', 'A', 1, 1, 0, 4, '2025-12-05 09:40:31', '2025-12-05 09:40:31');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1260, 1248, '购物金套餐删除', '', 'merchant:shipping:credits:package:delete', '', 'A', 1, 1, 0, 4, '2025-12-05 09:40:47', '2025-12-05 09:40:47');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1261, 1248, '购物金套餐显示状态变更', '', 'merchant:shipping:credits:package:show:update', '', 'A', 1, 1, 0, 4, '2025-12-05 09:41:10', '2025-12-05 09:41:10');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1264, 1249, '购物金订单分页列表', '', 'merchant:shipping:credits:order:page', '', 'A', 1, 1, 0, 4, '2025-12-09 14:51:56', '2025-12-09 14:51:56');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1282, 1009, '底部导航', '', '', '/design/bottomNavigation', 'C', 1, 1, 0, 4, '2025-12-26 17:25:28', '2025-12-26 17:27:01');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1283, 1282, '页面底部导航查询', '', 'merchant:page:layout:bottom:navigation', '', 'A', 1, 1, 0, 4, '2025-12-26 17:47:11', '2025-12-26 17:47:11');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1284, 1282, '底部导航保存', '', 'merchant:page:layout:bottom:navigation:save', '', 'A', 1, 1, 0, 4, '2025-12-26 17:47:32', '2025-12-26 17:47:32');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1285, 171, '商品统计', '', '', '/dashboard/statistic/product', 'C', 1, 1, 0, 4, '2025-12-30 15:33:21', '2025-12-30 16:34:48');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1286, 1285, '顶部核心数据统计', '', 'merchant:product:statistics:top', '', 'A', 1, 1, 0, 4, '2025-12-30 15:33:52', '2025-12-30 15:33:52');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1287, 1285, '折线趋势图统计', '', 'merchant:product:statistics:chart', '', 'A', 1, 1, 0, 4, '2025-12-30 15:34:16', '2025-12-30 15:34:16');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1288, 1285, '商品分类统计', '', 'merchant:product:statistics:category', '', 'A', 1, 1, 0, 4, '2025-12-30 15:34:38', '2025-12-30 15:34:38');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1289, 1285, '商品类型统计', '', 'merchant:product:statistics:type', '', 'A', 1, 1, 0, 4, '2025-12-30 15:34:56', '2025-12-30 15:34:56');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1290, 171, '控制台', '', '', '/dashboard', 'C', 9, 1, 0, 4, '2025-12-31 11:42:04', '2025-12-31 11:43:22');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1291, 171, '订单统计', '', '', '/dashboard/statistic/order', 'C', 1, 1, 0, 4, '2025-12-31 11:43:42', '2025-12-31 11:45:27');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1292, 1291, '顶部核心数据统计', '', 'merchant:order:statistics:top', '', 'A', 1, 1, 0, 4, '2025-12-31 11:44:00', '2025-12-31 11:44:00');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1293, 1291, '折线趋势图统计', '', 'merchant:order:statistics:chart', '', 'A', 1, 1, 0, 4, '2025-12-31 11:44:19', '2025-12-31 11:44:19');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1294, 1291, '订单类型统计', '', 'merchant:order:statistics:type', '', 'A', 1, 1, 0, 4, '2025-12-31 11:44:35', '2025-12-31 11:44:35');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1295, 1291, '订单发货统计', '', 'merchant:order:statistics:shipping', '', 'A', 1, 1, 0, 4, '2025-12-31 11:44:53', '2025-12-31 11:44:53');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1296, 1250, '购物金退款订单分页列表', '', 'merchant:shipping:credits:refund:order:page', '', 'A', 1, 1, 0, 4, '2026-01-08 11:16:40', '2026-01-08 11:16:40');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1297, 1250, '购物金退款订单详情', '', 'merchant:shipping:credits:refund:order:info', '', 'A', 1, 1, 0, 4, '2026-01-08 11:17:01', '2026-01-08 11:17:01');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1298, 1236, '商户会员开卡协议详情', '', 'merchant:agreement:member:open:card:info', '', 'A', 1, 1, 0, 4, '2026-01-12 11:13:45', '2026-01-12 11:13:45');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1299, 1236, '商户会员开卡协议保存', '', 'merchant:agreement:member:open:card:save', '', 'A', 1, 1, 0, 4, '2026-01-12 11:14:09', '2026-01-12 11:14:09');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1300, 1250, '购物金退款订单审核', '', 'merchant:shipping:credits:refund:order:audit', '', 'A', 1, 1, 0, 4, '2026-01-12 11:22:53', '2026-01-12 11:22:53');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1301, 1232, '商户会员概览', '', 'merchant:member:statistics:overview', '', 'A', 1, 1, 0, 4, '2026-01-12 16:38:30', '2026-01-12 16:38:30');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1302, 1232, '商户会员环比数据', '', 'merchant:member:statistics:mom:data', '', 'A', 1, 1, 0, 4, '2026-01-12 16:38:46', '2026-01-12 16:38:46');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1303, 1232, '商户会员新增趋势数据', '', 'merchant:member:statistics:new:trend:data', '', 'A', 1, 1, 0, 4, '2026-01-12 16:38:57', '2026-01-12 16:38:57');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1304, 1232, '商户会员消费趋势数据', '', 'merchant:member:statistics:consume:trend:data', '', 'A', 1, 1, 0, 4, '2026-01-12 16:39:31', '2026-01-12 16:39:31');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1305, 188, '商户用户购物金记录', '', 'merchant:user:shopping:credits:record', '', 'A', 1, 1, 0, 4, '2026-01-19 12:07:42', '2026-01-19 12:07:42');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1306, 188, '商户用户购物金冻结/解冻', '', 'merchant:user:shopping:credits:freeze:update', '', 'A', 1, 1, 0, 4, '2026-01-19 14:31:12', '2026-01-19 14:31:12');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1307, 195, '商户会员可用优惠券列表', '', 'merchant:coupon:member:usable:list', '', 'A', 1, 1, 0, 4, '2026-01-22 10:40:31', '2026-01-22 10:40:31');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1308, 171, '用户统计', '', '', '/dashboard/statistic/user', 'C', 1, 1, 0, 4, '2026-01-22 15:27:04', '2026-01-22 15:27:22');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1309, 1308, '顶部核心数据统计', '', 'merchant:user:statistics:top', '', 'A', 1, 1, 0, 4, '2026-01-22 15:27:42', '2026-01-22 15:27:42');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1310, 1308, '新增用户数量统计', '', 'merchant:user:statistics:add', '', 'A', 1, 1, 0, 4, '2026-01-22 15:28:04', '2026-01-22 15:28:04');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1311, 1308, '活跃用户数量统计', '', 'merchant:user:statistics:alive', '', 'A', 1, 1, 0, 4, '2026-01-22 15:28:21', '2026-01-22 15:28:21');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1312, 1308, '新增商户会员数量统计', '', 'merchant:user:statistics:member', '', 'A', 1, 1, 0, 4, '2026-01-22 15:28:55', '2026-01-22 15:28:55');
INSERT INTO `eb_system_menu` (`id`, `pid`, `name`, `icon`, `perms`, `component`, `menu_type`, `sort`, `is_show`, `is_delte`, `type`, `create_time`, `update_time`) VALUES (1313, 1308, '成交用户数量统计', '', 'merchant:user:statistics:bought', '', 'A', 1, 1, 0, 4, '2026-01-22 15:29:17', '2026-01-22 15:29:17');

INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1237);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1238);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1239);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1240);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1262);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1263);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1265);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1266);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1267);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1268);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1269);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1270);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1271);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1272);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1273);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1274);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1275);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1276);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1277);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1278);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1279);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1280);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1281);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1314);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (1, 1315);

INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1230);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1231);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1232);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1233);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1234);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1236);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1241);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1242);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1243);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1244);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1245);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1246);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1247);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1248);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1249);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1250);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1251);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1252);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1253);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1254);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1255);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1256);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1257);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1258);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1259);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1260);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1261);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1264);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1282);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1283);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1284);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1285);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1286);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1287);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1288);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1289);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1290);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1291);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1292);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1293);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1294);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1295);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1296);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1297);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1298);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1299);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1300);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1301);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1302);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1303);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1304);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1305);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1306);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1307);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1308);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1309);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1310);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1311);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1312);
INSERT INTO `eb_system_role_menu` (`rid`, `menu_id`) VALUES (2, 1313);


INSERT INTO `eb_system_config` (`id`, `name`, `title`, `form_id`, `value`, `status`, `create_time`, `update_time`, `form_name`, `mer_id`) VALUES (null, 'consumerRightsProtectionAgreement', '', 0, '{\"agreement\":\"<p>消费者权益保障说明</p>\\n<p>&nbsp;</p>\"}', 0, '2026-01-26 15:42:00', '2026-01-29 12:05:01', '', 0);
INSERT INTO `eb_system_config` (`id`, `name`, `title`, `form_id`, `value`, `status`, `create_time`, `update_time`, `form_name`, `mer_id`) VALUES (null, 'shopMemberCardRegistrationAuthorizationAgreement', '', 0, '{\"agreement\":\"<div class=\\\"lake-content\\\">\\n<p id=\\\"u8e391bfb\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">会员授权协议</span></p>\\n<p id=\\\"ufdb9f041\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">《会员授权协议》（以下简称&ldquo;本协议&rdquo;）是众邦、商家及用户所订立的协议，以约束商家与用户间就会员卡开通所享有的权利义务进行的约定及众邦、商家与用户间就个人信息授权使用及保护的有关约定。</span></p>\\n<p id=\\\"ud33afab1\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">您通过网络页面勾选确认本协议，即视为您同意接受本协议的全部内容。您在接受本协议前请仔细阅读协议全部内容，如不同意本协议内容，或无法准确理解协议条文的含义，请不要进行确认或后续系统操作。</span></p>\\n<p id=\\\"uafbe29b2\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">您同意，众邦有权随时对本协议内容进行变更，并以众邦平台的相关渠道通知用户予以公示，无需单独另行通知，变更的公告自公告载明的时间生效，并成为协议的一部分。如您不同意变更规则，则应暂停使用本协议约定服务。</span></p>\\n<p id=\\\"u1c82d677\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">定义</span></p>\\n<p id=\\\"uca05a195\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">1、会员：指以众邦平台入驻商家开通的店铺维度或品牌维度进行的会员体系管理，用户主动授权加入会员领取会员卡，且满足商家设定的等级规则即是取得相应等级的会员资格，获得相应的权益。</span></p>\\n<p id=\\\"u40981b28\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">2、会员等级规则：会员等级规则由每个商家自行维护并设定公示，用户可在会员中心页内查看会员规则。</span></p>\\n<p id=\\\"u9319643d\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">3、开卡须知：由用户在开通会员时，与商家共同签署的有关商家会员服务的协议，主要约定商家为用户提供会员服务的内容。</span></p>\\n<p id=\\\"u60229856\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">用户个人信息授权：</span></p>\\n<p id=\\\"ud01e0d1b\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">您授权众邦将您的信息（包括姓名、性别、生日、邮箱、众邦pin、手机号、会员积分、等级、会员周期消费累计值）进行存储、展示，并提供给当前店铺及/或该品牌方（以下简称&ldquo;商家&rdquo;），用于商家为您计算正确的会员权益、并为您提供会员服务。同时，您知悉并同意向您推送商业信息，包括发送邮件、短信、致电、营销权益发放及其他营销方式（如需退订，可依据信息提供步骤完成）。众邦商家将依法使用您的上述信息，并对您的信息予以保密，如您不希望向商家提供以上信息的，请勿点击&ldquo;开通会员&rdquo;。请您注意仔细阅读，确定了解商家向您提供的《开卡须知》。</span></p>\\n<p id=\\\"u6788a503\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">注销提示：</span></p>\\n<p id=\\\"uc803b4f1\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">如您注销众邦账户，您与众邦平台商家会员的线上绑定关系将同时解除，您已获得的会员权益（含积分、等级等）将同步注销且不可恢复。</span></p>\\n<p id=\\\"u915df4a7\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">责任承担：</span></p>\\n<p id=\\\"u4fb09446\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">您知悉众邦是会员系统功能的中立技术服务方，就会员服务由用户与商家自行按约履行，并各自承担责任，如发生争议，由您与店铺经营者自行协商解决。</span></p>\\n<p id=\\\"uf7280885\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">关于技术服务可能会涉及商家或商家的第三方技术服务商进行技术对接。因品牌商或品牌商的第三方技术服务商使用您的信息产生的纠纷，或者品牌商或品牌商的第三方技术服务商违反相关法律法规，或您在使用服务过程中遇到了经济损失，请您和商家协商解决。</span></p>\\n<p id=\\\"u7b0172e1\\\" class=\\\"ne-p\\\"><span class=\\\"ne-text\\\">您同意，众邦有权按照法律规定对本协议内容进行变更，协议将在公示七日后生效。本协议未尽事宜参照《众邦用户注册协议》及《隐私政策》执行。</span></p>\\n</div>\"}', 0, '2026-01-13 14:14:18', '2026-01-29 12:00:44', '', 0);


UPDATE `eb_system_form_temp` SET `name` = '修改剩余采集次数', `info` = '修改复制商品数量', `content` = '{\"formRef\":\"elForm\",\"formModel\":\"formData\",\"size\":\"small\",\"labelPosition\":\"right\",\"labelWidth\":75,\"formRules\":\"rules\",\"gutter\":15,\"disabled\":false,\"span\":24,\"formBtns\":true,\"fields\":[{\"__config__\":{\"label\":\"采集次数：\",\"labelWidth\":null,\"showLabel\":true,\"changeTag\":true,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"required\":false,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"layout\":\"colFormItem\",\"span\":24,\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"regList\":[],\"formId\":101,\"renderKey\":1659407013038,\"defaultValue\":0},\"placeholder\":\"请输入采集次数\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":true,\"__vModel__\":\"copyProductNum\"},{\"__config__\":{\"label\":\"修改类型：\",\"labelWidth\":null,\"showLabel\":true,\"tag\":\"el-radio-group\",\"tagIcon\":\"radio\",\"changeTag\":true,\"layout\":\"colFormItem\",\"span\":24,\"optionType\":\"default\",\"regList\":[],\"required\":true,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"border\":false,\"document\":\"https://element.eleme.cn/#/zh-CN/component/radio\",\"formId\":102,\"renderKey\":1659407051644,\"defaultValue\":\"add\"},\"__slot__\":{\"options\":[{\"label\":\"增加\",\"value\":\"add\"},{\"label\":\"减少\",\"value\":\"sub\"}]},\"style\":{},\"size\":\"medium\",\"disabled\":false,\"__vModel__\":\"type\"},{\"__config__\":{\"label\":\"修改数量：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":24,\"layout\":\"colFormItem\",\"required\":true,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":103,\"renderKey\":1659407068415},\"placeholder\":\"修改数量：\",\"step\":1,\"step-strictly\":true,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"num\",\"min\":0,\"max\":9999}]}', `create_time` = '2022-08-02 10:26:13', `update_time` = '2026-01-15 11:08:45' WHERE `id` = 34;
UPDATE `eb_system_form_temp` SET `name` = '充值套餐', `info` = '充值套餐', `content` = '{\"formRef\":\"elForm\",\"formModel\":\"formData\",\"size\":\"medium\",\"labelPosition\":\"right\",\"labelWidth\":120,\"formRules\":\"rules\",\"gutter\":15,\"disabled\":false,\"span\":15,\"formBtns\":true,\"fields\":[{\"__config__\":{\"label\":\"充值金额：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":\"\",\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":15,\"layout\":\"colFormItem\",\"required\":true,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":101,\"renderKey\":1769154318879},\"placeholder\":\"请输入充值金额\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"price\",\"min\":0.01,\"precision\":2},{\"__config__\":{\"label\":\"赠送金额：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":24,\"layout\":\"colFormItem\",\"required\":true,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":102,\"renderKey\":1769154382855},\"placeholder\":\"请输入赠送金额\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"give_money\",\"min\":0,\"precision\":2}]}', `create_time` = '2022-10-13 12:30:12', `update_time` = '2026-01-23 15:48:44' WHERE `id` = 48;


INSERT INTO `eb_schedule_job` (`job_id`, `bean_name`, `method_name`, `params`, `cron_expression`, `status`, `remark`, `is_delete`, `create_time`) VALUES (44, 'CardTimesProductValidityTask', 'cardTimesProductValidity', NULL, '0 10 0 * * ?', 0, '次卡商品到期下架定时任务', 0, '2025-12-02 11:56:50');
INSERT INTO `eb_schedule_job` (`job_id`, `bean_name`, `method_name`, `params`, `cron_expression`, `status`, `remark`, `is_delete`, `create_time`) VALUES (45, 'CardTimesOrderValidityTask', 'cardTimesOrderValidity', NULL, '0 10 0 * * ?', 0, '次卡订单到期自动完成定时任务', 0, '2025-12-02 12:00:07');

INSERT INTO `eb_system_form_temp` (`id`, `name`, `info`, `content`, `create_time`, `update_time`) VALUES (68, 'productUnit', '商品单位', '{\"formRef\":\"elForm\",\"formModel\":\"formData\",\"size\":\"medium\",\"labelPosition\":\"right\",\"labelWidth\":80,\"formRules\":\"rules\",\"gutter\":19,\"disabled\":false,\"span\":19,\"formBtns\":true,\"fields\":[{\"__config__\":{\"label\":\"名称\",\"labelWidth\":null,\"showLabel\":true,\"changeTag\":true,\"tag\":\"el-input\",\"tagIcon\":\"input\",\"required\":true,\"layout\":\"colFormItem\",\"span\":19,\"document\":\"https://element.eleme.cn/#/zh-CN/component/input\",\"regList\":[],\"tips\":false},\"__slot__\":{\"prepend\":\"\",\"append\":\"\"},\"__vModel__\":\"name\",\"placeholder\":\"请输入单位名称\",\"style\":{\"width\":\"100%\"},\"clearable\":true,\"prefix-icon\":\"\",\"suffix-icon\":\"\",\"maxlength\":\"10\",\"show-word-limit\":true,\"readonly\":false,\"disabled\":false},{\"__config__\":{\"label\":\"排序\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":15,\"layout\":\"colFormItem\",\"required\":true,\"tips\":false,\"tipsDesc\":\"\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":101,\"renderKey\":1763955883650,\"defaultValue\":0},\"placeholder\":\"排序\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"sort\",\"min\":0,\"precision\":0}]}', '2025-11-24 11:45:07', '2026-01-22 10:05:26');
INSERT INTO `eb_system_form_temp` (`id`, `name`, `info`, `content`, `create_time`, `update_time`) VALUES (69, 'rechargePackage', '店铺会员充值套餐', '{\"formRef\":\"elForm\",\"formModel\":\"formData\",\"size\":\"medium\",\"labelPosition\":\"right\",\"labelWidth\":100,\"formRules\":\"rules\",\"gutter\":15,\"disabled\":false,\"span\":24,\"formBtns\":true,\"fields\":[{\"__config__\":{\"label\":\"充值金额：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":24,\"layout\":\"colFormItem\",\"required\":true,\"tips\":true,\"tipsDesc\":\"用户按照充值金额进行支付\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":101,\"renderKey\":1764579784093,\"defaultValue\":0.01},\"placeholder\":\"充值金额\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"rechargeAmount\",\"precision\":2,\"min\":0},{\"__config__\":{\"label\":\"赠送金额：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":24,\"layout\":\"colFormItem\",\"required\":true,\"tips\":true,\"tipsDesc\":\"用户按照充值该套餐。额外赠送的额度\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":102,\"renderKey\":1764579827981},\"placeholder\":\"赠送金额\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"giftAmount\",\"precision\":0,\"min\":0},{\"__config__\":{\"label\":\"排序：\",\"showLabel\":true,\"changeTag\":true,\"labelWidth\":null,\"tag\":\"el-input-number\",\"tagIcon\":\"number\",\"span\":24,\"layout\":\"colFormItem\",\"required\":true,\"tips\":true,\"tipsDesc\":\"数字越大越靠前\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"document\":\"https://element.eleme.cn/#/zh-CN/component/input-number\",\"formId\":103,\"renderKey\":1764579869028,\"defaultValue\":0},\"placeholder\":\"排序\",\"step\":1,\"step-strictly\":false,\"controls-position\":\"\",\"disabled\":false,\"__vModel__\":\"sort\",\"min\":0,\"precision\":0},{\"__config__\":{\"label\":\"商城显示：\",\"tag\":\"el-switch\",\"tagIcon\":\"switch\",\"defaultValue\":0,\"span\":24,\"showLabel\":true,\"labelWidth\":null,\"layout\":\"colFormItem\",\"required\":true,\"tips\":true,\"tipsDesc\":\"隐藏后，移动端商城不展示该套餐，用户则无法充值该套餐\",\"tipsIsLink\":false,\"tipsLink\":\"\",\"regList\":[],\"changeTag\":true,\"document\":\"https://element.eleme.cn/#/zh-CN/component/switch\",\"formId\":104,\"renderKey\":1764579906683},\"style\":{},\"disabled\":false,\"active-text\":\"\",\"inactive-text\":\"\",\"active-color\":null,\"inactive-color\":null,\"active-value\":1,\"inactive-value\":0,\"__vModel__\":\"showStatus\"}]}', '2025-12-01 17:12:43', '2026-01-05 17:06:46');

UPDATE `eb_system_menu` SET `pid` = 174, `name` = '用户列表', `icon` = '', `perms` = '', `component` = '/user/index', `menu_type` = 'C', `sort` = 6100, `is_show` = 1, `is_delte` = 0, `type` = 4, `create_time` = '2022-03-14 17:36:23', `update_time` = '2026-01-29 14:43:42' WHERE `id` = 188;

