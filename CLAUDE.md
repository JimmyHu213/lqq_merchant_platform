# CRMEB Migration Project

## 项目概述
将溜圈圈后端从 mlqApi (Java Spring Boot) 迁移到 CRMEB PHP 多商户版 v3.4 (ThinkPHP 6)。
前端用 Vue 3 重写，管理后台使用 CRMEB 内置面板替代 mlqAdmin。

## 代码位置
- **开发目标 (CRMEB，直接在此修改)**: `./CRMEB_MER-v3.4/`
- **业务逻辑参考 (mlqApi，只读)**: `./mlqApi/`
- **旧管理后台参考 (mlqAdmin，只读)**: `./mlqAdmin/`
- **官网 (lzweb，不在迁移范围)**: `./lzweb/`
- **其他参考资料（只读）**: `./其他资料/`

## 开发方式
**直接在 CRMEB_MER-v3.4/ 目录上开发**。不是复制代码出来改，而是在 CRMEB 框架基础上：
- 新增文件：添加自定义模块（锁客、分账、推广员、LBS）
- 修改已有文件：在 CRMEB 已有的 Controller/Repository/DAO 中扩展功能
- 新增数据库字段：在已有表上 ALTER TABLE 增加字段
- 新增数据库表：为全新功能创建新表
- 配置修改：路由、中间件、事件注册等

mlqApi/ 和 mlqAdmin/ 仅作为业务逻辑参考源，**只读不改**。

## 技术栈
### 源 (mlqApi)
- Java 8 + Spring Boot 2.7.10 + MyBatis + MySQL 8.0 + Redis
- 端口: 8282, 数据库: zhjy

### 目标 (CRMEB PHP)
- PHP 7.1+ + ThinkPHP 6.0.7 + Swoole 4.4+ + MySQL + Redis
- 表前缀: eb_, 165 张表
- 架构: Controller → Repository → DAO → Model
- 路由前缀: /api/ (用户端), /sys/ (管理端), /mer/ (商户端)

## 三员工团队架构 (Planner → Generator → Evaluator)

### Planner (规划员)
- 将迁移阶段分解为冲刺级任务合同
- 输出: 任务规格书（含文件路径、函数签名、验收标准）
- 必须引用 mlqApi 源码和 CRMEB 目标代码的具体位置

### Generator (开发员)
- 每次只实现一个任务
- 严格遵循 CRMEB 代码模式
- 每个任务完成后提交 Git
- 更新进度文件 claude-progress.txt

### Evaluator (审核员)
- 对照 Planner 的规格书审核 Generator 的输出
- 检查项: 业务逻辑正确性、CRMEB 模式合规、安全性
- 资金计算逻辑 (分账) 必须逐行审核
- 可以拒绝并退回 Generator 修改

## 需要定制开发的模块 (CRMEB 未内置)

### 1. 自动锁客 (skId)
- eb_user 表增加 lock_mer_id, lock_time 字段
- 支付成功回调中判断并绑定
- 商户后台展示锁客统计

### 2. 微信多方分账
- 对接微信 profitsharing API
- 分润规则: 锁客商户(skzk%) + 推荐人(佣金%) + 平台(剩余)
- 使用 CRMEB 队列系统异步执行

### 3. 推广员 (tgy) 角色
- 评估 CRMEB Circle/CircleAgent 是否可复用
- 推广员绑定商铺、区域佣金统计

### 4. LBS 附近商铺搜索
- Haversine 距离计算
- 按距离排序 + 0.5km-10km 过滤

## 关键业务逻辑参考

### 订单状态码 (mlqApi → 需映射到 CRMEB)
- 101: 已下单未支付 → 201: 已支付未发货 → 301: 已发货 → 401: 确认收货
- 102/103: 取消 → 202: 退款

### 分润计算 (dxcOrder 方法)
```
基础金额 = goodsPrice × (1 - fszk)
锁客商铺分润 = 基础金额 × skzk%
推荐人分润 = 基础金额 × 佣金%
平台 = 剩余
```

### 用户角色判断
- userId < 60000 → 推广员 (tgy)
- userId >= 60000 → 商户 (shop)

## 数据迁移映射 (核心表)
| mlqApi 表 | CRMEB 表 | 说明 |
|-----------|----------|------|
| t_customer_buyer | eb_user + eb_wechat_user | 用户 + 微信绑定 |
| t_business_shop | eb_merchant | 商户 |
| t_customer_order | eb_store_order | 订单 |
| t_customer_buyer_yhq | eb_store_coupon_user | 优惠券 |
| t_business_shop_goods | eb_store_product | 商品 |
| user_recharge_order | eb_user_recharge | 充值 |

## 编码规范
- 所有开发工作在 `./CRMEB_MER-v3.4/` 目录内完成
- PHP 代码遵循 CRMEB 已有模式，不引入新的设计模式
- 新增 Controller 放在 `CRMEB_MER-v3.4/app/controller/{admin|api|merchant}/` 下
- 新增 Model 放在 `CRMEB_MER-v3.4/app/common/model/` 下
- 新增 DAO 放在 `CRMEB_MER-v3.4/app/common/dao/` 下
- 新增 Repository 放在 `CRMEB_MER-v3.4/app/common/repositories/` 下
- 新增路由在 `CRMEB_MER-v3.4/route/` 下对应文件中注册
- 数据库迁移 SQL 放在 `CRMEB_MER-v3.4/install/migrations/` 下（新建此目录）
- 注释使用中文
- 修改 CRMEB 已有文件时，在修改处加注释标记：`// [LQQ-迁移] 描述`

## 修改 CRMEB 已有代码的规则
可以修改 CRMEB 已有文件，但必须遵守：
1. **可修改**: app/ 目录下的 controller、repository、dao、model、route、config
2. **谨慎修改**: crmeb/services/ 下的支付、微信相关服务（需 Evaluator 审核）
3. **禁止修改**: crmeb/ 目录下的基础框架类（BaseController、BaseDao 等）、vendor/ 目录
4. 每处修改必须加 `// [LQQ-迁移]` 注释，方便追踪和回滚
5. 修改已有文件前，先阅读该文件完整逻辑，确保不破坏已有功能

## 禁止事项
- 不要修改 vendor/ 目录（通过 composer 管理）
- 不要修改 crmeb/ 下的基础框架类
- 不要在代码中硬编码密钥或敏感信息
- 分账金额计算不允许 AI 自主决定，必须由人工确认
- 不要推送代码到远程仓库（本地 Git 管理）
