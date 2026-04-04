-- ============================================================
-- [LQQ-迁移] 005: 订单数据迁移 (t_customer_order → eb_order)
-- 日期: 2026-04-03
-- 前提: 先执行 003(用户迁移) + 004(商户迁移) 确保 ID 映射存在
-- ============================================================

-- mlqApi 订单状态 → JAVA-MER 映射:
-- 101(未支付)→status=0, 102(用户取消)→status=9+cancel=2, 103(超时取消)→status=9+cancel=1
-- 201(已支付)→status=1+paid=1, 202(退款中)→status=1+refund=1, 203(退款完成)→status=9+refund=3
-- 301(已发货)→status=4, 401(已收货)→status=5, 402(自动收货)→status=5

INSERT INTO `eb_order` (
    `order_no`, `mer_id`, `uid`,
    `total_num`, `pro_total_price`, `total_postage`, `total_price`,
    `coupon_price`, `pay_price`, `pay_postage`,
    `paid`, `pay_time`, `pay_type`, `pay_channel`,
    `status`, `refund_status`, `cancel_status`,
    `out_trade_no`, `type`, `level`,
    `is_user_del`, `is_merchant_del`, `is_del`,
    `receiving_time`, `create_time`, `update_time`
)
SELECT
    IFNULL(o.order_sn, CONCAT('LQQ', o.id)),                  -- order_no
    IFNULL(mm.new_id, 0),                                      -- mer_id
    IFNULL(um.new_id, 0),                                      -- uid
    1,                                                         -- total_num
    IFNULL(o.goods_price, 0),                                  -- pro_total_price (商品原价)
    IFNULL(o.freight_price, 0),                                -- total_postage (运费)
    IFNULL(o.order_price, 0),                                  -- total_price (订单总价)
    IFNULL(o.coupon_price, 0),                                 -- coupon_price (优惠券抵扣)
    IFNULL(o.actual_price, 0),                                 -- pay_price (实付金额)
    0,                                                         -- pay_postage
    -- paid
    CASE WHEN o.order_status IN (201,202,203,301,401,402) THEN 1 ELSE 0 END,
    -- pay_time
    CASE WHEN o.order_status >= 201 THEN IFNULL(o.pay_time, o.add_time) ELSE NULL END,
    'weixin',                                                  -- pay_type
    'routine',                                                 -- pay_channel (小程序)
    -- status 映射
    CASE o.order_status
        WHEN 101 THEN 0   -- 待支付
        WHEN 102 THEN 9   -- 已取消
        WHEN 103 THEN 9   -- 已取消
        WHEN 201 THEN 1   -- 待发货
        WHEN 202 THEN 1   -- 待发货(退款中用refund_status标记)
        WHEN 203 THEN 9   -- 已取消(退款完成)
        WHEN 301 THEN 4   -- 待收货
        WHEN 401 THEN 5   -- 已收货
        WHEN 402 THEN 5   -- 已收货(系统)
        ELSE 0
    END,
    -- refund_status
    CASE o.order_status
        WHEN 202 THEN 1   -- 退款申请中
        WHEN 203 THEN 3   -- 已全额退款
        ELSE 0
    END,
    -- cancel_status
    CASE o.order_status
        WHEN 102 THEN 2   -- 用户取消
        WHEN 103 THEN 1   -- 系统取消
        ELSE 0
    END,
    IFNULL(o.pay_id, ''),                                      -- out_trade_no (微信支付单号)
    0,                                                         -- type (普通订单)
    0,                                                         -- level
    0, 0, 0,                                                   -- is_user_del, is_merchant_del, is_del
    -- receiving_time
    CASE WHEN o.order_status IN (401, 402) THEN IFNULL(o.confirm_time, o.end_time) ELSE NULL END,
    IFNULL(o.add_time, NOW()),                                 -- create_time
    NOW()                                                      -- update_time
FROM `zhjy`.`t_customer_order` o
LEFT JOIN `lqq_id_mapping` um ON um.table_name = 'user' AND um.old_id = CAST(o.customer_id AS CHAR)
LEFT JOIN `lqq_id_mapping` mm ON mm.table_name = 'merchant' AND mm.old_id = CAST(o.shop_id AS CHAR);

-- 记录订单 ID 映射
INSERT INTO `lqq_id_mapping` (`table_name`, `old_id`, `new_id`)
SELECT 'order', o.id, eo.id
FROM `zhjy`.`t_customer_order` o
JOIN `eb_order` eo ON eo.order_no = IFNULL(o.order_sn, CONCAT('LQQ', o.id));
