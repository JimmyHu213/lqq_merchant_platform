# lz-project

#### 介绍

莱正科技完整项目仓库，包含社交电商平台"溜圈圈"和公司官网，整体采用**前后端分离 + 传统服务端渲染**的混合架构。

#### 项目结构

```
lz-project-master/
├── lzweb/       → 莱正科技官网（静态前端）
├── mlqApi/      → 溜圈圈 API 后端（Spring Boot）
├── mlqAdmin/    → 溜圈圈管理后台（Spring Boot + FreeMarker）
├── doc/         → 数据库脚本 & 部署脚本
└── 其他资料/     → API 文档、参考代码、UI 素材
```

#### 软件架构

##### 系统数据流

```
微信小程序用户
    ↓ 微信登录
mlqApi (8282) ← Redis (Token缓存)
    ↓ MyBatis
MySQL (zhjy库)
    ↓ 微信支付回调
微信支付服务器

运营人员
    ↓ 浏览器访问
mlqAdmin (8686)
    ↓ MyBatis
MySQL (同一数据库)
```

##### 1. `lzweb` — 莱正科技官网

| 项目 | 说明 |
|------|------|
| **技术栈** | HTML + jQuery 1.7.2 + Vue.js + CSS3，纯前端 MPA |
| **核心页面** | 首页(fullpage滚动)、关于我们、新闻、案例、常见问题、联系我们 |
| **特殊功能** | 医疗体检预约系统（Vue组件）、体检报告查询、微信支付 |
| **后端API** | 对接 `http://cd5120.hccenter.net/`（成都市第五人民医院健康管理中心） |
| **集成** | 百度地图、微信JS-SDK、微信支付 |

**目录结构：**

```
lzweb/
├── index.html              - 首页（fullpage全屏滚动）
├── about.html              - 公司介绍
├── news.html / newsshow.html    - 新闻列表 / 新闻详情
├── case.html / caseshow.html    - 案例展示 / 案例详情
├── wenti.html / wentishow.html  - 常见问题 / 问题详情
├── contact.html            - 联系我们（百度地图集成）
├── report_query_zl.html    - 医疗报告查询
├── index.js                - 首页 Vue 实例（体检预约）
├── common.js               - 公共库（API封装、微信SDK、支付组件）
├── style/
│   ├── css/                - 7个样式文件
│   ├── js/                 - 13个JS文件（jQuery、fullpage、pace等）
│   └── images/             - 226+ 图片资源
└── uploads/                - 案例图片
```

**关键组件：**

- **header-con** — 全局头部（公司Logo、用户信息）
- **wx-payorder** — 微信支付组件（日期选择、人员选择、支付处理）
- **footer-con** — 导航底栏
- **fullpage.js** — 全屏滚动（banner、服务、案例、关于、客户、联系、页脚）

##### 2. `mlqApi` — 溜圈圈 API 后端

| 项目 | 说明 |
|------|------|
| **技术栈** | Java 8 + Spring Boot 2.7.10 + MyBatis + MySQL 8.0 + Redis |
| **端口** | 8282 |
| **架构** | Controller → Service → Mapper 分层架构 |
| **数据库** | MySQL (`zhjy` 库) |
| **缓存** | Redis（Token缓存，10天过期） |

**目录结构：**

```
mlqApi/src/main/java/com/simple/
├── LqApplication.java          - Spring Boot 启动类
├── config/                     - 配置（微信API密钥、支付配置、Token解析器）
├── controller/                 - REST API 控制器（13个）
│   ├── WxAuthController.java   - 微信登录/认证
│   ├── WxHomeController.java   - 首页数据
│   ├── WxOrderController.java  - 订单管理 & 支付
│   ├── GoodsController.java    - 商品管理
│   ├── BuyerYhqController.java - 优惠券管理
│   ├── SShopController.java    - 商铺管理
│   └── ...
├── service/                    - 业务逻辑层（19+ 服务）
├── mapper/                     - MyBatis 数据访问层（16个Mapper）
├── model/                      - 实体模型（28个）
└── common/                     - 工具类（支付、Redis、短信、HTTP等）
```

**核心模块：**

- **用户认证** (`WxAuthController`) — 微信小程序登录、手机号绑定、Token管理
- **首页数据** (`WxHomeController`) — 轮播图、分类、附近推荐商铺（基于经纬度）
- **订单管理** (`WxOrderController`) — 下单、微信支付(JSAPI)、支付回调、退款、发货确认
- **商品管理** (`GoodsController`) — 商品CRUD
- **优惠券** (`BuyerYhqController`) — 发放、核销、过期管理
- **商铺管理** (`SShopController`) — 商铺搜索、统计
- **推荐分佣** — 用户邀请机制、佣金追踪与结算
- **充值系统** — 用户钱包充值

**主要数据模型：**

| 实体 | 数据表 | 用途 |
|------|--------|------|
| BuyerEntity | t_customer_buyer | 顾客用户（微信绑定、推荐关系、定位） |
| Order | t_customer_order | 订单（支付状态、发货、退款） |
| Shop | t_business_shop | 商铺信息（位置、评分） |
| ShopGoods | t_business_shop_goods | 商品信息 |
| BuyerYhq | t_buyer_yhq | 优惠券（0=已用, 1=有效, -1=过期） |
| UserCjlist | t_user_cjlist | 收藏列表 |
| ShopCate | t_shop_cate | 商铺分类 |
| Address | t_address | 用户地址 |

