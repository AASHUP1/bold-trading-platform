# Master MVP Feature Build Guide: BOLD Monorepo Platform
**Document:** FEATURES.md  
**Product:** BOLD Trading & Research Platform (Monorepo)  
**Parent Specifications:** `PRD.md`, `PLAN.md`, `DESIGN.md`, `ARCHITECTURE.md`, `DATABASE.md`, `API.md`  

---

## Strict Execution Protocol
1. **Build One Feature at a Time:** Features must be built sequentially.
2. **Update Implementation Checkboxes:** After completing and verifying a feature, update its corresponding tasks in [`PLAN.md`](file:///d:/trading%20app/PLAN.md), [`admin-panel/PLAN.md`](file:///d:/trading%20app/admin-panel/PLAN.md), or [`mobile-application/PLAN.md`](file:///d:/trading%20app/mobile-application/PLAN.md).
3. **User Confirmation Gate:** **Never begin the next feature until the user reviews, tests, and confirms the current feature.**

---

## Subproject Feature Guides

* **[`admin-panel/FEATURES.md`](file:///d:/trading%20app/admin-panel/FEATURES.md):** Detailed build specifications for the 6 Admin MVP features (Firebase Admin Auth, S21 Telemetry, Universal Key Vault, Emergency Kill Switch, Strategy Review Queue, Audit Trail).
* **[`mobile-application/FEATURES.md`](file:///d:/trading%20app/mobile-application/FEATURES.md):** Detailed build specifications for the 7 Mobile MVP features (Onboarding S01–S03, Dashboard & Watchlists S04/S06, Candlestick Charting S07, Paper Terminal S14, Portfolio & Journal S16, F&O Option Chain S11, Market Scanner & Signals S08/S15).

---

## Master MVP Feature Matrix

| # | Feature Name | Subproject | Primary Screens | Database Tables | Endpoints Used | User Story |
| :- | :--- | :--- | :--- | :--- | :--- | :--- |
| **A1** | **Admin Auth & Session Guard** | `admin-panel` | `/login` | `admin_users` | Firebase SDK, `/v1/admin/auth/me` | US01 |
| **A2** | **Screen S21 Telemetry Overview** | `admin-panel` | `/overview` (S21) | `feed_health_logs` | `GET /v1/admin/telemetry/*` | US04 |
| **A3** | **Universal Key & Secret Vault** | `admin-panel` | `/keys` | `api_keys_vault` | `GET/POST /v1/admin/keys/*` | US01, US03 |
| **A4** | **Emergency Kill Switch & Flags** | `admin-panel` | `/governance` | `feature_flags`, `audit_events` | `POST /v1/admin/governance/kill-switch` | US05, US07 |
| **A5** | **Strategy Review Queue** | `admin-panel` | `/strategies` | `strategy_reviews` | `GET/POST /v1/admin/strategies/*` | US06 |
| **A6** | **Tamper-Evident Audit Trail** | `admin-panel` | `/audit` | `audit_events` | `GET /v1/admin/audit/events` | US02 |
| **M1** | **Onboarding & Mobile OTP Auth** | `mobile-application` | `S01`, `S02`, `S03` | `users`, `workspaces` | `POST /v1/auth/otp/*` | US01, US02 |
| **M2** | **Home Dashboard & Watchlist** | `mobile-application` | `S04`, `S06` | `instruments`, `watchlists` | `GET /v1/watchlists`, `WS /v1/stream` | US03, US04 |
| **M3** | **Interactive Candlestick Chart** | `mobile-application` | `S07` | `market_candles` | `GET /v1/market/candles` | US03 |
| **M4** | **Paper Terminal & Risk Engine** | `mobile-application` | `S14` | `paper_accounts`, `paper_orders` | `POST /v1/paper/orders` | US05, US06 |
| **M5** | **Portfolio & Trade Journal** | `mobile-application` | `S16` | `paper_positions`, `ledger_entries` | `GET /v1/paper/accounts/me` | US07, US08 |
| **M6** | **F&O Desk & Option Chain** | `mobile-application` | `S11` | `instruments`, `market_candles` | `GET /v1/market/option-chain` | US09 |
| **M7** | **Scanner & Signals Feed** | `mobile-application` | `S08`, `S15` | `scanner_definitions`, `signals` | `GET /v1/signals` | US10 |

---

## Definition of Done for Any Completed Feature

Before marking any feature as completed and asking for confirmation:
1. **Code Completeness:** Clean, tested, single-responsibility functions following `ARCHITECTURE.md`.
2. **Visual Fidelity:** Strictly matches tokens, colors, typography, and states in `DESIGN.md`.
3. **Error & State Coverage:** Handles loading skeletons, error states, and empty states.
4. **Zero Live Order Execution:** Paper trading remains strictly isolated from real broker endpoints.
5. **Decimal Safety:** All currency and P&L calculations use `BigDecimal` / `NUMERIC(15, 2)`.
6. **Task Checkbox:** Corresponding items checked in `PLAN.md`.
