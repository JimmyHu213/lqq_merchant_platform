-- ============================================================
-- [LQQ-迁移] 006: 商品 & 优惠券数据迁移
-- 日期: 2026-04-03
-- ============================================================

-- ==================== 商品迁移 ====================
-- t_business_shop_goods → eb_product

INSERT INTO `eb_product` (
    `mer_id`, `name`, `intro`, `keyword`,
    `image`, `slider_image`,
    `price`, `ot_price`, `cost`,
    `stock`, `sales`, `browse`,
    `unit_name`, `sort`, `spec_type`,
    `is_show`, `is_del`, `is_recycle`,
    `audit_status`, `type`,
    `create_time`, `update_time`
)
SELECT
    IFNULL(mm.new_id, 0),                                      -- mer_id
    IFNULL(g.name, '未命名商品'),                               -- name
    IFNULL(g.goods_brief, ''),                                 -- intro
    IFNULL(g.keywords, ''),                                    -- keyword
    IFNULL(g.primary_pic_url, ''),                             -- image (主图)
    IFNULL(g.list_pic_url, ''),                                -- slider_image (轮播图)
    IFNULL(g.retail_price, 0),                                 -- price (零售价)
    IFNULL(g.counter_price, 0),                                -- ot_price (原价/划线价)
    0,                                                         -- cost (成本)
    IFNULL(g.kcnum, 999),                                      -- stock (库存)
    0,                                                         -- sales
    0,                                                         -- browse
    IFNULL(g.goods_unit, '件'),                                -- unit_name
    IFNULL(g.sort_order, 0),                                   -- sort
    0,                                                         -- spec_type (单规格)
    CASE WHEN g.is_on_sale = 1 THEN 1 ELSE 0 END,             -- is_show
    0,                                                         -- is_del
    0,                                                         -- is_recycle
    1,                                                         -- audit_status (已审核)
    0,                                                         -- type (普通商品)
    NOW(),                                                     -- create_time
    NOW()                                                      -- update_time
FROM `zhjy`.`t_business_shop_goods` g
LEFT JOIN `lqq_id_mapping` mm ON mm.table_name = 'merchant' AND mm.old_id = CAST(g.shop_id AS CHAR);

-- 记录商品 ID 映射
INSERT INTO `lqq_id_mapping` (`table_name`, `old_id`, `new_id`)
SELECT 'product', g.id, p.id
FROM `zhjy`.`t_business_shop_goods` g
JOIN `eb_product` p ON p.name = IFNULL(g.name, '未命名商品')
    AND p.mer_id = IFNULL((SELECT new_id FROM lqq_id_mapping WHERE table_name='merchant' AND old_id=CAST(g.shop_id AS CHAR)), 0)
LIMIT 10000;

-- ==================== 优惠券迁移 ====================
-- t_customer_buyer_yhq → eb_coupon_user

INSERT INTO `eb_coupon_user` (
    `coupon_id`, `mer_id`, `uid`, `name`,
    `publisher`, `category`, `receive_type`, `coupon_type`,
    `money`, `min_price`, `start_time`, `end_time`, `use_time`,
    `status`, `create_time`, `update_time`
)
SELECT
    0,                                                         -- coupon_id (无对应模板，设0)
    IFNULL(mm.new_id, 0),                                      -- mer_id
    IFNULL(um.new_id, 0),                                      -- uid
    CONCAT('溜圈圈优惠券-', IFNULL(y.shop_name, '')),           -- name
    2,                                                         -- publisher (2=商户)
    2,                                                         -- category (2=商户优惠券)
    3,                                                         -- receive_type (3=后台发放)
    1,                                                         -- coupon_type (1=满减)
    CAST(IFNULL(y.je, 0) * 100 AS UNSIGNED),                   -- money (转为分)
    0,                                                         -- min_price (无门槛)
    IFNULL(y.time, NOW()),                                     -- start_time
    IFNULL(y.dqtime, DATE_ADD(NOW(), INTERVAL 30 DAY)),        -- end_time
    CASE WHEN y.state = 0 THEN y.time ELSE NULL END,           -- use_time (state=0已使用)
    -- status 映射: mlqApi 1=可用,0=已用,-1=过期
    CASE y.state
        WHEN 1 THEN 0   -- MER: 0=未使用
        WHEN 0 THEN 1   -- MER: 1=已使用
        WHEN -1 THEN 2  -- MER: 2=已过期 (JAVA-MER 可能不同，需确认)
        ELSE 0
    END,
    IFNULL(y.time, NOW()),                                     -- create_time
    NOW()                                                      -- update_time
FROM `zhjy`.`t_customer_buyer_yhq` y
LEFT JOIN `lqq_id_mapping` um ON um.table_name = 'user' AND um.old_id = CAST(y.buyer_id AS CHAR)
LEFT JOIN `lqq_id_mapping` mm ON mm.table_name = 'merchant' AND mm.old_id = CAST(y.shop_id AS CHAR);
