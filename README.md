<div align="center">

<!-- Animated Wave Header -->
<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0F172A,50:1E3A8A,100:0EA5E9&height=210&section=header&text=CARTIFY&fontSize=65&fontColor=FFFFFF&animation=fadeIn&fontAlignY=35&desc=Production-Ready%20Modular%20Monolith%20E-Commerce%20Engine&descAlignY=58&descSize=18" width="100%" />

<!-- Dynamic Typing SVG -->
<a href="https://github.com/AK-Elshamy/cartify">
  <img src="https://readme-typing-svg.demolab.com?font=Fira+Code&weight=600&size=18&duration=3000&pause=1000&color=38BDF8&center=true&vCenter=true&width=750&lines=Java+25+%26+Spring+Boot+4+Modular+Monolith+Architecture;Pessimistic+Write+Locking+Against+Inventory+Race+Conditions;Idempotent+Strategy-Based+Payment+%26+Webhook+Engine;Multi-Stage+Docker+Orchestration+%26+OpenAPI+3+Swagger" alt="Typing SVG" />
</a>

<br/>

<!-- Tech Stack Badges -->
[![Java](https://img.shields.io/badge/Java-25-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-4.1.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Spring Security](https://img.shields.io/badge/JWT_Security-Stateless-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![Swagger](https://img.shields.io/badge/OpenAPI-3.0-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)](http://localhost:8080/swagger-ui.html)
[![JUnit5](https://img.shields.io/badge/Tests-11_Passing-25A162?style=for-the-badge&logo=junit5&logoColor=white)](#-automated-test-suite)

</div><br/><br/>

<!-- Quick Navigation Topic Links -->
<p align="center">
  <a href="#-why-cartify-stands-out"><strong>🎯 Overview</strong></a> •
  <a href="#-system-architecture"><strong>🏗️ Architecture</strong></a> •
  <a href="#-database-schema-erd"><strong>🗄️ Database ERD</strong></a> •
  <a href="#-concurrency--payment-flow"><strong>⚡ Payment Flow</strong></a> •
  <a href="#-engineering-decisions--trade-offs"><strong>🧠 Trade-offs</strong></a> •
  <a href="#-one-command-deployment"><strong>🚀 Quick Start</strong></a> •
  <a href="#-api-reference"><strong>📡 API Docs</strong></a> •
  <a href="#-automated-test-suite"><strong>🧪 Tests</strong></a>
</p>

</div>

---

<br/>

> [!IMPORTANT]
> **Production Mindset, Not Just CRUD:** Cartify is architected to solve real-world e-commerce engineering
bottlenecks—preventing **inventory overselling** under concurrent load, eliminating **duplicate charges** via
database-enforced idempotency keys, and automating **stock rollbacks** on failed asynchronous payment webhooks.

---

## 🎯 Why Cartify Stands Out

<table>
  <tr>
    <td width="50%" valign="top">
      <h3>👔 For Recruiters & Hiring Managers</h3>
      <ul>
        <li><b>End-to-End Ownership:</b> Complete lifecycle from database modeling & security to testing, API docs, and containerization.</li>
        <li><b>Zero-Friction Setup:</b> Runs with a single <code>docker compose up</code> command—no local JDK or database setup needed.</li>
        <li><b>Clean Git Workflow:</b> Built incrementally using feature branches, semantic commits, and peer-review-ready PR structure.</li>
        <li><b>Self-Documented API:</b> Interactive Swagger UI with pre-configured JWT Bearer authorization out of the box.</li>
      </ul>
    </td>
    <td width="50%" valign="top">
      <h3>🧠 For Tech Leads & Senior Engineers</h3>
      <ul>
        <li><b>Concurrency Control:</b> JPA <code>PESSIMISTIC_WRITE</code> row-level locks (<code>SELECT ... FOR UPDATE</code>) prevent overselling.</li>
        <li><b>Financial Integrity:</b> Immutable price & address snapshots at checkout + database-enforced payment idempotency.</li>
        <li><b>Extensible Design Patterns:</b> Strategy Pattern for payment gateways (<code>CARD</code>, <code>COD</code>) honoring Open/Closed Principle.</li>
        <li><b>Hardened Runtime:</b> Multi-stage Docker build executing under a dedicated non-root Linux user.</li>
      </ul>
    </td>
  </tr>
</table>

---

## 🏗️ System Architecture

Cartify enforces a clean **Layered Modular Monolith** architecture. Every request passes through stateless JWT
verification and global exception boundaries before hitting transactional domain services.

```mermaid
flowchart TB
    classDef client fill: #0EA5E9, stroke: #0284C7, color: #FFFFFF, font-weight: bold
    classDef edge fill: #6366F1, stroke: #4F46E5, color: #FFFFFF, font-weight: bold
    classDef module fill: #1E293B, stroke: #38BDF8, color: #F8FAFC
    classDef storage fill: #059669, stroke: #047857, color: #FFFFFF, font-weight: bold

    subgraph Clients ["🌐 Client & External Ecosystem"]
        direction LR
        WebApp["Web / Mobile Client"]:::client
        SwaggerUI["Swagger UI (OpenAPI 3)"]:::client
        PayGateway["External Payment Gateway"]:::client
    end

    subgraph SecurityEdge ["🛡️ Security & API Edge Layer"]
        direction LR
        JwtFilter["Stateless JWT Filter\n(Role-Based Access)"]:::edge
        WebhookAuth["Webhook Secret Guard\n(X-Webhook-Secret)"]:::edge
        Advice["Global Exception Handler\n(@RestControllerAdvice)"]:::edge
    end

    subgraph CoreDomain ["⚙️ Modular Monolith Core (Spring Boot 4)"]
        direction TB
        Auth["🔐 Auth & User Module"]:::module
        Catalog["📦 Product, Category & Media"]:::module
        CartWish["🛒 Cart & Wishlist Engine"]:::module
        OrderEng["🧾 Transactional Order Engine"]:::module
        PayStrat["💳 Payment Strategy & Idempotency"]:::module
    end

    subgraph Persistence ["🗄️ Persistence & Infrastructure"]
        direction LR
        Postgres[("PostgreSQL 17\nACID + Row-Level Locks")]:::storage
        Uploads[("Docker Named Volume\n(/app/uploads)")]:::storage
    end

    WebApp & SwaggerUI -->|Authorization: Bearer JWT| JwtFilter
    PayGateway -->|Callback Event| WebhookAuth
    JwtFilter --> Auth & Catalog & CartWish & OrderEng & PayStrat
    WebhookAuth --> PayStrat
    OrderEng ==>|" SELECT ... FOR UPDATE "| Postgres
    PayStrat ==>|Idempotent TX| Postgres
    Auth & Catalog & CartWish --> Postgres
    Catalog --> Uploads
```

---

## 🗄️ Database Schema (ERD)

Designed for **3NF data integrity**, **historical immutability** (snapshotting prices and shipping addresses at
checkout), and **unique idempotency constraints**.

```mermaid
erDiagram
    USERS ||--o{ ADDRESSES: "manages"
    USERS ||--|| CARTS: "owns"
    USERS ||--o{ ORDERS: "places"
    USERS ||--o{ USER_WISHLIST: "saves"
    CATEGORY ||--o{ PRODUCT: "classifies"
    PRODUCT ||--o{ IMAGE: "has"
    PRODUCT ||--o{ USER_WISHLIST: "featured in"
    CARTS ||--o{ CART_ITEM: "contains"
    PRODUCT ||--o{ CART_ITEM: "added to"
    ORDERS ||--o{ ORDER_ITEMS: "snapshots"
    PRODUCT ||--o{ ORDER_ITEMS: "ordered in"
    ORDERS ||--o{ PAYMENTS: "settled via"

    USERS {
        bigint id PK
        string email UK
        string password
        string first_name
        string last_name
        enum role "USER | ADMIN"
    }

    PRODUCT {
        bigint id PK
        string name
        string brand
        decimal price
        int inventory "Locked on Checkout"
        int quantity "Units Sold"
        bigint category_id FK
    }

    ORDERS {
        bigint id PK
        bigint user_id FK
        enum status "PENDING | CONFIRMED | SHIPPED | DELIVERED | CANCELLED"
        decimal total_amount
        string shipping_street
        string shipping_city
        timestamp order_date
    }

    PAYMENTS {
        bigint id PK
        bigint order_id FK
        string transaction_reference UK
        string idempotency_key UK
        enum payment_method "CARD | CASH_ON_DELIVERY"
        enum status "PENDING | SUCCEEDED | FAILED | REFUNDED"
        decimal amount
    }
```

---

## ⚡ Concurrency & Payment Flow

### How Cartify Prevents Overselling & Double-Charging

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as 🛍️ Customer
    participant API as ⚙️ Cartify API
    participant DB as 🐘 PostgreSQL 17
    participant PSP as 💳 Payment Gateway

    rect rgb(15, 23, 42)
        Note over Buyer, DB: Phase 1: Pessimistic-Locked Checkout
        Buyer ->> API: POST /api/orders/checkout { addressId }
        API ->> DB: BEGIN TRANSACTION
        API ->> DB: SELECT * FROM product WHERE id IN (...) FOR UPDATE
        DB -->> API: Row Locks Acquired (Concurrent Buyers Queued)
        API ->> DB: Verify Stock -> Deduct Inventory -> Snapshot Address & Prices
        API ->> DB: Save Order (PENDING) & Clear Cart -> COMMIT
        API -->> Buyer: 201 Created { orderId, totalAmount }
    end

    rect rgb(30, 41, 59)
        Note over Buyer, PSP: Phase 2: Idempotent Payment Initiation
        Buyer ->> API: POST /api/payments/initiate { orderId, CARD, idempotencyKey }
        API ->> DB: Lookup idempotencyKey
        alt Key Already Exists
            DB -->> API: Return Existing Payment (Zero Duplicate Charge)
        else New Payment Request
            API ->> DB: Save Payment (PENDING) + Generate TXN Reference
        end
        API -->> Buyer: 201 Created { transactionReference, paymentUrl }
    end

    rect rgb(17, 24, 39)
        Note over PSP, DB: Phase 3: Verified Asynchronous Webhook
        PSP ->> API: POST /api/payments/webhook [Header: X-Webhook-Secret]
        API ->> API: Verify Webhook Secret Signature
        alt Status == SUCCEEDED
            API ->> DB: Payment -> SUCCEEDED | Order -> CONFIRMED
        else Status == FAILED
            API ->> DB: Payment -> FAILED | Order -> CANCELLED | Restore Stock
        end
        API -->> PSP: 200 OK
    end
```

---

## 🧠 Engineering Decisions & Trade-offs

| Architectural Challenge                 | Solution Implemented                                                                         | Engineering Impact                                                                                    |
|:----------------------------------------|:---------------------------------------------------------------------------------------------|:------------------------------------------------------------------------------------------------------|
| **Concurrent Checkout Race Conditions** | JPA `@Lock(LockModeType.PESSIMISTIC_WRITE)`                                                  | Guarantees zero negative inventory even when multiple threads purchase the final unit simultaneously. |
| **Catalog Mutations After Purchase**    | **Order Snapshot Pattern** (copying `unitPrice` & address fields onto `Order` / `OrderItem`) | Prevents admin price edits or user address deletions from corrupting historical financial invoices.   |
| **Adding Future Payment Providers**     | **Strategy Pattern** (`PaymentStrategy` interface + Spring DI map)                           | Adding Apple Pay or PayPal requires zero changes to `PaymentServiceImpl` (**Open/Closed Principle**). |
| **Network Retries & Double Clicks**     | Database `UNIQUE` constraint on `idempotencyKey`                                             | Safely returns the original payment session on duplicate client retries without re-charging.          |
| **ORM $N+1$ Query Degradation**         | `@EntityGraph` + Java `record` DTO Projections                                               | Fetches relational graphs in single `LEFT JOIN` queries and prevents accidental lazy-loading leaks.   |
| **Container Attack Surface**            | **Multi-Stage Dockerfile** + dedicated non-root `spring` user                                | Strips Maven/JDK build tools from production runtime and isolates filesystem permissions.             |

---

## 🚀 One-Command Deployment

Boot the entire production stack (**Spring Boot 4 API + PostgreSQL 17** with healthchecks and persistent volumes) in
seconds:

```bash
# 1. Clone the repository
git clone https://github.com/AK-Elshamy/cartify.git
cd cartify

# 2. Build the multi-stage image and start all containers
docker compose up --build -d

# 3. Check container health status
docker compose ps
```

### 🌐 Live Interactive Endpoints

| Service                           | URL                                     | Notes                                        |
|:----------------------------------|:----------------------------------------|:---------------------------------------------|
| **Swagger UI (Interactive Docs)** | `http://localhost:8080/swagger-ui.html` | Click **Authorize** and paste your JWT token |
| **OpenAPI 3.0 JSON Spec**         | `http://localhost:8080/v3/api-docs`     | Importable into Postman / Bruno              |
| **REST API Base URL**             | `http://localhost:8080/api`             | Ready for frontend integration               |

---

## 📡 API Reference

<details>
<summary><b>🔐 Authentication & Users</b> (Click to expand)</summary>

| Method | Endpoint             | Auth             | Description                                         |
|:-------|:---------------------|:-----------------|:----------------------------------------------------|
| `POST` | `/api/auth/register` | Public           | Register a new customer (`USER` role)               |
| `POST` | `/api/auth/login`    | Public           | Authenticate credentials and issue JWT Bearer token |
| `GET`  | `/api/users/me`      | `USER` / `ADMIN` | Retrieve authenticated user profile                 |

</details>

<details>
<summary><b>📦 Products, Categories & Wishlist</b> (Click to expand)</summary>

| Method | Endpoint                    | Auth    | Description                                                     |
|:-------|:----------------------------|:--------|:----------------------------------------------------------------|
| `GET`  | `/api/products`             | Public  | Paginated & filterable product listing (`page`, `size`, `sort`) |
| `POST` | `/api/products`             | `ADMIN` | Create a new product with initial inventory                     |
| `POST` | `/api/images/upload`        | `ADMIN` | Upload product images to persistent Docker volume               |
| `GET`  | `/api/categories`           | Public  | List all product categories                                     |
| `POST` | `/api/wishlist/{productId}` | `USER`  | Toggle product in personal wishlist                             |

</details>

<details>
<summary><b>🛒 Shopping Cart, Addresses & Orders</b> (Click to expand)</summary>

| Method  | Endpoint                       | Auth    | Description                                        |
|:--------|:-------------------------------|:--------|:---------------------------------------------------|
| `GET`   | `/api/cart`                    | `USER`  | Get active shopping cart and calculated subtotal   |
| `POST`  | `/api/cart/items`              | `USER`  | Add item or update quantity in cart                |
| `POST`  | `/api/addresses`               | `USER`  | Save a shipping address                            |
| `POST`  | `/api/orders/checkout`         | `USER`  | Execute pessimistic-locked checkout & create order |
| `PATCH` | `/api/orders/{orderId}/cancel` | `USER`  | Cancel pending order and restore reserved stock    |
| `PATCH` | `/api/orders/{orderId}/status` | `ADMIN` | Advance order fulfillment lifecycle                |

</details>

<details open>
<summary><b>💳 Payments & Webhooks</b> (Click to expand)</summary>

| Method | Endpoint                        | Auth               | Description                                   |
|:-------|:--------------------------------|:-------------------|:----------------------------------------------|
| `POST` | `/api/payments/initiate`        | `USER`             | Initiate `CARD` or `CASH_ON_DELIVERY` payment |
| `POST` | `/api/payments/webhook`         | `X-Webhook-Secret` | Gateway callback (`SUCCEEDED` / `FAILED`)     |
| `GET`  | `/api/payments/order/{orderId}` | `USER`             | Retrieve payment history for an order         |

**Sample Checkout & Payment Payload:**

```json
// POST /api/payments/initiate
{
  "orderId": 1,
  "paymentMethod": "CARD",
  "idempotencyKey": "checkout-order-1-attempt-1"
}
```

```json
// 201 Created Response
{
  "id": 1,
  "orderId": 1,
  "transactionReference": "TXN-89F2A1C4-91B",
  "paymentMethod": "CARD",
  "status": "PENDING",
  "orderStatus": "PENDING",
  "amount": 1500.00,
  "paymentUrl": "https://checkout.cartify-pay.local/pay/TXN-89F2A1C4-91B",
  "createdAt": "2026-11-21T09:30:00.124510"
}
```

</details>

---

## 🧪 Automated Test Suite

Run the isolated unit test suite powered by **JUnit 5**, **Mockito**, and **AssertJ**:

```bash
./mvnw test
```

* **`OrderServiceImplTest`** — Verifies stock deduction, price/address snapshotting, cart clearing, empty-cart
  rejection, `InsufficientStockException` rollback, and inventory restoration on cancellation.
* **`PaymentServiceImplTest`** — Verifies payment strategy resolution, duplicate `idempotencyKey` short-circuiting,
  invalid webhook secret rejection (`403 Forbidden`), order confirmation on `SUCCEEDED`, and automatic stock rollback on
  `FAILED`.

---

## 📂 Project Structure

```text
cartify/
├── src/
│   ├── main/java/com/elshamy/cartify/
│   │   ├── config/          # Spring Security, OpenAPI 3 & Web MVC configurations
│   │   ├── controller/      # REST controllers with Jakarta Bean Validation
│   │   ├── dtos/            # Immutable Java Record request/response contracts
│   │   ├── exception/       # Centralized @RestControllerAdvice error mapping
│   │   ├── model/           # JPA Entities, Enums & relational mappings
│   │   ├── repository/      # Spring Data JPA repositories & row-level locks
│   │   ├── security/        # Stateless JWT filter, token provider & UserDetails
│   │   └── service/         # Domain business logic & Payment Strategy implementations
│   └── test/java/com/elshamy/cartify/
│       └── service/         # JUnit 5 & Mockito test suites
├── Dockerfile               # Multi-stage Java 25 build with non-root runtime user
├── docker-compose.yml       # Health-checked PostgreSQL 17 + API orchestration
└── pom.xml                  # Maven build & dependency management
```

---

<div align="center">

### 👨‍💻 Architected & Engineered by **Ahmed Elshamy**

[![GitHub](https://img.shields.io/badge/GitHub-AK--Elshamy-181717?style=for-the-badge&logo=github)](https://github.com/AK-Elshamy)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/a-elshamy)

*⭐ Star this repository if you appreciate clean backend architecture!*

<img src="https://capsule-render.vercel.app/api?type=waving&color=0:0F172A,50:1E3A8A,100:0EA5E9&height=110&section=footer" width="100%" />

</div>