# 溜圈圈迁移项目 (JAVA-MER)

## 项目概述
将溜圈圈后端从 mlqApi (旧 Java Spring Boot) 迁移到 JAVA-MER 2.2 多商户版 (新 Java Spring Boot)。
基于 JAVA-MER 成熟的电商框架，移植溜圈圈的自定义业务逻辑。
前端使用 JAVA-MER 自带的 Vue 管理后台 + UniApp 移动端。

## 代码位置
- **开发目标 (JAVA-MER，直接在此修改)**: `./lqq_platform/mer_java/`
- **商户管理后台前端**: `./lqq_platform/mer_mer_admin/` (Vue + Element UI)
- **平台管理后台前端**: `./lqq_platform/mer_plat_admin/` (Vue + Element UI)
- **移动端前端**: `./lqq_platform/mer_uniapp/` (UniApp 跨平台)
- **业务逻辑参考 (mlqApi，只读)**: `./mlqApi/`
- **旧管理后台参考 (mlqAdmin，只读)**: `./mlqAdmin/`
- **其他参考资料（只读）**: `./其他资料/`、`./doc/`

## 开发方式
**直接在 lqq_platform/mer_java/ 目录上开发**。在 JAVA-MER 框架基础上：
- 新增文件：添加自定义模块（锁客、分账、推广员、LBS）
- 修改已有文件：在 JAVA-MER 已有的 Controller/Service/DAO 中扩展功能
- 新增数据库字段：在已有表上 ALTER TABLE 增加字段
- 新增数据库表：为全新功能创建新表
- 配置修改：路由、拦截器、定时任务等

mlqApi/ 和 mlqAdmin/ 仅作为业务逻辑参考源，**只读不改**。

## 技术栈
### 源 (mlqApi - 旧系统)
- Java 8 + Spring Boot 2.7.10 + MyBatis + MySQL 8.0 + Redis
- 端口: 8282, 数据库: zhjy

### 目标 (JAVA-MER 2.2 - 新系统)
- Java 8 + Spring Boot 2.2.6 + MyBatis Plus 3.3.1 + MySQL 8.0 + Redis
- 数据库: java_mer_trip
- 连接池: Druid (max-active: 200)
- 端口: Admin API 20800, Front API 另配
- 架构: Controller → Service → DAO (extends BaseMapper) → Entity
- 安全: Spring Security + JWT
- 文档: Swagger + Knife4j
- 微信: weixin-java SDK (小程序、支付、开放平台)
- 支付宝: alipay-sdk 4.15.20
- 定时任务: Quartz

### JAVA-MER 多模块结构
```
mer_java/
├── crmeb-common/     # 共享层: Entity(168个)、DTO、Request/Response、工具类、配置
├── crmeb-service/    # 服务层: Service接口+实现(406个)、DAO(165个)
├── crmeb-admin/      # 管理端API: Controller(171个)、Manager、Task、Config
├── crmeb-front/      # 用户端API: Controller(57个)、Service、Config
└── crmeb-generate/   # 代码生成工具
```

### API 路由前缀
- `api/front/*` — 用户端 (C端用户访问)
- `api/admin/platform/*` — 平台管理端
- `api/admin/merchant/*` — 商户管理端
- `api/admin/circle/*` — 圈子/社区
- `api/publicly/*` — 公开接口 (无需认证)

## 三员工团队架构 (Planner → Generator → Evaluator)

### Planner (规划员)
- 将迁移阶段分解为冲刺级任务合同
- 输出: 任务规格书（含文件路径、函数签名、验收标准）
- 必须引用 mlqApi 源码和 JAVA-MER 目标代码的具体位置

### Generator (开发员)
- 每次只实现一个任务
- 严格遵循 JAVA-MER 代码模式 (MyBatis Plus ServiceImpl/BaseMapper 模式)
- 每个任务完成后提交 Git
- 更新进度文件 claude-progress.txt

### Evaluator (审核员)
- 对照 Planner 的规格书审核 Generator 的输出
- 检查项: 业务逻辑正确性、JAVA-MER 模式合规、安全性
- 资金计算逻辑 (分账) 必须逐行审核
- 可以拒绝并退回 Generator 修改

## 功能模块映射 (mlqApi → JAVA-MER)

### A. 直接移植 (12个) — JAVA-MER 已有，配置/适配即可
| # | 模块 | mlqApi 代码 | JAVA-MER 对应 | 工作量 |
|---|------|------------|---------------|--------|
| A1 | 用户&微信登录 | WxAuthController | LoginController + UserService | 小 |
| A2 | 商户管理 | SShopController + Shop | MerchantController + MerchantApplyService | 中(需扩展字段) |
| A3 | 商品管理 | GoodsController | ProductController + ProductService | 小 |
| A4 | 订单管理 | WxOrderController | OrderController + OrderService | 中(状态映射) |
| A5 | 微信支付 | PayCommon + PayUtil | PayController + WechatPayService | 小 |
| A6 | 优惠券 | BuyerYhqController | CouponController + CouponUserService | 小 |
| A7 | 充值/钱包 | UserRechargeService | RechargeController + RechargeOrderService | 小 |
| A8 | 用户评论 | UserPl | ProductReplyService | 小 |
| A9 | 地址管理 | BuyerYhqController | UserAddressService | 小 |
| A10 | 文件上传 | 本地存储 | UploadService (OSS/COS) | 小 |
| A11 | 系统配置&通知 | SysCommController | SystemConfigService + SmsService | 小 |
| A12 | 员工管理 | ShopTempController | MerchantEmployeeService | 小 |

