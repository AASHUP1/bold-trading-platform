# API Specification: BOLD Mobile Application
**Document:** API.md  
**Product:** BOLD Android Trading & Research Platform  
**Base URL:** `https://api.boldtrading.internal/v1`  
**Streaming URL:** `wss://api.boldtrading.internal/v1/stream/market`  
**API Versioning:** URL Path Prefix (`/v1/...`)  
**Source of Truth:** [`mobile-application/PRD.md`](file:///d:/trading%20app/mobile-application/PRD.md), [`mobile-application/ARCHITECTURE.md`](file:///d:/trading%20app/mobile-application/ARCHITECTURE.md), and [`mobile-application/DATABASE.md`](file:///d:/trading%20app/mobile-application/DATABASE.md)  

---

## 1. Global Standards & Conventions

### 1.1 API Versioning Rules
* All mobile endpoints use the path-based prefix `/v1/`.
* Deprecated fields are marked with `Sunset: <date>` HTTP headers. Breaking schema alterations will publish a new `/v2/` prefix.

### 1.2 Standard Unified Error Envelope
All error responses adhere strictly to this schema:
```json
{
  "error": {
    "code": "DAILY_RISK_LIMIT_EXCEEDED",
    "message": "Simulated order rejected: Exceeds your configured 2.0% daily virtual capital loss limit.",
    "status_code": 422,
    "request_id": "req_mob_01HPX8M123",
    "timestamp": "2026-11-14T09:41:00Z",
    "details": [
      {
        "field": "stop_loss",
        "issue": "Trade risk is ₹24,000; available daily risk budget remaining is ₹12,000"
      }
    ]
  }
}
```

### 1.3 Pagination, Sorting, and Headers
* **Pagination (Query Params):** `page` (default `1`) and `limit` (default `20`, max `50`).
* **Pagination Metadata:**
  ```json
  "meta": {
    "page": 1,
    "limit": 20,
    "total_records": 48,
    "has_next": true
  }
  ```
* **Sorting:** `?sort_by=exchange_timestamp&order=desc`.
* **Standard Client Request Headers:**
  * `Authorization: Bearer <Mobile_JWT>` (for authenticated routes)
  * `Content-Type: application/json`
  * `X-Client-Version: 1.0.0-android`
  * `X-Idempotency-Key: <uuid>` (Mandatory for simulated order creation)

### 1.4 Rate Limiting
* Public endpoints (Auth, Quotes search): `60 requests/minute`.
* Authenticated endpoints (Paper orders, Watchlists): `120 requests/minute`.
* Heavy computational jobs (Backtesting, AI queries): `10 requests/minute`.

---

## 2. Resource Endpoints

### 2.1 Authentication & Workspace (`users`, `workspaces`)
Maps to Screens `S01`, `S02`, `S03` & `DATABASE.md` Tables `users`, `workspaces`.

#### 2.1.1 Request OTP
* **Method & URL:** `POST /v1/auth/otp/request`
* **Purpose:** Request a 6-digit SMS verification code to sign in or register.
* **Auth Required:** No
* **Request Body:**
```json
{
  "phone": "+919876543210"
}
```
* **Success Response (200 OK):**
```json
{
  "message": "OTP dispatched successfully.",
  "expires_in_seconds": 120,
  "retry_after_seconds": 30
}
```
* **Errors:** `400 Bad Request` (invalid phone format), `429 Too Many Requests`.

#### 2.1.2 Verify OTP
* **Method & URL:** `POST /v1/auth/otp/verify`
* **Purpose:** Verify 6-digit code and exchange for a JWT session token.
* **Auth Required:** No
* **Request Body:**
```json
{
  "phone": "+919876543210",
  "otp": "248106"
}
```
* **Success Response (200 OK):**
```json
{
  "access_token": "eyJhbGciOi...",
  "token_type": "Bearer",
  "expires_in": 2592000,
  "is_new_user": false,
  "user": {
    "id": "c0000000-0000-0000-0000-000000000001",
    "phone": "+919876543210",
    "status": "active"
  }
}
```
* **Errors:** `400 Bad Request` (expired or incorrect OTP).

#### 2.1.3 Get Entitlements (Free Launch Mode)
* **Method & URL:** `GET /v1/entitlements`
* **Purpose:** Returns active feature permissions (100% Free at launch).
* **Auth Required:** Yes (`user`)
* **Success Response (200 OK):**
```json
{
  "tier": "FREE_PRO_UNLOCKED",
  "is_free_launch_active": true,
  "features": {
    "scanner_unlimited": true,
    "fno_option_chain": true,
    "strategy_builder": true,
    "ai_assistant_monthly_queries": 250
  }
}
```

---

### 2.2 Market Data & Instruments (`instruments`, `market_candles`)
Maps to Screens `S04`, `S05`, `S06`, `S07`, `S11` & `DATABASE.md` Tables `instruments`, `market_candles`.

#### 2.2.1 Search Instruments
* **Method & URL:** `GET /v1/instruments/search`
* **Purpose:** Instant debounced search for equities and F&O derivatives.
* **Auth Required:** Yes
* **Query Params:** `?q=RELIANCE&exchange=NSE`
* **Success Response (200 OK):**
```json
{
  "results": [
    {
      "id": "b0000000-0000-0000-0000-000000000001",
      "exchange": "NSE",
      "symbol": "RELIANCE",
      "name": "Reliance Industries Ltd.",
      "instrument_type": "equity",
      "tick_size": 0.05,
      "lot_size": 1,
      "last_price": 2948.50,
      "change_percent": 1.24
    }
  ]
}
```

#### 2.2.2 Get Historical OHLCV Candles
* **Method & URL:** `GET /v1/market/candles`
* **Purpose:** Fetch historical candlestick arrays for interactive charts (`S07`).
* **Auth Required:** Yes
* **Query Params:** `?instrument_id=b0000000-0000-0000-0000-000000000001&timeframe=15m&limit=100`
* **Success Response (200 OK):**
```json
{
  "instrument_id": "b0000000-0000-0000-0000-000000000001",
  "timeframe": "15m",
  "data_mode": "delayed",
  "candles": [
    {
      "open": 2939.10,
      "high": 2952.00,
      "low": 2934.20,
      "close": 2948.50,
      "volume": 4280000,
      "vwap": 2928.10,
      "timestamp": "2026-11-14T09:30:00Z"
    }
  ]
}
```

#### 2.2.3 Get Option Chain Desk
* **Method & URL:** `GET /v1/market/option-chain`
* **Purpose:** Fetch strikes, Greeks, and open interest for `S11`.
* **Query Params:** `?underlying=NIFTY&expiry=2026-11-26`
* **Success Response (200 OK):**
```json
{
  "underlying": "NIFTY 50",
  "underlying_price": 24836.30,
  "pcr": 1.12,
  "atm_strike": 24800.00,
  "strikes": [
    {
      "strike": 24800.00,
      "call": {
        "ltp": 136.80,
        "oi": 6840000,
        "oi_change": 8.2,
        "iv": 14.8,
        "delta": 0.52,
        "gamma": 0.0018,
        "theta": -8.4,
        "vega": 12.6
      },
      "put": {
        "ltp": 151.60,
        "oi": 6210000,
        "oi_change": -3.1,
        "iv": 15.2,
        "delta": -0.48,
        "gamma": 0.0018,
        "theta": -8.1,
        "vega": 12.4
      }
    }
  ]
}
```

#### 2.2.4 Real-Time WebSocket Market Stream
* **Protocol & URL:** `WS /v1/stream/market`
* **Purpose:** Multiplexed price ticks, order book depth, and simulation fill notifications.
* **Subscribe Frame (Client $\rightarrow$ Server):**
```json
{
  "action": "subscribe",
  "instruments": ["b0000000-0000-0000-0000-000000000001"]
}
```
* **Tick Event Frame (Server $\rightarrow$ Client):**
```json
{
  "event": "market.tick",
  "instrument_id": "b0000000-0000-0000-0000-000000000001",
  "last_price": 2948.50,
  "volume": 4280000,
  "exchange_timestamp": "2026-11-14T09:41:00Z",
  "data_mode": "delayed"
}
```

---

### 2.3 Paper Trading & Virtual Ledger (`paper_accounts`, `paper_orders`, `ledger_entries`)
Maps to Screen `S14` & `DATABASE.md` Tables `paper_accounts`, `paper_orders`, `paper_positions`, `ledger_entries`.

#### 2.3.1 Get Paper Account & Portfolio
* **Method & URL:** `GET /v1/paper/accounts/me`
* **Purpose:** Fetch virtual balances, equity, margin used, and realized/unrealized P&L.
* **Auth Required:** Yes
* **Success Response (200 OK):**
```json
{
  "account_id": "d0000000-0000-0000-0000-000000000001",
  "virtual_capital": 1000000.00,
  "available_cash": 800000.00,
  "margin_used": 120000.00,
  "total_equity": 1042680.00,
  "unrealized_pnl": 2425.00,
  "realized_pnl": 40255.00,
  "daily_risk_utilization_percent": 12.0
}
```

#### 2.3.2 Submit Simulated Paper Order
* **Method & URL:** `POST /v1/paper/orders`
* **Purpose:** Submit a virtual market/limit order with risk validation.
* **Auth Required:** Yes
* **Headers:** `X-Idempotency-Key: uuid-v4-string`
* **Request Body:**
```json
{
  "instrument_id": "b0000000-0000-0000-0000-000000000001",
  "direction": "BUY",
  "order_type": "MARKET",
  "quantity": 10,
  "requested_price": 2948.50,
  "stop_loss": 2910.00,
  "target": 3020.00
}
```
* **Success Response (201 Created):**
```json
{
  "order_id": "po000000-0000-0000-0000-000000000001",
  "status": "FILLED",
  "filled_price": 2948.50,
  "quantity": 10,
  "order_value": 29485.00,
  "estimated_fees": 5.90,
  "total_debit": 29490.90,
  "available_cash_after": 770509.10,
  "is_simulation": true,
  "filled_at": "2026-11-14T09:41:00Z"
}
```
* **Errors:**
  * `422 Unprocessable Entity`: Daily loss budget exceeded or missing stop-loss.
  * `409 Conflict`: Replay of identical `X-Idempotency-Key`.

#### 2.3.3 Reset Paper Account
* **Method & URL:** `POST /v1/paper/accounts/reset`
* **Purpose:** Reset positions, open orders, and balance back to ₹10,00,000 virtual cash.
* **Auth Required:** Yes
* **Success Response (200 OK):**
```json
{
  "message": "Paper account reset to ₹10,00,000.00. Previous records archived.",
  "new_balance": 1000000.00,
  "reset_count": 1,
  "reset_timestamp": "2026-11-14T12:15:00Z"
}
```

---

### 2.4 Watchlists (`watchlists`, `watchlist_items`)
Maps to Screen `S06` & `DATABASE.md` Tables `watchlists`, `watchlist_items`.

#### 2.4.1 Get User Watchlists
* **Method & URL:** `GET /v1/watchlists`
* **Auth Required:** Yes
* **Success Response (200 OK):**
```json
{
  "watchlists": [
    {
      "id": "w0000000-0000-0000-0000-000000000001",
      "name": "Core Holdings",
      "items": [
        {
          "instrument_id": "b0000000-0000-0000-0000-000000000001",
          "symbol": "RELIANCE",
          "last_price": 2948.50,
          "change_percent": 1.24,
          "volume": 4280000
        }
      ]
    }
  ]
}
```

#### 2.4.2 Add Instrument to Watchlist
* **Method & URL:** `POST /v1/watchlists/{id}/items`
* **Request Body:**
```json
{
  "instrument_id": "b0000000-0000-0000-0000-000000000002"
}
```
* **Success Response (201 Created):**
```json
{
  "message": "Instrument added to watchlist.",
  "watchlist_id": "w0000000-0000-0000-0000-000000000001",
  "instrument_id": "b0000000-0000-0000-0000-000000000002"
}
```

---

### 2.5 Algo Lab & Backtesting (`strategies`, `strategy_versions`)
Maps to Screens `S12`, `S13` & `DATABASE.md` Tables `strategies`, `strategy_versions`.

#### 2.5.1 Save Strategy Draft
* **Method & URL:** `POST /v1/strategies`
* **Request Body:**
```json
{
  "name": "NIFTY momentum",
  "timeframe": "15m",
  "rules_ast": {
    "entry": {
      "operator": "AND",
      "conditions": [
        { "indicator": "EMA", "period": 20, "comparison": "crosses_above", "target_indicator": "EMA", "target_period": 50 },
        { "indicator": "RSI", "period": 14, "comparison": ">", "value": 55 }
      ]
    },
    "exit": {
      "operator": "OR",
      "conditions": [
        { "price": "close", "comparison": "<", "indicator": "EMA", "period": 20 }
      ]
    },
    "risk": {
      "stop_loss_percent": 1.0,
      "trailing_stop_percent": 0.5,
      "daily_loss_cap_percent": 2.0
    }
  }
}
```
* **Success Response (201 Created):**
```json
{
  "strategy_id": "st000000-0000-0000-0000-000000000001",
  "version_number": 1,
  "status": "VALIDATED"
}
```

#### 2.5.2 Execute Backtest Job
* **Method & URL:** `POST /v1/strategies/{id}/backtests`
* **Purpose:** Queue historical simulation over past 12 months with modeled slippage and taxes (`S13`).
* **Request Body:**
```json
{
  "start_date": "2026-01-01",
  "end_date": "2026-10-31",
  "starting_capital": 1000000.00,
  "slippage_per_side_percent": 0.05,
  "brokerage_per_order": 20.00
}
```
* **Success Response (202 Accepted):**
```json
{
  "backtest_id": "bt000000-0000-0000-0000-000000000001",
  "status": "PROCESSING",
  "estimated_duration_seconds": 3
}
```

---

### 2.6 Signals Feed (`signals`)
Maps to Screen `S15` & `DATABASE.md` Table `signals`.

#### 2.6.1 List Active Signals
* **Method & URL:** `GET /v1/signals`
* **Query Params:** `?status=ACTIVE&timeframe=15m`
* **Success Response (200 OK):**
```json
{
  "signals": [
    {
      "id": "sig00000-0000-0000-0000-000000000001",
      "symbol": "RELIANCE",
      "strategy_name": "VWAP reclaim / v1.4",
      "direction": "BUY",
      "timeframe": "15m",
      "entry_zone": { "min": 2932.00, "max": 2950.00 },
      "stop_loss": 2910.00,
      "targets": [2970.00, 2995.00, 3020.00],
      "rationale": "Close above VWAP with 1.8x average volume.",
      "triggered_at": "2026-11-14T09:30:00Z",
      "valid_until": "2026-11-14T15:00:00Z"
    }
  ]
}
```

---

### 2.7 AI Research Assistant (`research_reports`)
Maps to Screen `S10`.

#### 2.7.1 Ask AI Research Query
* **Method & URL:** `POST /v1/research/ask`
* **Purpose:** Natural-language financial queries over audited filings and earnings commentary with citations.
* **Request Body:**
```json
{
  "company_symbol": "INFY",
  "query": "How resilient are Infosys margins if discretionary IT spending slows?"
}
```
* **Success Response (200 OK):**
```json
{
  "summary": "In this example dataset, a 21.1% operating margin suggests a buffer. Utilization and deal mix help, but softer demand can pressure pricing.",
  "scenarios": {
    "bull": "Stronger deal wins; margins expand.",
    "base": "Stable demand; margins hold.",
    "bear": "Pricing pressure; margins contract."
  },
  "citations": [
    { "id": 1, "source": "Consolidated Q2 FY27 Financial Statement", "timestamp": "2026-10-16" }
  ],
  "disclaimer": "AI research is for educational analysis only, not investment advice."
}
```

---

## 3. Database Mapping Verification

| Endpoint | Mapped PRD Feature | Mapped Database Table | Validation Status |
| :--- | :--- | :--- | :--- |
| `POST /v1/auth/otp/*` | Screens S01–S02: Auth & OTP | `users` | **Strict Match** |
| `GET /v1/entitlements` | Free Launch Model | `workspaces` | **Strict Match** |
| `GET /v1/instruments/*` | Screens S04, S06: Search & Quotes | `instruments` | **Strict Match** |
| `GET /v1/market/candles` | Screen S07: Advanced Charts | `market_candles` | **Strict Match** |
| `GET /v1/market/option-chain` | Screen S11: F&O Trade Desk | `instruments`, `market_candles` | **Strict Match** |
| `POST /v1/paper/orders` | Screen S14: Paper Terminal | `paper_orders`, `paper_positions`, `ledger_entries` | **Strict Match** |
| `GET /v1/paper/accounts/me` | Screen S16: Paper Portfolio | `paper_accounts` | **Strict Match** |
| `GET/POST /v1/watchlists` | Screen S06: Watchlists | `watchlists`, `watchlist_items` | **Strict Match** |
| `POST /v1/strategies` | Screen S12: Strategy Builder | `strategies`, `strategy_versions` | **Strict Match** |
| `GET /v1/signals` | Screen S15: Signals Feed | `signals` | **Strict Match** |
