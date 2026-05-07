# LQQ Merchant Platform (溜圈圈多商户平台)

Backend migration project: migrating the LQQ (溜圈圈) social commerce platform from the legacy `mlqApi` system to **JAVA-MER 2.2 Multi-Merchant Edition** — a mature, feature-rich e-commerce framework.

## Architecture

```
lqq_platform/
├── mer_java/          # Backend — Java 8 + Spring Boot 2.2.6 + MyBatis Plus
├── mer_mer_admin/     # Merchant Admin Panel — Vue + Element UI
├── mer_plat_admin/    # Platform Admin Panel — Vue + Element UI
└── mer_uniapp/        # Mobile App — UniApp (WeChat Mini Program + H5)

mlqApi/                # Legacy backend (read-only reference)
mlqAdmin/              # Legacy admin (read-only reference)
```

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 8 |
| Framework | Spring Boot 2.2.6 |
| ORM | MyBatis Plus 3.3.1 |
| Database | MySQL 8.0 |
| Cache | Redis 6+ |
| Connection Pool | Druid |
| Security | Spring Security + JWT |
| API Docs | Swagger + Knife4j |
| WeChat | weixin-java SDK (Mini Program, Payment, Open Platform) |
| Alipay | alipay-sdk 4.15.20 |
| Scheduling | Quartz |

## Features

### Platform Features (from JAVA-MER)

Multi-merchant management, product catalog, order lifecycle, payment (WeChat/Alipay), coupons, wallet & recharge, reviews, flash sales, group buying, community notes, paid membership, daily sign-in, points mall, refund & after-sales, CMS, visual page builder, shopping cart, logistics tracking, WeChat live streaming, and more.

### Custom LQQ Modules

| Module | Description | Status |
|--------|-------------|--------|
| **Auto Lock-in (B1)** | First purchase auto-binds user to merchant | Done |
| **WeChat Profit Sharing (B2)** | Multi-party fund splitting (locked merchant + referrer + platform) | In Progress |
| **Promoter System (B3)** | Merchant-invited promoters with platform approval & commission tracking | Done |
| **LBS Nearby Search (B4)** | Haversine-based merchant discovery within 0.5–10 km | Done |
| **Offline Verification (B5)** | In-store QR code scan order verification | Built-in |
| **Coupon Transfer (B6)** | User-to-user coupon gifting with source tracking | Done |
| **Lottery System (B7)** | Periodic lottery with points entry, auto-draw, merchant & platform management | Done |

## Backend Module Structure

```
mer_java/
├── crmeb-common/      # Shared: Entities, DTOs, Request/Response, Utils
├── crmeb-service/     # Service layer: Interfaces, Implementations, DAOs
├── crmeb-admin/       # Admin API: Platform & Merchant controllers (port 20800)
├── crmeb-front/       # User-facing API: Mobile / Mini Program controllers
└── crmeb-generate/    # Code generation tools
```

### API Routes

| Prefix | Scope |
|--------|-------|
| `api/front/*` | User-facing (C-end) |
| `api/admin/platform/*` | Platform management |
| `api/admin/merchant/*` | Merchant management |
| `api/admin/circle/*` | Community / social |
| `api/publicly/*` | Public (no auth) |

## Getting Started

### Prerequisites

- Java 8 (JDK 1.8)
- MySQL 8.0
- Redis 6+
- Maven 3.6+

### Setup

1. **Database** — Create a MySQL database named `java_mer_trip` and import the schema:
   ```bash
   mysql -u root -p java_mer_trip < lqq_platform/mer_java/sql/crmeb_mer_v2.2.sql
   ```

2. **Configuration** — Update database and Redis settings in:
   ```
   mer_java/crmeb-admin/src/main/resources/application.yml
   ```

3. **Build**:
   ```bash
   cd lqq_platform/mer_java
   mvn clean install -DskipTests
   ```

4. **Run**:
   ```bash
   cd crmeb-admin
   mvn spring-boot:run
   ```

5. **API Docs** — Visit [http://localhost:20800/doc.html](http://localhost:20800/doc.html)

## Migration Progress

- **Phase 0–1**: Project init + environment setup — **Complete**
- **Phase 2**: Data migration scripts + entity extensions — **Complete**
- **Phase 3**: Custom module development (6/7 done) — **In Progress**
- **Phase 4**: Frontend adaptation — Pending
- **Phase 5**: Integration testing — Pending
- **Phase 6**: Production deployment — Pending

See [`claude-progress.txt`](claude-progress.txt) for detailed task-level tracking.

## License

Proprietary. All rights reserved.