**订单状态流转：**

| 状态码 | 含义 |
|--------|------|
| 101 | 已下单，未支付 |
| 102 | 下单后取消 |
| 103 | 超时自动取消 |
| 201 | 已支付，未发货 |
| 202 | 退款/取消 |
| 301 | 已发货，未确认 |
| 401 | 确认收货（完成） |
| 402 | 超时自动确认 |

**外部集成：**

- 微信支付（统一下单、分账、转账）
- 微信OAuth（小程序登录、手机号解密）
- 短信服务（注册验证码）
- 二维码生成（ZXing）

##### 3. `mlqAdmin` — 溜圈圈管理后台

| 项目 | 说明 |
|------|------|
| **技术栈** | Java 8 + Spring Boot 2.3.1 + MyBatis + FreeMarker（服务端渲染） |
| **端口** | 8686 |
| **前端** | jQuery + Bootstrap + ACE Admin 模板 + ECharts |
| **数据库** | MySQL（与 mlqApi 共享同一数据库） |

**目录结构：**

```
mlqAdmin/src/main/java/com/simple/
├── LqApplication.java          - 启动类
├── config/                     - 配置（MyBatis、Web、启动任务）
├── auth/                       - 认证授权模块
│   ├── controller/             - 登录、角色管理、权限管理
│   ├── service/                - 认证业务逻辑
│   ├── mapper/                 - 用户/角色数据访问
│   └── model/                  - Admin、Role、Permission实体
├── shop/                       - 电商管理模块
│   ├── controller/             - 商铺、商品、订单、优惠券管理（6个控制器）
│   ├── service/                - 业务逻辑（6个服务）
│   ├── mapper/                 - 数据访问（8个Mapper）
│   └── model/                  - 实体模型（7个）
├── sys/                        - 系统管理模块
│   ├── controller/             - 字典、文件上传、系统配置
│   ├── service/
│   └── mapper/
├── controller/                 - 首页路由、静态资源
└── common/                     - 工具类 & AOP权限拦截
    └── aop/                    - @Permission注解 + 切面处理

resources/
├── templates/                  - 57个FreeMarker HTML模板
│   ├── shop/                   - 商铺管理页面
│   ├── system/                 - 系统管理页面
│   └── count/                  - 数据统计页面
├── mapper/                     - MyBatis XML映射文件
└── static/                     - 前端资源
    ├── css/                    - Bootstrap、ACE Admin
    ├── js/                     - jQuery、jQuery UI
    ├── kindeditor/             - 富文本编辑器
    ├── webuploader/            - 文件上传组件
    ├── zTree/                  - 树形组件
    ├── layer/                  - 弹窗组件
    └── My97DatePicker/         - 日期选择器
```

**核心功能：**

- **权限系统** — 基于AOP注解的RBAC权限控制（角色-权限管理）
- **商铺管理** — 商铺CRUD、客服管理、订单管理
- **商品管理** — 商品分类、商品信息、模板管理
- **优惠券管理** — 优惠券发放与统计
- **数据统计** — ECharts可视化、订单/收入统计
- **系统管理** — 字典管理、文件上传、管理员管理
- **富文本编辑** — KindEditor编辑器

##### 4. `doc/` — 部署与数据库

| 文件 | 说明 |
|------|------|
| `lqq.sql` (~90,000行) | 溜圈圈数据库 schema + 数据 |
| `spa.sql` (~90,000行) | 另一套数据库 schema + 数据 |
| `app.sh` | 生产环境部署脚本（start/stop/restart，端口 20000） |

##### 5. `其他资料/` — 参考资料

| 目录 | 说明 |
|------|------|
| **接口文档/** | 完整的Swagger API文档（public/app/admin三套，含md/html/json格式） |
| **crmeb/** | CRMEB电商参考项目（Java SpringBoot，含支付集成、商品复制等文档） |
| **admin/** | Vue 2管理后台参考项目（26+组件目录） |
| **app/** | UniApp移动端参考项目（22+页面，32+组件） |
| **溜圈圈相关代码/** | 公众号领券、智慧商圈VIP、UniApp客户端等历史代码 |
| **源曼/** | 备用前端项目（Vue.js） |
| **lzsite/** | 官网静态页面备份 |
| **图片素材/icons/** | UI设计素材（banner、个人中心、分类图标等） |
| **mmd/** | 另一个Java参考项目（含50+个MyBatis Mapper定义） |

#### 环境配置

##### mlqApi 配置

| 环境 | 端口 | 数据库地址 | Redis |
|------|------|-----------|-------|
| 生产 (zs) | 8282 | 110.41.171.20:3306/zhjy | localhost:6379 |
| 测试 (test) | 8282 | 116.205.132.135:3306/zhjy | 192.168.5.188:6379 |

##### mlqAdmin 配置

| 环境 | 端口 | 说明 |
|------|------|------|
| 生产 (zs) | 8686 | 与mlqApi共享同一MySQL数据库 |
| 测试 (test) | 8686 | 测试环境配置 |

#### 参与贡献

1. Fork 本仓库
2. 新建 Feat_xxx 分支
3. 提交代码
4. 新建 Pull Request
