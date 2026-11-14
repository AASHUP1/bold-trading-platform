# MVP Feature Build Guide: BOLD Admin & Compliance Console
**Document:** FEATURES.md  
**Product:** BOLD Control Room (Admin Panel)  
**Target:** React 18+ (Vite, TypeScript, Tailwind CSS, Firebase Hosting)  
**Source of Truth:** [`admin-panel/PRD.md`](file:///d:/trading%20app/admin-panel/PRD.md), [`admin-panel/DESIGN.md`](file:///d:/trading%20app/admin-panel/DESIGN.md), [`admin-panel/ARCHITECTURE.md`](file:///d:/trading%20app/admin-panel/ARCHITECTURE.md), [`admin-panel/DATABASE.md`](file:///d:/trading%20app/admin-panel/DATABASE.md), and [`admin-panel/API.md`](file:///d:/trading%20app/admin-panel/API.md)  

---

## Build Execution Protocol
1. **One Feature at a Time:** Implement each feature in the exact order below.
2. **Update Checkboxes:** Upon completing and verifying a feature, check off its corresponding tasks in [`admin-panel/PLAN.md`](file:///d:/trading%20app/admin-panel/PLAN.md).
3. **Confirmation Rule:** Do not begin the next feature until the user reviews and confirms the current feature.

---

## Feature 1: Firebase Admin Authentication & Session Guard

### 1.1 Overview & User Story
* **What it does:** Allows authorized administrators (operations admins, compliance officers, super admins) to securely sign in using email and password with Firebase Authentication. Protects all admin routes behind token verification and session refresh handlers.
* **User Story Covered:** `US01` (*"As an admin, I want to authenticate securely with role verification, so that only authorized personnel can access platform controls."*)

### 1.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/login` (Admin Login Screen).
* **Components:**
  * Container: `#121923` Card (`radiusLarge`, `border: 1px solid #253140`).
  * Inputs: Email Input (`Inter` 14px, `#0B0F14` background, `#253140` border, `#38D996` focus ring) and Password Input with reveal toggle.
  * Button: Primary CTA (`#38D996` bg, `#0B0F14` text, 40px height, loading spinner).
  * Layout Guard: `ProtectedRoute` wrapper and `Header` user profile chip.

### 1.3 Database Tables & API Endpoints
* **Database Table:** `admin_users` (UUID, email, role, is_active, last_login_at, last_login_ip).
* **Endpoints Used:**
  * Firebase Client Auth SDK: `signInWithEmailAndPassword()`.
  * Backend Role Check: `GET /v1/admin/auth/me`.

### 1.4 Step-by-Step Build Order
1. Initialize Firebase client in `src/lib/firebase.ts` with environment variables.
2. Build `AuthProvider` context in `src/app/providers.tsx` handling auth state changes (`onAuthStateChanged`).
3. Construct `LoginForm.tsx` with Zod schema validation (`email` format, password length $\ge 8$).
4. Implement `ProtectedRoute.tsx` redirecting unauthenticated traffic to `/login`.
5. Attach Axios Bearer token request interceptor in `src/lib/api.ts`.

### 1.5 Edge Cases, States & Validation Rules
* **Validation:** Client-side Zod validation prevents submitting malformed emails.
* **Loading State:** Button disables, shows 16px SVG spinner, text changes to *"Authenticating..."*.
* **Error State:** Invalid credentials show a red alert banner (`#FF657A` border, *"Invalid email or password"*).
* **Edge Case (Suspended Admin):** If `admin_users.is_active = false`, backend returns `403 Forbidden`; UI signs out user and displays *"Account suspended. Contact super administrator."*

### 1.6 Acceptance Criteria Checklist
* [x] Unauthenticated users attempting to access `/overview` or `/keys` are redirected to `/login`.
* [x] Submitting valid credentials authenticates via Firebase and navigates to `/overview`.
* [x] Form fields display visible focus ring (`2px #38D996`) on keyboard tab.
* [x] Failed logins display inline error message without page reload.
* [x] JWT token is automatically attached to all outgoing API requests.

---

## Feature 2: Screen S21 Telemetry & Platform Overview

### 2.1 Overview & User Story
* **What it does:** Displays high-density operational health metrics for the entire BOLD platform: Total Users, Active Free Subscriptions, Feed Health across NSE/BSE adapters, API Error Rate (< 0.50% threshold), and a 60-point latency area chart (0–800ms).
* **User Story Covered:** `US04` (*"As an operations admin, I want to see real-time error rates and feed latency for NSE and BSE feeds on the dashboard, so that I can spot data degradation before users report it."*)

### 2.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/overview` (Screen `S21` - Platform Overview / Control Room).
* **Components:**
  * Top Bar: Environment badge (`SANDBOX / LIVE`), IST Clock (`tnum`), Admin role chip.
  * Metric Cards Grid: 4 cards with `#121923` surface, `#253140` borders, large `tnum` numbers.
  * Status Badges: `#38D996` (`HEALTHY`), `#F3C969` (`DEGRADED`), `#FF657A` (`OFFLINE`).
  * Chart Component: `FeedLatencyChart` (SVG/Canvas area chart with 500ms threshold line).
  * Incidents List: Active incident pills with acknowledged timestamps.

### 2.3 Database Tables & API Endpoints
* **Database Table:** `feed_health_logs` (adapter_id, latency_ms, error_rate, status, recorded_at).
* **Endpoints Used:**
  * `GET /v1/admin/telemetry/overview`
  * `GET /v1/admin/telemetry/latency-series?adapter_id=NSE_ADAPTER&minutes=60`

### 2.4 Step-by-Step Build Order
1. Define TypeScript interfaces in `src/types/telemetry.ts`.
2. Implement `useTelemetry` React Query hook with `refetchInterval: 15000` (15s auto-polling).
3. Build `MetricCard.tsx` component displaying title, tabular value, growth %, and status badge.
4. Build `FeedLatencyChart.tsx` plotting 60 points with tooltip inspection.
5. Assemble the S21 layout in `src/features/overview/OverviewScreen.tsx`.

### 2.5 Edge Cases, States & Validation Rules
* **Loading State:** Metric cards display pulse skeleton rectangles (`LoadingSkeleton.tsx`).
* **Degraded Feed State:** If any adapter latency > 500ms or error rate > 0.50%, the overall badge switches to yellow `#F3C969` (`DEGRADED`) with warning icon.
* **Empty State:** If no incidents exist, the incident panel displays a checkmark: *"All systems operational"*.
* **Network Disconnect:** If backend is unreachable, toast warning appears: *"Telemetry feed stale. Retrying..."*.

### 2.6 Acceptance Criteria Checklist
* [x] Dashboard loads in < 1.5 seconds and renders all 4 metric cards.
* [x] Latency chart renders 60 data points with smooth line interpolation.
* [x] Hovering over a chart node displays tooltip with exact time and latency in ms.
* [x] Telemetry polls every 15 seconds in the background without UI flicker.

---

## Feature 3: Universal Key & Secret Management Vault

### 3.1 Overview & User Story
* **What it does:** Centralized management hub to view, test, and rotate external third-party API credentials (Market Data feed keys, Gemini AI keys, Broker OAuth client secrets, FCM keys). All secrets are encrypted server-side with AES-256 and displayed with security masking.
* **User Story Covered:** `US01`, `US02`, `US03` (*"As an admin, I want to rotate API keys and test upstream connectivity from the dashboard, so that third-party integrations never fail without warning."*)

### 3.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/keys` (Universal Key & Secret Manager).
* **Components:**
  * Tab Bar: `Market Data Feeds`, `Broker OAuth`, `AI & Notifications`.
  * Key Table: Provider name, masked string (`sk-gemini-••••••••9A12`), latency badge, quota usage bar.
  * Action Buttons: `Test Ping` (Outline), `Rotate Key` (Primary `#38D996`), `Revoke` (Destructive `#FF657A`).
  * Modal: `RotateKeyDialog` (Backdrop blur, input field, upstream verify checkbox, confirm CTA).

### 3.3 Database Tables & API Endpoints
* **Database Table:** `api_keys_vault` (id, provider, masked_value, encrypted_payload, status, latency_ms).
* **Endpoints Used:**
  * `GET /v1/admin/keys`
  * `POST /v1/admin/keys/{provider}/test-ping`
  * `POST /v1/admin/keys/{provider}/rotate`

### 3.4 Step-by-Step Build Order
1. Build `KeyListTable.tsx` rendering rows with masked keys and copy buttons.
2. Implement `TestPingButton.tsx` firing test requests and updating latency badges.
3. Build `RotateKeyDialog.tsx` modal with Zod secret validation and confirmation step.
4. Hook optimistic mutations using TanStack Query `useMutation` in `useKeyManager.ts`.
5. Integrate clipboard copy with feedback toast (`"Masked key copied to clipboard"`).

### 3.5 Edge Cases, States & Validation Rules
* **Security Rule:** Plain-text secret is NEVER rendered or returned in GET responses.
* **Rotation Failure:** If upstream test ping fails during rotation, error modal displays upstream error message (e.g., `HTTP 400 API_KEY_INVALID`) and prevents saving.
* **Loading State:** Table displays skeleton rows; test ping shows inline spinning loader.
* **Rate-Limit State:** If provider quota > 80%, quota bar shifts from green to yellow `#F3C969`.

### 3.6 Acceptance Criteria Checklist
* [x] All stored keys are strictly masked (`••••••••abcd`) in the table.
* [x] Clicking "Test Ping" triggers network call and updates latency in < 1 second.
* [x] Submitting a new key in the rotation modal updates the masked string and creates an audit event.
* [x] Form disables during submission to prevent double-submit.

---

## Feature 4: Platform Emergency Kill Switch & Feature Flags

### 4.1 Overview & User Story
* **What it does:** Provides a prominent emergency toggle to freeze all signal generation and paper order simulation across the entire mobile platform within < 500ms. Also manages global feature flags including the `GLOBAL_FREE_TIER_OVERRIDE` ensuring 100% free user access.
* **User Story Covered:** `US05`, `US07` (*"As a compliance officer, I want a one-click Emergency Kill Switch that immediately locks live signal generation, so that users are protected during data feed anomalies."*)

### 4.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/governance` (Feature Flags & Emergency Controls).
* **Components:**
  * Master Alert Banner: Red border banner (`#FF657A`) with emergency switch.
  * Confirmation Dialog: `KillSwitchModal` requiring user to type `"CONFIRM FREEZE"`.
  * Feature Flags List: Clean toggle rows (`#38D996` ON, `#253140` OFF) with descriptions.

### 4.3 Database Tables & API Endpoints
* **Database Tables:** `feature_flags`, `audit_events`.
* **Endpoints Used:**
  * `GET /v1/admin/governance/flags`
  * `POST /v1/admin/governance/kill-switch`

### 4.4 Step-by-Step Build Order
1. Build `KillSwitchBanner.tsx` with high-visibility red glow (`shadow-glow-red`).
2. Implement `KillSwitchModal.tsx` with strict text match validation (`"CONFIRM FREEZE"`).
3. Build `FeatureFlagsList.tsx` rendering toggle switches for module permissions.
4. Bind mutation hook `useGovernance.ts` with instant optimistic state update.

### 4.5 Edge Cases, States & Validation Rules
* **Accidental Activation Guard:** The confirm button remains disabled until the operator types the exact string `"CONFIRM FREEZE"`.
* **Active Kill Switch State:** When active, the entire admin topbar displays a pulsing red indicator: `⚠️ KILL SWITCH ACTIVE - SIMULATIONS PAUSED`.
* **Rollback on Error:** If the Redis/database update fails, the UI rolls back switch state and alerts the operator.

### 4.6 Acceptance Criteria Checklist
* [x] Master kill switch cannot be toggled without typing the confirmation text.
* [x] Activating the kill switch sets `kill_switch_active = true` and updates mobile status in < 500ms.
* [x] Toggling feature flags saves immediately with optimistic UI updates.
* [x] Every toggle mutation generates a permanent entry in the audit trail.

---

## Feature 5: Strategy Review & Compliance Queue

### 5.1 Overview & User Story
* **What it does:** Allows compliance officers and research leads to review algorithmic strategies submitted by the quant team, inspect entry/exit conditions and backtest metrics, and approve them for broadcast to the mobile signals feed.
* **User Story Covered:** `US06` (*"As a compliance lead, I want to review strategy rules and backtest metrics, so that only validated setups are published to users."*)

### 5.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/strategies` (Strategy Review Queue).
* **Components:**
  * Filter Chips: Risk category (`All`, `Low`, `Medium`, `High`), Status (`Pending`, `Approved`).
  * Review Table: Strategy name, author, win rate %, profit factor, max drawdown %.
  * Side Drawer: `StrategyDetailDrawer` displaying entry/exit AST logic and modelled cost assumptions.
  * Decision Bar: Approve button (`#38D996`), Request Changes button (`#F3C969`), Reject button (`#FF657A`).

### 5.3 Database Tables & API Endpoints
* **Database Table:** `strategy_reviews` (id, strategy_version_id, decision, risk_category, backtest_win_rate).
* **Endpoints Used:**
  * `GET /v1/admin/strategies/queue`
  * `POST /v1/admin/strategies/queue/{id}/decision`

### 5.4 Step-by-Step Build Order
1. Build `StrategyQueueTable.tsx` with risk badges and tabular metrics.
2. Implement `StrategyDetailDrawer.tsx` rendering conditions tree and backtest chart.
3. Build review decision form prompting for optional reviewer notes.
4. Connect `useStrategyReview` hook with mutation to update review queue table.

### 5.5 Edge Cases, States & Validation Rules
* **Validation:** Rejection or change requests require at least 10 characters of `review_notes`.
* **Empty State:** If no strategies are pending, table displays: *"Review queue clear. All submitted strategies processed."*
* **Concurrency Guard:** If another admin approved the strategy while drawer is open, submit returns `409 Conflict` and refreshes view.

### 5.6 Acceptance Criteria Checklist
* [x] Review queue correctly filters by risk category (`Low`, `Medium`, `High`).
* [x] Clicking a strategy row opens side drawer with complete rule parameters.
* [x] Approving a strategy changes status to `Approved` and updates the table row immediately.
* [x] Review decision records reviewer ID and timestamp.

---

## Feature 6: Tamper-Evident Audit Trail Viewer

### 6.1 Overview & User Story
* **What it does:** Searchable, paginated audit trail displaying cryptographic SHA-256 event hashes, actor emails, IP addresses, and state diffs for all administrative actions.
* **User Story Covered:** `US02`, `US05` (*"As an auditor, I want a tamper-evident log of all secret rotations and kill switch events, so that system governance is transparent."*)

### 6.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `/audit` (Audit Trail).
* **Components:**
  * Search Bar: Filter by actor email, IP address, or event hash.
  * Date Range Filter: Filter logs by date range.
  * Audit Table: Timestamp (IST), actor, action category, target resource, SHA-256 hash.
  * Row Expansion: Collapsible JSON viewer showing `state_before` and `state_after`.

### 6.3 Database Tables & API Endpoints
* **Database Table:** `audit_events` (id, event_hash, actor_email, action_category, state_before, state_after).
* **Endpoints Used:** `GET /v1/admin/audit/events`

### 6.4 Step-by-Step Build Order
1. Build `AuditLogTable.tsx` with monospace font for hashes (`JetBrains Mono`).
2. Add expandable row mechanism to render JSON state diffs.
3. Implement debounced search input (300ms) for actor email and IP address.
4. Add pagination controls (`Previous`, `Next`, page indicators).

### 6.5 Edge Cases, States & Validation Rules
* **Empty State:** If query yields no matches, displays *"No audit records found matching criteria"*.
* **Append-Only Assurance:** UI does not include delete or edit buttons for any audit record.
* **High-Volume Pagination:** Table paginates strictly with `limit=50` to maintain fast rendering.

### 6.6 Acceptance Criteria Checklist
* [x] Displays audit events with exact IST timestamps and actor roles.
* [x] Searching by email or event hash filters results in real time.
* [x] Expanding a row displays formatted JSON state diffs cleanly.
* [x] Table pagination functions forwards and backwards without jumping.
