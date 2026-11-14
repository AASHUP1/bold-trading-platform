# UI/UX Design System Specification: BOLD Mobile Application
**Document:** DESIGN.md  
**Product:** BOLD Android Trading & Research Platform  
**Target Platform:** Native Android (Kotlin, Jetpack Compose, Material 3 Foundation)  
**Source of Truth:** [`mobile-application/PRD.md`](file:///d:/trading%20app/mobile-application/PRD.md) and BOLD UI/UX Design Brief v1.0 (Screens S01–S20)  
**Strict Implementation Rule:** Every Compose component, theme file, screen layout, and color resource built in this application must follow this document strictly. Do not invent new colors, fonts, or arbitrary padding.

---

## 1. Brand Feel & 3 Design Principles

### Brand Feel
**"A Sharper Edge. A Considered Risk."**  
BOLD is an institutional financial terminal adapted for high-speed mobile decision-making. It rejects the gamified, candy-colored, casino-style aesthetics of modern retail broker apps. Its visual signature is sleek, dense, high-contrast, and disciplined—reminiscent of a Bloomberg or Reuters terminal refined for modern Android touchscreens.

### 3 Design Principles
1. **Clarity at Market Speed:** Crucial market data (LTP, % change, volume, stop levels) must be absorbable at a sub-second glance. High-contrast typography, fixed tabular numerals (`tnum`), and zero distracting bounce animations during market hours.
2. **Defensive Risk by Design:** The interface actively reinforces discipline. Order tickets prominently display potential loss before gains; risk budgets (0.5%–2%) are surfaced visually; and virtual paper trades are unequivocally stamped with watermarks (*"ILLUSTRATIVE / SIMULATION ONLY"*).
3. **Ergonomic Density:** Built for thumb reachability using an 8dp grid, 48dp minimum touch targets, and persistent bottom navigation combined with draggable bottom sheets for order execution and filters.

---

## 2. Color Palette (Strict Tokens)

All UI elements must utilize these exact hex codes.

### 2.1 Dark Theme (Default & Primary Theme)
| Token Name | HEX Code | Compose Resource | Usage |
| :--- | :--- | :--- | :--- |
| **App Background** | `#0B0F14` | `BoldTheme.colors.background` | App scaffold canvas, list backings |
| **Surface (Card)** | `#121923` | `BoldTheme.colors.surface` | Instrument cards, summary panels |
| **Elevated Surface** | `#1C2734` | `BoldTheme.colors.surfaceElevated`| Selected cards, bottom sheets, active chips |
| **Border / Divider** | `#253140` | `BoldTheme.colors.border` | Card outlines, tab dividers, chart grid lines |
| **Primary / Bullish** | `#38D996` | `BoldTheme.colors.bullish` | Positive P&L, green candles, primary CTAs |
| **Bearish / Loss** | `#FF657A` | `BoldTheme.colors.bearish` | Negative P&L, red candles, stop-loss markers |
| **Warning / Caution** | `#F3C969` | `BoldTheme.colors.warning` | Degraded data banner, risk alerts, ATM strikes |
| **Primary Text** | `#EAF0F6` | `BoldTheme.colors.textPrimary` | High-contrast prices, headings, values |
| **Secondary Text** | `#92A0B2` | `BoldTheme.colors.textSecondary` | Subtitles, indicators, volume labels, timestamps |
| **Muted Text** | `#5A687A` | `BoldTheme.colors.textMuted` | Disabled items, search placeholders |

### 2.2 Light Theme (Clean Institutional Theme)
| Token Name | HEX Code | Compose Resource | Usage |
| :--- | :--- | :--- | :--- |
| **App Background** | `#F8FAFC` | `BoldTheme.colors.background` | Slate-50 canvas |
| **Surface (Card)** | `#FFFFFF` | `BoldTheme.colors.surface` | Crisp white cards |
| **Elevated Surface** | `#F1F5F9` | `BoldTheme.colors.surfaceElevated`| Slate-100 raised sheets |
| **Border / Divider** | `#E2E8F0` | `BoldTheme.colors.border` | Slate-200 structural borders |
| **Primary / Bullish** | `#059669` | `BoldTheme.colors.bullish` | Emerald-600 positive price |
| **Bearish / Loss** | `#DC2626` | `BoldTheme.colors.bearish` | Red-600 negative price |
| **Warning / Caution** | `#D97706` | `BoldTheme.colors.warning` | Amber-600 caution alerts |
| **Primary Text** | `#0F172A` | `BoldTheme.colors.textPrimary` | Slate-900 primary text |
| **Secondary Text** | `#475569` | `BoldTheme.colors.textSecondary` | Slate-600 labels |
| **Muted Text** | `#94A3B8` | `BoldTheme.colors.textMuted` | Slate-400 placeholder |

---

## 3. Typography Scale & Weights

* **Primary Font Family:** `Inter` (Fallback: `Roboto`).
* **Tabular Numbers Requirement:** Every price, percentage, quantity, strike, and P&L figure must have `FontFeatureSettings("tnum")` applied in Jetpack Compose to prevent layout jittering as quotes update.

| Compose Style | Size (sp) | Line Height (sp) | Weight | Compose FontStyle |
| :--- | :--- | :--- | :--- | :--- |
| `displayLarge` | 32.sp | 40.sp | Bold (700) | Main onboarding headlines |
| `titleLarge` | 22.sp | 28.sp | SemiBold (600) | Top app bar titles, index values |
| `titleMedium` | 18.sp | 24.sp | SemiBold (600) | Card headings, instrument symbols |
| `bodyLarge` | 15.sp | 22.sp | Normal (400) | Standard copy, research briefs |
| `bodyMedium` | 14.sp | 20.sp | Medium (500) | Watchlist prices, button labels |
| `labelLarge` | 13.sp | 18.sp | SemiBold (600) | Filter chips, table column headers |
| `labelSmall` | 11.sp | 14.sp | Medium (500) | Micro tags (NSE/BSE), timestamps |
| `tabularPrice` | 16.sp | 20.sp | SemiBold (600) | Tabular numbers for LTP & P&L |

---

## 4. Spacing Scale, Radius, Touch Targets & Breakpoints

### 4.1 Spacing Scale (8dp Base)
* `2.dp`: Micro indicator padding, candle borders
* `4.dp`: Tag padding, icon-to-label separation
* `8.dp`: Base padding between compact chips
* `12.dp`: Inner card padding, list item vertical gap
* `16.dp`: Screen horizontal gutters, bottom sheet content padding
* `20.dp`: Section dividers, dashboard card margins
* `24.dp`: Modal inner padding
* `32.dp`: Onboarding carousel spacing

### 4.2 Border Radius
* `radiusSmall` (`6.dp`): Status chips, exchange pill tags (e.g., `NSE`, `BSE`)
* `radiusMedium` (`10.dp`): Buttons, search inputs, quantity steppers
* `radiusLarge` (`14.dp`): Stock cards, strategy builder condition boxes
* `radiusSheet` (`20.dp` top radius): Bottom sheets (Order preview, filters)
* `radiusFull` (`9999.dp`): Navigation bar indicator pills, circular avatar

### 4.3 Touch Targets & Ergonomics
* **Minimum Interactive Size:** `48.dp x 48.dp` for every tappable element (buttons, watchlist rows, chart tool toggles, chips).
* **Card Elevation / Borders:** In Dark Mode, elevation is depicted by border contrast (`1.dp solid #253140`) rather than drop shadows.

---

## 5. Component Specifications (All States)

### 5.1 Buttons
* **Primary (Buy / Bullish / Confirm):** Background `#38D996`, Text `#0B0F14` (Bold 14sp). Height: 48dp. Radius: 10dp.
* **Destructive (Sell / Bearish / Reset):** Background `#FF657A`, Text `#0B0F14` (Bold 14sp). Height: 48dp.
* **Secondary / Outlined:** Background `transparent`, Border `1.dp solid #253140`, Text `#EAF0F6`.
* **Ghost / Icon Action:** Transparent background with `#92A0B2` icon tint.
* **States (Compose):**
  * *Default:* Standard token colors.
  * *Pressed / Ripple:* Surface lightens by 12% with `indication = rememberRipple()`.
  * *Disabled:* Background `#1C2734`, Text `#5A687A`, Border `#253140`.
  * *Loading:* Replaced with circular progress indicator (`strokeWidth = 2.dp`, color `#0B0F14`).

### 5.2 Inputs & Steppers
* **Text / Search Input:** Height `48.dp`, Background `#121923`, Border `1.dp solid #253140`, Radius `10.dp`, Text `#EAF0F6`, Cursor `#38D996`.
* **Number Stepper (Quantity / Stop Loss):**
  * Left button `[-]` (44dp) $\rightarrow$ Center numerical text (tabular, 16sp) $\rightarrow$ Right button `[+]` (44dp).
* **States:** Focused (Border `#38D996`), Error (Border `#FF657A`, helper label), Inactive (Border `#253140`).

### 5.3 Cards
* **Watchlist Row Card:** Height `68.dp`, Surface `#121923`, Border `#253140`.
  * Left: Symbol (`RELIANCE`), Sub-label (`NSE · Energy`).
  * Center: Intraday sparkline chart (Green or Red trend).
  * Right: LTP (`₹2,948.50`), Badge `+1.24%` (`#38D996` with `10% alpha` background).
* **Position Card:** Surface `#121923`, Top row: Instrument + Qty, Bottom row: Avg Price, LTP, Unrealized P&L in Green/Red.

### 5.4 Bottom Sheets & Modals
* **Order Preview Sheet:** Draggable handle at top (`32.dp x 4.dp`, `#5A687A`), Sheet surface `#121923`, Top radius `20.dp`. Contains order value, estimated brokerage/taxes, stop-loss trigger price, and prominent "SIMULATION ONLY" alert banner.
* **Confirmation Dialog:** Alert for Account Reset (`₹10,00,000` reset) or Kill Switch. Centered modal, Background `#121923`, Border `1.dp solid #FF657A`.

### 5.5 Bottom Navigation Bar
* **Height:** `64.dp` + system navigation insets.
* **Background:** `#0B0F14` with top border `1.dp solid #253140`.
* **5 Standard Tabs:**
  1. `Home` (Dashboard `S04`)
  2. `Scanner` (Scanner `S08`)
  3. `Trade Desk` (F&O Options `S11`)
  4. `Algo Lab` (Strategy Builder `S12`)
  5. `Research` (AI Assistant & Briefs `S09`, `S10`)
* **States:** Active tab: Icon & Label in `#38D996`; Inactive tab: Icon & Label in `#92A0B2`.

### 5.6 Data Tables (Option Chain)
* **Columns:** `Calls OI` \| `Calls LTP` \| `STRIKE` \| `Puts LTP` \| `Puts OI`.
* **ATM Strike Highlight:** Row background `#1C2734` with subtle yellow border (`#F3C969`).
* **Tap Action:** Tapping any strike expands detailed Greeks ($\Delta$, $\Gamma$, $\Theta$, $\nu$) and Payoff sheet.

### 5.7 In-App Alerts & Watermark Banners
* **Illustrative / Mock Mode Watermark (Permanent):**
  * Height `24.dp`, Background `rgba(243, 201, 105, 0.12)`, Border `1.dp solid #F3C969`.
  * Text: `● ILLUSTRATIVE / Live quote not connected` (`#F3C969`, 11sp SemiBold).

---

## 6. Page Layout Specifications (Screens S01 to S20)

### S01 — Splash and Onboarding
* Carousel slides illustrating Market Discovery, Strategy Testing, and Paper Simulation.
* Bottom Actions: **Get started** (Primary Green Button) and **I already have an account** (Outlined).
* Regulatory disclosure disclaimer at base.

### S02 — Authentication & OTP
* Segmented control (`Mobile number` / `Email`).
* Clean numeric input with "+91" prefix.
* 6-box OTP entry screen with 02:00 minute expiry timer and resend trigger.

### S03 — Workspace Personalization
* Select trading universe: Checkable chips (`NSE`, `BSE`, `Cash`, `Futures`, `Options`).
* Density selection cards: `Beginner`, `Intermediate`, `Advanced`.
* Starting Virtual Capital selector: Default ₹10,00,000 with 1% risk per trade selector.

### S04 — Home Dashboard
* Top Bar: User avatar, greeting, search icon, data status pill.
* Market Indices Carousel: NIFTY 50, SENSEX, BANK NIFTY cards with daily % change.
* Paper Portfolio Card: Big virtual equity balance (`₹10,42,680`), daily P&L badge, and "Explore portfolio" CTA.
* Quick Action Pills: `Scan`, `AI Research`, `Algo Lab`, `Paper Trade`.
* Watchlist movers & Sector breadth overview.

### S05 — Market Overview & Heatmap
* Segmented tabs: `Indices`, `Heatmap`, `Breadth`.
* Sector Heatmap: Proportional tiles colored green/red based on sector daily performance.
* Advances/Declines bar: 1,426 advances vs 892 declines with ratio pill.

### S06 — Watchlist & Instrument Details
* Tabbed multi-watchlist bar (`Core Holdings`, `F&O Radar`, `+ New`).
* Search symbol trigger leading to full-screen instant search.
* Tapping item navigates to detail sheet: 52-week High/Low bar, volume, news, and "Open Advanced Chart" button.

### S07 — Advanced Candlestick Chart
* Full-screen chart canvas with interval selector (`1m`, `5m`, `15m`, `1H`, `1D`).
* Indicator overlay bar: `EMA`, `VWAP`, `RSI`, `MACD`.
* Crosshair inspection showing Open, High, Low, Close, Volume.
* Bottom bar: "Simulate Trade" button triggering Paper Terminal sheet directly.

### S08 — Market Scanner
* Preset scan chips: `Breakout`, `Breakdown`, `Momentum`, `Volume Spike`, `VWAP Reclaim`, `Gap`.
* Condition Builder Card: `Price above EMA 20` AND `RSI between 55 - 70` AND `Volume > 1.5x avg`.
* Matched results list with match timestamp and trigger criteria checklist.

### S09 — Instrument Research (Fundamentals)
* Segmented view: `Overview`, `Quarterly`, `Ratios`, `Peer Comparison`.
* Financial snapshot: Revenue, Net Profit, Operating Margin, Cash Flow with YoY growth indicators.
* Valuation grid: P/E, P/B, ROE, ROCE, Debt/Equity.

### S10 — AI Research Assistant
* Conversational research query bar with quick suggestions (*"How resilient are Infosys margins?"*).
* Sourced analysis cards structured with:
  * Executive conclusion.
  * Key factors checklist.
  * Bull / Base / Bear scenario matrix.
  * Numbered citations linked directly to verified quarterly statements.

### S11 — F&O Trade Desk & Option Chain
* Underlying selector (`NIFTY 50`, `BANK NIFTY`) with Expiry Date dropdown.
* Strike table with live LTP, PCR indicator (`1.12`), and ATM strike highlight.
* Expiry payoff curve rendering Max Risk, Breakeven point, and simulated profit trajectory.

### S12 — Strategy Builder (Algo Lab)
* Strategy metadata input: Name, Instrument, Timeframe.
* Section 01: Entry Condition blocks (e.g., `EMA 20 crosses above EMA 50`).
* Section 02: Exit Condition blocks (`Price closes below EMA 20` OR `Target 2R`).
* Section 03: Safeguards (`1% stop-loss`, `No trade on expiry day`, `2% daily loss cap`).

### S13 — Backtest Results
* Performance summary: Total Return (`+18.42%`), Win Rate (`58.6%`), Profit Factor (`1.84`), Max Drawdown (`6.20%`).
* Interactive historical equity curve graph with drawdown area chart.
* Trade log breakdown showing winners, losers, and modelled fees/taxes.

### S14 — Paper Trading Terminal
* Balance header: Available Virtual Cash vs Margin Used.
* Order Ticket: Toggle Buy / Sell, Quantity, Order Type (Market / Limit), Stop Loss, Target.
* Real-time estimated fee calculation (GST, STT, Exchange fees simulated).
* "Preview Simulated Buy" button launching confirmation sheet.

### S15 — Signals Feed
* Filter tabs: `New`, `Active`, `Invalidated`, `Expired`.
* Signal Card: Instrument, Direction (BUY/SELL), Strategy name, Entry Zone, Stop Loss, Target 1/2/3, Freshness timer.
* Direct action: "Simulate Trade" (pre-populates order ticket with signal stops).

### S16 — Paper Portfolio & Trade Journal
* Virtual Equity metric, Today's simulated P&L, Risk utilization progress bar.
* Active simulated positions with live P&L mark.
* Trade Journal tab: Process rating (1–5 stars), trade notes, attached chart screenshots, and strategy attribution.

### S17 — Broker Connection Hub
* Directory of supported brokers: Zerodha Kite, Upstox, Angel One, Dhan, Fyers.
* Status cards: Token validity, Last sync time, "Connect" CTA.
* Prominent banner: *"Account sync only. Live order placement is disabled."*

### S18 — Alerts & Activity
* Push notification history: Price alerts, Scanner triggers, Paper order fills, Risk limit warnings.
* Alert creation sheet: Price condition, threshold, quiet hours settings.

### S19 — Subscription & Plans
* Overview of Free, Pro, and Premium features.
* Prominent notice: *"All platform features are currently free during the introductory period."*

### S20 — Workspace Profile & Security
* User details, biometric app lock toggle, active sessions manager.
* Privacy & data export/deletion requests.
* Disclaimer & SEBI regulatory risk disclosure links.

---

## 7. Accessibility & Android System Rules

1. **Dual-Encoding for Financial States:** Never indicate profit/loss or buy/sell using color alone. Green values must always be accompanied by a plus sign and upward arrow (`+₹8,420 ▲`); Red values must include a minus sign and downward arrow (`-₹2,180 ▼`).
2. **TalkBack & Content Descriptions:** Every icon, chart point, and action button must supply a localized `contentDescription` (e.g., `contentDescription = "Buy Reliance, price 2948 rupees, bullish"`).
3. **Dynamic Font Scaling:** UI layouts must accommodate Android system font scaling up to `130%` without text clipping or button overflow (use flexible Compose `wrapContentHeight` and `Modifier.weight`).
4. **Contrast Compliance:** Minimum contrast of `4.5:1` for regular text and `3:1` for large numbers and primary buttons.
5. **Reduced Motion:** If user enables "Remove Animations" in Android accessibility settings, chart animations and transition effects must instantly resolve to their final state.
