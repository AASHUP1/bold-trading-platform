# MVP Feature Build Guide: BOLD Mobile Application
**Document:** FEATURES.md  
**Product:** BOLD Android Trading & Research Platform  
**Target:** Native Android (Kotlin, Jetpack Compose, Android 10+ / API 29+)  
**Source of Truth:** [`mobile-application/PRD.md`](file:///d:/trading%20app/mobile-application/PRD.md), [`mobile-application/DESIGN.md`](file:///d:/trading%20app/mobile-application/DESIGN.md), [`mobile-application/ARCHITECTURE.md`](file:///d:/trading%20app/mobile-application/ARCHITECTURE.md), [`mobile-application/DATABASE.md`](file:///d:/trading%20app/mobile-application/DATABASE.md), and [`mobile-application/API.md`](file:///d:/trading%20app/mobile-application/API.md)  

---

## Build Execution Protocol
1. **One Feature at a Time:** Implement each feature in the exact sequence below.
2. **Update Checkboxes:** Upon completing and verifying a feature, check off its corresponding tasks in [`mobile-application/PLAN.md`](file:///d:/trading%20app/mobile-application/PLAN.md).
3. **Confirmation Rule:** Do not begin the next feature until the user reviews and confirms the current feature.

---

## Feature 1: Onboarding, Authentication & Workspace Setup (S01–S03)

### 1.1 Overview & User Story
* **What it does:** Welcomes the trader with an institutional onboarding carousel, handles passwordless phone OTP authentication with 2-minute countdown timer, and personalizes the trading workspace (starting ₹10,00,000 virtual capital, exchange selection, and 1% risk per trade budget).
* **User Story Covered:** `US01`, `US02` (*"As a user, I want to sign up via mobile OTP and configure my ₹10,00,000 virtual capital workspace, so that I can practice trading tailored to my risk profile."*)

### 1.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screens:**
  * `S01`: Splash and Onboarding Carousel
  * `S02`: Login and Account Security (Phone + 6-Digit OTP)
  * `S03`: Workspace Personalization & Risk Preferences
* **Components:**
  * Carousel slides with indicator dots and regulatory disclosure footer.
  * Phone Number Input with `+91` prefix and 6-box numeric OTP entry.
  * Density selector cards (`Beginner`, `Intermediate`, `Advanced`).
  * Risk selection radio pills (`Up to 1%`, `1-2%`, `Over 2%`).
  * Primary Button (`#38D996` background, `#0B0F14` text, 48dp height).

### 1.3 Database Tables & API Endpoints
* **Database Tables:** `users`, `workspaces`, `paper_accounts` (initial deposit of ₹10,00,000).
* **Endpoints Used:**
  * `POST /v1/auth/otp/request`
  * `POST /v1/auth/otp/verify`
  * `PATCH /v1/me/preferences`

### 1.4 Step-by-Step Build Order
1. Build `OnboardingScreen.kt` with `HorizontalPager` rendering 3 feature slides.
2. Build `AuthScreen.kt` with phone input, OTP text fields, and countdown timer.
3. Build `PersonalizationScreen.kt` with exchange chips and virtual capital slider.
4. Implement `AuthViewModel.kt` handling OTP dispatches and saving tokens to Jetpack DataStore.
5. Setup `BoldNavHost.kt` navigation graph linking `S01` $\rightarrow$ `S02` $\rightarrow$ `S03` $\rightarrow$ `S04`.

### 1.5 Edge Cases, States & Validation Rules
* **Validation:** Phone number must be exactly 10 digits; OTP must be exactly 6 digits.
* **Loading State:** Button disables and renders circular loader (`strokeWidth = 2.dp`).
* **Error State:** Invalid OTP triggers red input border (`#FF657A`) and shakes input boxes.
* **Edge Case (OTP Expiry):** When timer reaches 00:00, "Resend OTP" button activates.

### 1.6 Acceptance Criteria Checklist
* [ ] S01 onboarding carousel swipes cleanly with 60 FPS animation.
* [ ] Requesting OTP disables button and counts down from 02:00.
* [ ] Submitting valid OTP saves JWT to DataStore and advances to S03.
* [ ] Completing S03 initializes ₹10,00,000 virtual balance and lands on S04 Dashboard.

---

## Feature 2: Home Dashboard & Real-Time Watchlist (S04, S06)

### 2.1 Overview & User Story
* **What it does:** Displays the core market pulse (NIFTY 50, SENSEX, BANK NIFTY), the user's paper portfolio equity card (`₹10,42,680`), quick action pills, and custom watchlists with intraday sparklines and real-time streaming quotes.
* **User Story Covered:** `US03`, `US04` (*"As a trader, I want to view major indices and track my watchlist with real-time price updates, so that I can monitor market moves instantly."*)

### 2.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screens:**
  * `S04`: Home Dashboard
  * `S06`: Watchlist & Instrument Details
* **Components:**
  * Top Bar: Greeting, search icon, data status watermark (`● ILLUSTRATIVE / Live quote not connected`).
  * Index Cards: NIFTY/SENSEX with percentage change badges (`#38D996` bull / `#FF657A` bear).
  * Paper Portfolio Card: Big virtual equity metric, daily P&L badge, "Explore portfolio" CTA.
  * Watchlist Row Card: Instrument symbol, sector label, intraday sparkline, tabular price text (`tnum`).

### 2.3 Database Tables & API Endpoints
* **Database Tables:** `instruments`, `watchlists`, `watchlist_items`, `market_candles`.
* **Client Database (Room):** `cached_instruments`, `cached_watchlist_items`.
* **Endpoints Used:**
  * `GET /v1/market/indices`
  * `GET /v1/watchlists`
  * `WS /v1/stream/market` (WebSocket tick stream)

### 2.4 Step-by-Step Build Order
1. Build `DashboardScreen.kt` with index carousel and portfolio overview card.
2. Build `WatchlistScreen.kt` with multi-watchlist tab bar and `LazyColumn`.
3. Build `SparklineCanvas.kt` rendering mini trend line in green/red.
4. Implement `WebSocketManager.kt` streaming real-time prices into a Kotlin Coroutines `SharedFlow`.
5. Connect `WatchlistViewModel.kt` with `@Stable` recomposition optimization.

### 2.5 Edge Cases, States & Validation Rules
* **Recomposition Optimization:** Price text uses isolated Composable with `@Stable` annotation to prevent whole-screen redraws.
* **Loading State:** Watchlist displays 6 shimmer skeleton rows.
* **Empty State:** If watchlist is empty, displays *"No instruments in this watchlist. Tap + to add."*
* **Offline / Disconnect:** If WebSocket drops, persistent watermark shifts to yellow `#F3C969` (`● DISCONNECTED - SHOWING CACHED QUOTES`).

### 2.6 Acceptance Criteria Checklist
* [ ] Home Dashboard renders NIFTY 50 and SENSEX with accurate daily change colors.
* [ ] Watchlist scrolls smoothly with zero frame drops across 50 items.
* [ ] Quotes update in real time via WebSocket without UI layout jittering (`tnum`).
* [ ] Tapping any instrument navigates to its detail sheet or chart.

---

## Feature 3: Interactive Candlestick Charting & Indicators (S07)

### 3.1 Overview & User Story
* **What it does:** Full-screen hardware-accelerated candlestick chart for any NSE/BSE stock or index. Supports intervals from 1m to 1D, overlays EMA 20/50 and VWAP, renders an RSI sub-chart, and features an interactive crosshair and direct "Simulate Trade" CTA.
* **User Story Covered:** `US03` (*"As a technical trader, I want to inspect 15-minute candlestick charts with EMA and VWAP overlays, so that I can identify high-conviction trade setups."*)

### 3.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `S07` (Advanced Chart and Indicators).
* **Components:**
  * Chart Header: Instrument name, interval chips (`1m`, `5m`, `15m`, `1H`, `1D`), data watermark.
  * Candlestick Canvas: Custom hardware-accelerated Compose `Canvas` drawing OHLC bars.
  * Overlays: Line paths for EMA 20, EMA 50, and VWAP.
  * RSI Sub-Chart: 14-period RSI line with 30–70 boundary shading.
  * Touch Crosshair: Draggable vertical and horizontal lines with price and timestamp labels.
  * Action Bar: "Save chart layout" and "Simulate trade" buttons.

### 3.3 Database Tables & API Endpoints
* **Database Tables:** `instruments`, `market_candles` (partitioned OHLCV).
* **Endpoints Used:** `GET /v1/market/candles?instrument_id={id}&timeframe={tf}&limit=100`

### 3.4 Step-by-Step Build Order
1. Build `CandlestickCanvas.kt` calculating scale factors and drawing OHLC bars.
2. Add gesture detectors for horizontal panning and pinch-to-zoom scaling.
3. Build `IndicatorRenderers.kt` computing mathematical EMA, VWAP, and RSI points.
4. Implement touch crosshair inspection overlay with exact candle metric pill.
5. Attach bottom "Simulate Trade" CTA opening `OrderTicketBottomSheet` (`S14`).

### 3.5 Edge Cases, States & Validation Rules
* **Performance Rule:** Canvas rendering must achieve 60 FPS during zoom and pan.
* **Gaps in Data:** Weekend and holiday gaps are collapsed without rendering blank vertical voids.
* **Loading State:** Chart canvas renders animated skeleton grid lines during initial candle fetch.
* **Error State:** If historical candles fail to load, displays *"Unable to load candles. Tap to retry."*

### 3.6 Acceptance Criteria Checklist
* [ ] Candlestick chart renders 100+ candles with clear green (`#38D996`) and red (`#FF657A`) bars.
* [ ] Toggling interval chips (`1m`, `5m`, `15m`) fetches and redraws candles in < 1 second.
* [ ] EMA and VWAP overlay lines align accurately with candle bodies.
* [ ] Tapping "Simulate Trade" opens the Paper Trading ticket with the instrument pre-selected.

---

## Feature 4: Paper Trading Terminal & Risk Engine (S14)

### 4.1 Overview & User Story
* **What it does:** Allows users to place simulated market and limit orders with Stop-Loss and Target levels against a ₹10,00,000 virtual balance. Includes statutory fee calculations and a pre-trade risk engine enforcing per-trade and daily loss limits.
* **User Story Covered:** `US05`, `US06` (*"As an active trader, I want to execute simulated market orders with attached stop-loss and target, so that I can practice disciplined execution without financial risk."*)

### 4.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `S14` (Paper Trading Terminal).
* **Components:**
  * Terminal Header: Available Virtual Cash (`₹8,00,000`), Margin Used (`₹1,20,000`).
  * Order Ticket: Buy / Sell toggle, Quantity Stepper (48dp buttons), Order Type dropdown.
  * Price Inputs: Stop Loss and Target price steppers with tick size alignment.
  * Fee Breakdown: Order value, estimated simulated fees (₹5.90), total virtual debit.
  * Preview Bottom Sheet: Modal sheet displaying order summary and "SIMULATION ONLY" alert banner.
  * Order Filled Dialog: Receipt modal showing fill price, timestamp, and updated cash balance.

### 4.3 Database Tables & API Endpoints
* **Database Tables:** `paper_accounts`, `paper_orders`, `paper_positions`, `ledger_entries`.
* **Endpoints Used:**
  * `GET /v1/paper/accounts/me`
  * `POST /v1/paper/orders` (requires `X-Idempotency-Key`)

### 4.4 Step-by-Step Build Order
1. Build `OrderTicketBottomSheet.kt` with quantity and stop-loss steppers.
2. Implement fee calculation logic simulating exchange turnover, GST, and SEBI charges.
3. Build `RiskEngineValidator.kt` checking 1% per-trade risk budget and 2% daily loss threshold.
4. Construct `OrderPreviewSheet.kt` modal and `OrderFilledDialog.kt` receipt.
5. Wire `PaperTerminalViewModel.kt` with state flow and optimistic ledger updates.

### 4.5 Edge Cases, States & Validation Rules
* **Physical Isolation Rule:** Simulated orders must NEVER contact real broker execution APIs.
* **Risk Breach:** If order risk > 2% daily loss cap, button disables with error label: *"Order exceeds daily 2% risk limit"*.
* **Insufficient Funds:** If order debit > available virtual cash, order is rejected with clear error toast.
* **Idempotency:** Every order submission generates a UUID `X-Idempotency-Key` to prevent duplicate orders.

### 4.6 Acceptance Criteria Checklist
* [ ] Order ticket dynamically calculates order value and estimated statutory fees.
* [ ] Pre-trade risk engine rejects orders that exceed configured risk limits.
* [ ] Confirming simulated buy executes order, updates virtual cash balance, and shows fill receipt.
* [ ] Filled order immediately reflects as an open position in the Portfolio tab.

---

## Feature 5: Virtual Portfolio & Trade Journal (S16)

### 5.1 Overview & User Story
* **What it does:** Displays active simulated positions, unrealized/realized P&L, strategy attribution, risk utilization bars, trade journal with 1–5 star process ratings, and an account reset button.
* **User Story Covered:** `US07`, `US08` (*"As a user, I want to review my virtual portfolio and log trade notes, so that I can evaluate my trading discipline."*)

### 5.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `S16` (Paper Portfolio and Trade Journal).
* **Components:**
  * Header Metric: Virtual Equity (`₹10,42,680`), Today's P&L badge (`+₹8,420 (+0.81%)`).
  * Tab Selector: `Overview`, `Positions`, `Journal`.
  * Position Card: Symbol, quantity, avg price, LTP, unrealized P&L in green/red.
  * Trade Journal Row: Closed trade P&L, process star rating (1–5), notes, and tags (`#VWAP`).
  * Account Reset Button: Red outlined button launching `ResetAccountDialog`.

### 5.3 Database Tables & API Endpoints
* **Database Tables:** `paper_accounts`, `paper_positions`, `ledger_entries`.
* **Client Database (Room):** `cached_paper_positions`, `offline_trade_journal`.
* **Endpoints Used:**
  * `GET /v1/paper/accounts/me`
  * `POST /v1/paper/accounts/reset`

### 5.4 Step-by-Step Build Order
1. Build `PortfolioScreen.kt` with equity card and risk utilization progress bar.
2. Build `PositionsList.kt` rendering open simulated positions with live P&L.
3. Build `TradeJournalTab.kt` allowing users to rate trade discipline and save notes.
4. Implement `ResetAccountDialog.kt` requiring confirmation to reset virtual cash to ₹10,00,000.
5. Connect `PortfolioViewModel.kt` observing Room cache and WebSocket price changes.

### 5.5 Edge Cases, States & Validation Rules
* **Empty Positions State:** When no positions are open, displays *"No open positions. Use the terminal to simulate your next trade."*
* **Decimal Safety:** All calculations must use `BigDecimal` to ensure zero rounding discrepancies.
* **Reset Account Confirmation:** Reset clears open positions and pending orders, creates a reset ledger entry, and restores cash to ₹10,00,000.

### 5.6 Acceptance Criteria Checklist
* [ ] Portfolio equity updates in real time as market prices change.
* [ ] Tapping a position row allows closing the position at the simulated market price.
* [ ] Trade journal saves process star ratings and discipline notes to local Room DB.
* [ ] Account reset restores virtual cash to ₹10,00,000 and archives previous history.

---

## Feature 6: F&O Trade Desk & Option Chain (S11)

### 6.1 Overview & User Story
* **What it does:** Complete derivatives analytics desk for NIFTY and BANK NIFTY options. Features live strike tables, Greeks ($\Delta$, $\Gamma$, $\Theta$, $\nu$), PCR, strike OI visualization, and an interactive expiry payoff diagram.
* **User Story Covered:** `US09` (*"As an F&O trader, I want to explore option chains with live Greeks and payoff diagrams, so that I can structure options trades safely."*)

### 6.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screen:** `S11` (F&O Trade Desk).
* **Components:**
  * Top Bar: Underlying index selector (`NIFTY 50`), Expiry Date dropdown, PCR badge (`1.12`).
  * Greeks Overview Pill: Delta 0.52, Gamma 0.0018, Theta -8.4, Vega 12.6.
  * Option Chain Table: Tabular columns (`Calls OI` | `Calls LTP` | `STRIKE` | `Puts LTP` | `Puts OI`).
  * ATM Strike Highlight: Distinct row styling (`#1C2734` background, subtle `#F3C969` border).
  * Payoff Diagram Canvas: Interactive profit/loss curve with Breakeven and Max Risk markers.

### 6.3 Database Tables & API Endpoints
* **Database Tables:** `instruments` (option contracts), `market_candles`.
* **Endpoints Used:** `GET /v1/market/option-chain?underlying=NIFTY&expiry={date}`

### 6.4 Step-by-Step Build Order
1. Build `OptionChainScreen.kt` with underlying index chips and expiry date selector.
2. Build `OptionChainTable.kt` with sticky strike center column and horizontal scroll.
3. Build `GreeksDetailCard.kt` expanding upon strike tap.
4. Implement `PayoffDiagramCanvas.kt` rendering long/short payoff curves.
5. Wire `OptionChainViewModel.kt` fetching strikes and calculating PCR.

### 6.5 Edge Cases, States & Validation Rules
* **ATM Identification:** ATM strike dynamically recalibrates as the underlying spot price moves.
* **Loading State:** Strike table displays skeleton placeholders while option chain compiles.
* **Zero Volume Strikes:** Deep OTM strikes with zero OI or bid/ask spread display dashed values (`--`).

### 6.6 Acceptance Criteria Checklist
* [ ] Option chain loads strikes for selected index and expiry cleanly.
* [ ] ATM strike is highlighted with distinct visual styling.
* [ ] Tapping any strike expands detailed Greeks and updates the payoff diagram.
* [ ] Tapping "Simulate Option Trade" handoffs contract to the Paper Terminal ticket.

---

## Feature 7: Market Scanner & Signals Feed (S08, S15)

### 7.1 Overview & User Story
* **What it does:** Scans the NIFTY 500 universe for momentum setups using pre-built filters (Breakout, VWAP Reclaim, Volume Spike), and displays verified algorithmic signals with defined entry zones, stops, and target levels.
* **User Story Covered:** `US10` (*"As a swing trader, I want to filter stocks by VWAP and volume criteria and receive structured signals, so that I can act on systematic setups without manual chart scanning."*)

### 7.2 Screens & Components Involved (`DESIGN.md` Strict)
* **Screens:**
  * `S08`: Advanced Market Scanner
  * `S15`: Signals Feed
* **Components:**
  * Preset Scan Chips: `Breakout`, `Breakdown`, `Momentum`, `Volume Spike`, `VWAP Reclaim`.
  * Scanner Results List: Instrument cards with matched criteria checklist (`✓ Price > EMA 20`, `✓ Volume 1.8x`).
  * Signal Card: Direction (`BUY`), Entry Zone (`₹2,932 - ₹2,950`), Stop Loss (`₹2,910`), Targets (`2,970 / 2,995`), Freshness indicator.
  * Direct CTA: "Simulate trade" button on each signal card.

### 7.3 Database Tables & API Endpoints
* **Database Tables:** `instruments`, `scanner_definitions`, `signals`.
* **Endpoints Used:**
  * `GET /v1/scanners`
  * `POST /v1/scanners/{id}/run`
  * `GET /v1/signals?status=ACTIVE`

### 7.4 Step-by-Step Build Order
1. Build `ScannerScreen.kt` with preset filter chips and custom condition cards.
2. Build `ScannerResultRow.kt` displaying criteria matched with green checkmarks.
3. Build `SignalsScreen.kt` with filter tabs (`New`, `Active`, `Invalidated`, `Expired`).
4. Build `SignalCard.kt` displaying defined entry zones and stop levels.
5. Wire "Simulate trade" button to pre-populate `OrderTicketBottomSheet` with signal stop loss.

### 7.5 Edge Cases, States & Validation Rules
* **Signal Invalidation:** If market price drops below stop loss before entry, signal state switches to `INVALIDATED` with red chip.
* **Empty State:** If scanner yields zero matches, displays *"No instruments currently meet this criteria. Try another preset."*
* **Freshness Timer:** Signals display countdown timer indicating when the setup expires.

### 7.6 Acceptance Criteria Checklist
* [ ] Selecting a scanner preset executes scan and returns matched instruments with criteria checkmarks.
* [ ] Signals feed lists active signals with clear entry, stop, and target boundaries.
* [ ] Tapping "Simulate trade" on a signal card launches the paper trading ticket with pre-filled risk stops.
* [ ] Expired or invalidated signals are clearly tagged and prevented from direct simulation.
