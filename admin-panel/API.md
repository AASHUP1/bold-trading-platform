# API Specification: BOLD Admin & Compliance Console
**Document:** API.md  
**Product:** BOLD Control Room (Admin Panel)  
**Base URL:** `https://api.boldtrading.internal/v1`  
**API Versioning:** URL Path Prefix (`/v1/admin/...`)  
**Source of Truth:** [`admin-panel/PRD.md`](file:///d:/trading%20app/admin-panel/PRD.md), [`admin-panel/ARCHITECTURE.md`](file:///d:/trading%20app/admin-panel/ARCHITECTURE.md), and [`admin-panel/DATABASE.md`](file:///d:/trading%20app/admin-panel/DATABASE.md)  

---

## 1. Global Standards & Conventions

### 1.1 API Versioning Rules
* All endpoints use the path-based version prefix `/v1/`.
* Breaking changes will increment the major version (`/v2/`). Minor backward-compatible enhancements (new optional fields) will not change the route prefix.

### 1.2 Standard Error Response Envelope
Every non-2xx HTTP response returns a unified JSON error payload:
```json
{
  "error": {
    "code": "KEY_ROTATION_FAILED",
    "message": "The provided Google Gemini API key failed connectivity verification.",
    "status_code": 422,
    "request_id": "req_01HPX7K9V4N8Y",
    "timestamp": "2026-11-14T12:00:00Z",
    "details": [
      {
        "field": "encrypted_payload",
        "issue": "Invalid API key provided: HTTP 400 API_KEY_INVALID from upstream provider"
      }
    ]
  }
}
```

### 1.3 Pagination, Filtering, and Sorting
* **Pagination (Query Params):** `page` (default `1`) and `limit` (default `25`, max `100`).
* **Metadata Envelope:**
  ```json
  "meta": {
    "page": 1,
    "limit": 25,
    "total_records": 128,
    "total_pages": 6,
    "has_next": true,
    "has_prev": false
  }
  ```
* **Sorting:** `sort_by={column}&order={asc|desc}` (e.g., `?sort_by=created_at&order=desc`).
* **Filtering:** Standard key-value query parameters (e.g., `?status=pending&risk_category=high`).

### 1.4 Rate Limiting & Admin Headers
* Standard Admin Rate Limit: `120 requests/minute` per IP address.
* Sensitive Mutation Rate Limit (`/rotate`, `/kill-switch`): `10 requests/minute`.
* **Standard Request Headers:**
  * `Authorization: Bearer <Firebase_Admin_JWT>`
  * `Content-Type: application/json`
  * `X-Request-Id: <uuid_or_nanoid>` (Optional, generated if missing)

---

## 2. Resource Endpoints

### 2.1 Universal Key & Secret Management (`api_keys_vault`)
Maps to `PRD.md` Section 3.1 & `DATABASE.md` Table `api_keys_vault`.

#### 2.1.1 List All Configured Keys
* **Method & URL:** `GET /v1/admin/keys`
* **Purpose:** Retrieve all stored API keys in masked format with status, latency, and quota metrics.
* **Auth Required:** Yes (`super_admin`, `operations_admin`)
* **Headers:** `Authorization: Bearer <token>`
* **Query Params:** `?category=market_data&status=valid`
* **Success Response (200 OK):**
```json
{
  "data": [
    {
      "id": "e0000000-0000-0000-0000-000000000001",
      "provider": "gemini_api",
      "key_category": "ai_model",
      "environment": "production",
      "masked_value": "sk-gemini-••••••••9A12",
      "status": "valid",
      "current_latency_ms": 140,
      "quota_limit": 1000000,
      "quota_used": 820000,
      "last_tested_at": "2026-11-14T09:41:00Z",
      "last_rotated_at": "2026-10-15T08:00:00Z",
      "updated_by": "a0000000-0000-0000-0000-000000000001"
    }
  ],
  "meta": { "page": 1, "limit": 25, "total_records": 7, "total_pages": 1 }
}
```
* **Errors:** `401 Unauthorized`, `403 Forbidden`.

#### 2.1.2 Rotate API Key Secret
* **Method & URL:** `POST /v1/admin/keys/{provider}/rotate`
* **Purpose:** Overwrite an existing API key secret with a new credential, encrypt server-side, test ping, and write audit event.
* **Auth Required:** Yes (`super_admin`, `operations_admin`)
* **Headers:** `Authorization: Bearer <token>`, `Content-Type: application/json`
* **Request Body:**
```json
{
  "raw_secret": "AIzaSyB9X1Y2Z3W4V5U6T7S8R9Q0P1O2N3M4L5",
  "environment": "production",
  "verify_connection_immediately": true
}
```
* **Success Response (200 OK):**
```json
{
  "message": "API key for gemini_api rotated successfully.",
  "key": {
    "provider": "gemini_api",
    "masked_value": "AIzaSyB••••••••M4L5",
    "status": "valid",
    "latency_ms": 115,
    "last_rotated_at": "2026-11-14T12:05:00Z"
  },
  "audit_event_id": "EVT_10490"
}
```
* **Errors:**
  * `400 Bad Request`: Payload validation failed (`raw_secret` too short).
  * `422 Unprocessable Entity`: Ping to upstream provider failed.

#### 2.1.3 Test Ping Key Connectivity
* **Method & URL:** `POST /v1/admin/keys/{provider}/test-ping`
* **Purpose:** Fire a dry-run test request to verify API key validity and measure round-trip latency.
* **Auth Required:** Yes (`super_admin`, `operations_admin`)
* **Success Response (200 OK):**
```json
{
  "provider": "gemini_api",
  "status": "valid",
  "latency_ms": 128,
  "tested_at": "2026-11-14T12:06:00Z"
}
```
* **Errors:** `400 Bad Request`, `504 Gateway Timeout`.

---

### 2.2 Telemetry & Feed Health (`feed_health_logs`)
Maps to `PRD.md` S21 & `DATABASE.md` Table `feed_health_logs`.

#### 2.2.1 Get Platform Overview Telemetry
* **Method & URL:** `GET /v1/admin/telemetry/overview`
* **Purpose:** Return high-level metric cards for S21 (Total Users, Subscriptions, Feed Health, Error Rate).
* **Auth Required:** Yes (`super_admin`, `operations_admin`, `compliance_lead`, `quant_lead`)
* **Success Response (200 OK):**
```json
{
  "total_users": 24860,
  "user_growth_month_percent": 8.4,
  "active_subscriptions": 4218,
  "subscription_tier_breakdown": { "free": 4218, "pro_beta": 0, "premium_beta": 0 },
  "overall_feed_health": "degraded",
  "api_error_rate_percent": 0.42,
  "api_error_threshold_percent": 0.50,
  "active_kill_switch": false,
  "timestamp": "2026-11-14T12:07:00Z"
}
```

#### 2.2.2 Get Adapter Timeseries Latency
* **Method & URL:** `GET /v1/admin/telemetry/latency-series`
* **Purpose:** Returns 60 downsampled points for the S21 latency area chart (0–800ms).
* **Query Params:** `?adapter_id=NSE_ADAPTER&minutes=60`
* **Success Response (200 OK):**
```json
{
  "adapter_id": "NSE_ADAPTER",
  "points": [
    { "timestamp": "2026-11-14T11:07:00Z", "latency_ms": 180, "error_rate": 0.001 },
    { "timestamp": "2026-11-14T11:37:00Z", "latency_ms": 680, "error_rate": 0.008 }
  ]
}
```

---

### 2.3 Feature Flags & Emergency Kill Switch (`feature_flags`)
Maps to `PRD.md` Section 3.1 & `DATABASE.md` Table `feature_flags`.

#### 2.3.1 List Feature Flags
* **Method & URL:** `GET /v1/admin/governance/flags`
* **Purpose:** List all platform switches (e.g., `GLOBAL_FREE_TIER_OVERRIDE`, `F_AND_O_CHAIN_ENABLED`).
* **Auth Required:** Yes (All Admin Roles)
* **Success Response (200 OK):**
```json
{
  "flags": [
    {
      "id": "f0000000-0000-0000-0000-000000000001",
      "flag_key": "GLOBAL_FREE_TIER_OVERRIDE",
      "is_enabled": true,
      "description": "Unlocks Pro and Premium features for 100% free user access",
      "module": "billing",
      "updated_at": "2026-11-14T09:00:00Z"
    }
  ]
}
```

#### 2.3.2 Toggle Emergency Platform Kill Switch
* **Method & URL:** `POST /v1/admin/governance/kill-switch`
* **Purpose:** Immediately pause or resume all signal generation and paper trading simulations across the entire platform.
* **Auth Required:** Yes (`super_admin`, `operations_admin`, `compliance_lead`)
* **Request Body:**
```json
{
  "kill_switch_active": true,
  "reason": "Elevated latency (>700ms) and corrupted volume feed detected on NSE primary line."
}
```
* **Success Response (200 OK):**
```json
{
  "kill_switch_active": true,
  "halted_modules": ["signal_engine", "paper_order_execution"],
  "activated_at": "2026-11-14T12:08:15Z",
  "audit_event_id": "EVT_10492"
}
```
* **Errors:** `400 Bad Request` (reason string required), `403 Forbidden`.

---

### 2.4 Strategy Review & Compliance Queue (`strategy_reviews`)
Maps to `PRD.md` Section 3.2 & `DATABASE.md` Table `strategy_reviews`.

#### 2.4.1 List Strategies Pending Review
* **Method & URL:** `GET /v1/admin/strategies/queue`
* **Query Params:** `?status=pending&risk_category=medium&page=1&limit=20`
* **Success Response (200 OK):**
```json
{
  "data": [
    {
      "id": "s0000000-0000-0000-0000-000000000001",
      "strategy_version_id": "sv000000-0000-0000-0000-000000000001",
      "strategy_name": "NIFTY Momentum v1.4",
      "submitted_by": "Quant Team",
      "risk_category": "medium",
      "backtest_win_rate": 58.60,
      "backtest_max_drawdown": 6.20,
      "decision": "pending",
      "created_at": "2026-11-14T09:30:00Z"
    }
  ],
  "meta": { "page": 1, "limit": 20, "total_records": 3, "total_pages": 1 }
}
```

#### 2.4.2 Submit Strategy Review Decision
* **Method & URL:** `POST /v1/admin/strategies/queue/{id}/decision`
* **Request Body:**
```json
{
  "decision": "approved", // 'approved' | 'changes_requested' | 'rejected'
  "review_notes": "Backtest metrics verified with 0.05% slippage and regulatory taxes included. Rule conditions comply with SEBI educational guidelines."
}
```
* **Success Response (200 OK):**
```json
{
  "id": "s0000000-0000-0000-0000-000000000001",
  "decision": "approved",
  "decided_at": "2026-11-14T12:10:00Z",
  "reviewer_email": "compliance.lead@boldtrading.internal"
}
```

---

### 2.5 Tamper-Evident Audit Trail (`audit_events`)
Maps to `PRD.md` Section 3.2 & `DATABASE.md` Table `audit_events`.

#### 2.5.1 Query Audit Logs
* **Method & URL:** `GET /v1/admin/audit/events`
* **Query Params:** `?action_category=key_rotation&actor_email=ops.admin@boldtrading.internal&page=1&limit=50`
* **Success Response (200 OK):**
```json
{
  "data": [
    {
      "id": "au000000-0000-0000-0000-000000000001",
      "event_hash": "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
      "actor_email": "ops.admin@boldtrading.internal",
      "actor_role": "operations_admin",
      "action_category": "key_rotation",
      "target_resource": "api_keys_vault:gemini_api",
      "ip_address": "10.0.0.24",
      "created_at": "2026-11-14T12:05:00Z"
    }
  ],
  "meta": { "page": 1, "limit": 50, "total_records": 10482, "total_pages": 210 }
}
```

---

## 3. Database Mapping Verification

| Endpoint | Mapped PRD Feature | Mapped Database Table | Validation Status |
| :--- | :--- | :--- | :--- |
| `GET/POST /v1/admin/keys/*` | Section 3.1: Universal Key & Secret Manager | `api_keys_vault` | **Strict Match** |
| `GET /v1/admin/telemetry/*` | Section 3.1: Platform Health & S21 | `feed_health_logs` | **Strict Match** |
| `POST /v1/admin/governance/kill-switch` | Section 3.1: Emergency Kill Switch | `feature_flags`, `audit_events` | **Strict Match** |
| `GET /v1/admin/governance/flags` | Section 3.1: Free Tier & Feature Flags | `feature_flags` | **Strict Match** |
| `GET/POST /v1/admin/strategies/*` | Section 3.2: Strategy Review Queue | `strategy_reviews` | **Strict Match** |
| `GET /v1/admin/audit/events` | Section 3.2: Audit Trail Viewer | `audit_events` | **Strict Match** |