### B. 需要独立开发 (7个) — JAVA-MER 无对应功能
| # | 模块 | 说明 | 工作量 |
|---|------|------|--------|
| B1 | 自动锁客 (skId) ★ | 首次消费自动绑定商铺, User 增加 lockMerId/lockTime | 中 |
| B2 | 微信多方分账 ★ | profitsharing API, 锁客商户+推荐人+平台三方分润 | 大 |
| B3 | 推广员 (tgy) ★ | userId<60000 为推广员, 绑定商铺/区域佣金统计 | 大 |
| B4 | LBS 附近搜索 | Haversine 距离计算, 0.5-10km 筛选排序 | 中 |
| B5 | 线下核销 | 到店扫码核销订单, 核销码生成, ishx/hxid 状态 | 中 |
| B6 | 优惠券转赠 | 用户间优惠券转移, zzrId 追踪来源 | 小 |
| B7 | 抽奖系统 | 按期抽奖, 积分参与, 自动开奖, UserCjlist | 中 |

详细进度见 `claude-progress.txt`

## 关键业务逻辑参考

### 订单状态码 (mlqApi → 需映射到 JAVA-MER)
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
| mlqApi 表 | JAVA-MER 对应 | 说明 |
|-----------|---------------|------|
| t_customer_buyer | User + WechatUser | 用户 + 微信绑定 |
| t_business_shop | Merchant | 商户 |
| t_customer_order | StoreOrder | 订单 |
| t_customer_buyer_yhq | StoreCouponUser | 优惠券 |
| t_business_shop_goods | Product | 商品 |
| user_recharge_order | UserRecharge | 充值 |

## 编码规范
- 所有后端开发在 `./lqq_platform/mer_java/` 目录内完成
- Java 代码遵循 JAVA-MER 已有模式 (MyBatis Plus)，不引入新的设计模式
- 新增 Entity 放在 `crmeb-common/src/main/java/com/zbkj/common/model/{模块}/` 下
- 新增 DAO 放在 `crmeb-service/src/main/java/com/zbkj/service/dao/` 下，继承 `BaseMapper<T>`
- 新增 Service 接口放在 `crmeb-service/src/main/java/com/zbkj/service/service/` 下
- 新增 Service 实现放在 `crmeb-service/src/main/java/com/zbkj/service/service/impl/` 下，继承 `ServiceImpl<Dao, Entity>`
- 新增 Admin Controller 放在 `crmeb-admin/src/main/java/com/zbkj/admin/controller/{platform|merchant}/` 下
- 新增 Front Controller 放在 `crmeb-front/src/main/java/com/zbkj/front/controller/` 下
- 新增 Request/Response DTO 放在 `crmeb-common/src/main/java/com/zbkj/common/request/` 或 `response/` 下
- 数据库迁移 SQL 放在 `mer_java/sql/migrations/` 下（新建此目录）
- 注释使用中文
- 修改 JAVA-MER 已有文件时，在修改处加注释标记：`// [LQQ-迁移] 描述`
- 使用 Lombok 注解 (@Data, @EqualsAndHashCode 等)，与已有代码风格保持一致
- 使用 MyBatis Plus LambdaQueryWrapper 进行查询构建

## 修改 JAVA-MER 已有代码的规则
可以修改 JAVA-MER 已有文件，但必须遵守：
1. **可修改**: crmeb-admin/、crmeb-front/ 下的 controller、service、config
2. **可修改**: crmeb-service/ 下的 service、dao
3. **可修改**: crmeb-common/ 下的 model、request、response、constants
4. **谨慎修改**: 支付相关 Service（微信支付、支付宝回调等，需 Evaluator 审核）
5. **禁止修改**: Spring Security 核心配置、MyBatis Plus 基础框架类、第三方 SDK 封装
6. 每处修改必须加 `// [LQQ-迁移]` 注释，方便追踪和回滚
7. 修改已有文件前，先阅读该文件完整逻辑，确保不破坏已有功能

## 禁止事项
- 不要修改 pom.xml 中已有依赖的版本号（可新增依赖）
- 不要修改 Spring Security 核心认证链
- 不要在代码中硬编码密钥或敏感信息
- 分账金额计算不允许 AI 自主决定，必须由人工确认
- 不要推送代码到远程仓库（本地 Git 管理）

## 需要人工审批的操作（所有 Agent 必须遵守）
以下操作**必须**获得用户明确同意后才能执行：
1. **删除任何文件或目录**（rm、git clean、docker volume rm 等）
2. **修改系统配置**（Docker 配置、application.yml、数据库配置、Nginx 配置等）
3. **执行破坏性 Git 操作**（reset --hard、force push、branch -D 等）
4. **清除数据**（docker down -v、DROP TABLE、TRUNCATE 等）
5. **安装或卸载系统级依赖**（mvn 新增/删除依赖、apt install 等）

违反此规则的 Agent 输出将被 Evaluator 标记为 REJECTED。
