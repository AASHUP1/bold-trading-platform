# UI/UX Design System Specification: BOLD Admin & Compliance Console
**Document:** DESIGN.md  
**Product:** BOLD Control Room (Admin Panel)  
**Target Framework:** React 18+ (Vite, Tailwind CSS, Shadcn/Radix UI)  
**Hosting Target:** Firebase Hosting (Free Spark Tier)  
**Source of Truth:** [`admin-panel/PRD.md`](file:///d:/trading%20app/admin-panel/PRD.md) and BOLD Design Brief v1.0 (Screen S21)  

---

## 1. Brand Feel & 3 Design Principles

### Brand Feel
**"Mission Control: Vigilant, Uncluttered, and High-Precision."**  
The BOLD Admin Console is an institutional command center for platform operators, quant engineers, and compliance officers. It prioritizes clarity over ornamentation, instantaneous comprehension of system anomalies, and fail-safe confirmation for critical actions (key rotation, strategy publication, emergency kill switch).

### 3 Design Principles
1. **Zero-Ambiguity Telemetry (Visibility First):** System status, feed health, and API error rates are visible within 1 second of loading. System degradations are highlighted with distinct, non-color-exclusive indicators (badges + status icons).
2. **Defensive Action Design (Safe by Default):** Destructive or high-impact actions (emergency kill switch, secret key rotation, strategy rejections) require explicit confirmation modals, role authorization verification, and automated audit logging.
3. **Institutional Density & Readability:** Dense tabular data layouts designed for professional operations without visual clutter. Monospace tabular numerals for all metrics, latencies, and timestamps.

---

## 2. Color Palette (Strict System)

Every component must strictly use the hex tokens below. No extraneous colors are permitted.

### 2.1 Dark Mode (Primary / Default Operational Theme)
| Token Name | HEX Code | CSS Variable | Usage |
| :--- | :--- | :--- | :--- |
| **App Background** | `#0B0F14` | `--bg-app` | Main root canvas and sidebar base |
| **Surface (Card)** | `#121923` | `--surface-card` | Data cards, table containers, dialog background |
| **Elevated Surface** | `#1C2734` | `--surface-elevated`| Hovered rows, active sidebar items, modals, popovers |
| **Border / Divider** | `#253140` | `--border-subtle` | Card borders, table dividers, input borders |
| **Primary Accent** | `#38D996` | `--primary-bullish`| Primary CTA, approved state, positive metric, toggle ON |
| **Secondary Accent** | `#2A85FF` | `--primary-accent` | Informational links, secondary badges, active filters |
| **Error / Destructive** | `#FF657A` | `--accent-bearish` | Error alerts, emergency kill switch, invalid keys, failed jobs |
| **Warning / Caution** | `#F3C969` | `--accent-warning` | Degraded feeds, elevated latency, quota >80%, pending reviews |
| **Primary Text** | `#EAF0F6` | `--text-primary` | High-contrast headings, active values, card titles |
| **Secondary Text** | `#92A0B2` | `--text-secondary` | Labels, subtitles, table headers, inactive state |
| **Muted Text / Placeholder** | `#5A687A` | `--text-muted` | Input placeholders, disabled text, hints |

### 2.2 Light Mode (Clean Institutional Theme)
| Token Name | HEX Code | CSS Variable | Usage |
| :--- | :--- | :--- | :--- |
| **App Background** | `#F8FAFC` | `--bg-app` | Slate-50 root background |
| **Surface (Card)** | `#FFFFFF` | `--surface-card` | Pure white cards and panels |
| **Elevated Surface** | `#F1F5F9` | `--surface-elevated`| Slate-100 raised items, hovered rows |
| **Border / Divider** | `#CBD5E1` | `--border-subtle` | Slate-300 structural borders |
| **Primary Accent** | `#059669` | `--primary-bullish`| Emerald-600 primary action |
| **Secondary Accent** | `#0284C7` | `--primary-accent` | Sky-600 informational links and buttons |
| **Error / Destructive** | `#DC2626` | `--accent-bearish` | Red-600 danger action, kill switch |
| **Warning / Caution** | `#D97706` | `--accent-warning` | Amber-600 warning notifications |
| **Primary Text** | `#0F172A` | `--text-primary` | Slate-900 high-contrast copy |
| **Secondary Text** | `#475569` | `--text-secondary` | Slate-600 secondary labels |
| **Muted Text / Placeholder** | `#94A3B8` | `--text-muted` | Slate-400 input placeholders |

---

## 3. Typography Scale & Weights

* **Primary Typeface:** `Inter`, `-apple-system`, `BlinkMacSystemFont`, `sans-serif`
* **Monospace / Metric Typeface:** `JetBrains Mono`, `Roboto Mono`, `monospace` (Mandatory for API keys, JSON payloads, latencies, timestamps, and error codes)
* **Tabular Numbers:** `font-feature-settings: "tnum" 1` applied globally on all metrics and financial data.

| Style Name | Font Size | Line Height | Weight | Tracking | Usage |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Display 1** | `28px` (1.75rem) | `36px` | 700 (Bold) | `-0.02em` | Page header titles |
| **Heading 2** | `22px` (1.375rem)| `28px` | 600 (SemiBold) | `-0.01em` | Card section titles, modal headers |
| **Heading 3** | `18px` (1.125rem)| `24px` | 600 (SemiBold) | `0em` | Sub-sections, table modal headers |
| **Body Regular** | `14px` (0.875rem) | `20px` | 400 (Regular) | `0em` | Primary body text, descriptions |
| **Body Medium** | `14px` (0.875rem) | `20px` | 500 (Medium) | `0em` | Form labels, table cells, button text |
| **Caption / Small**| `12px` (0.75rem) | `16px` | 400 (Regular) | `0.01em` | Timestamps, helper text, breadcrumbs |
| **Badge / Micro** | `11px` (0.6875rem)| `14px` | 600 (SemiBold) | `0.04em` | Status chips (UPPERCASE) |
| **Code / Key Mono**| `13px` (0.8125rem)| `18px` | 500 (Medium) | `0em` | Masked API secrets, audit hashes |

---

## 4. Spacing Scale, Radius, Shadows & Breakpoints

### 4.1 Spacing Scale (8dp / 4dp System)
* `space-1`: `4px` (tight padding, icon spacing)
* `space-2`: `8px` (inner button padding, chip gap)
* `space-3`: `12px` (form field vertical padding, badge spacing)
* `space-4`: `16px` (card internal padding, gutter)
* `space-6`: `24px` (card layout spacing, section gaps)
* `space-8`: `32px` (page content padding)
* `space-12`: `48px` (major section separations)

### 4.2 Border Radius
* `radius-sm`: `6px` (Status badges, micro tags)
* `radius-md`: `8px` (Buttons, input fields, selects)
* `radius-lg`: `12px` (Cards, alert banners, popovers)
* `radius-xl`: `16px` (Modals, slide-out drawer panels)
* `radius-full`: `9999px` (Pill badges, avatars)

### 4.3 Shadows
* **Dark Mode:** Border-defined separation (`1px solid #253140`). Glow accents for status:
  * `shadow-glow-green`: `0 0 12px rgba(56, 217, 150, 0.25)`
  * `shadow-glow-red`: `0 0 14px rgba(255, 101, 122, 0.35)`
* **Light Mode:**
  * `shadow-sm`: `0 1px 2px 0 rgba(0, 0, 0, 0.05)`
  * `shadow-md`: `0 4px 6px -1px rgba(0, 0, 0, 0.08), 0 2px 4px -2px rgba(0, 0, 0, 0.04)`
  * `shadow-lg`: `0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.05)`

### 4.4 Breakpoints
* **Desktop Wide (Default):** `1440px` and above
* **Desktop / Laptop:** `1200px` to `1439px`
* **Tablet Landscape:** `992px` to `1199px` (Collapsible sidebar into icon rail)
* **Tablet Portrait / Minimum:** `768px` (Drawer navigation; tables enable horizontal scroll)

---

## 5. Component Specifications (All States)

### 5.1 Buttons
* **Variants:**
  * `Primary (Green)`: Background `#38D996`, Text `#0B0F14`, Font Weight 600.
  * `Destructive (Red)`: Background `#FF657A`, Text `#0B0F14`, Font Weight 600.
  * `Secondary / Outline`: Background `transparent`, Border `1px solid #253140`, Text `#EAF0F6`.
  * `Ghost`: Background `transparent`, Text `#92A0B2`.
* **States:**
  * **Default:** Standard tokens.
  * **Hover:** Primary shifts to `#48E9A6`; Outline gets background `#1C2734`; Destructive shifts to `#FF7A8E`.
  * **Focus:** `2px solid #38D996` with `2px` offset (`focus-visible:ring-2`).
  * **Disabled:** Background `#1C2734`, Border `#253140`, Text `#5A687A`, Cursor `not-allowed`.
  * **Loading:** Text hidden or replaced with 16px SVG spinner; opacity 0.7; pointer events disabled.

### 5.2 Form Inputs & API Key Fields
* **Height:** `40px` (standard desktop form).
* **Base Style:** Background `#121923`, Border `1px solid #253140`, Radius `8px`, Text `#EAF0F6`, Font `Inter` 14px.
* **Key/Secret Masking:** API key inputs include an inline toggle eye icon (`Reveal / Mask`) and a `Copy` icon button.
* **States:**
  * **Default:** Border `#253140`, Placeholder `#5A687A`.
  * **Hover:** Border `#92A0B2`.
  * **Focus:** Border `#38D996`, Glow ring `0 0 0 1px #38D996`.
  * **Error:** Border `#FF657A`, helper text in `#FF657A` with warning icon.
  * **Disabled / Read-only:** Background `#0B0F14`, Border `#253140`, Text `#5A687A`.

### 5.3 Cards & Data Containers
* **Container:** Background `#121923`, Border `1px solid #253140`, Radius `12px`, Padding `20px`.
* **Card Header:** Title in `16px SemiBold (#EAF0F6)` + optional status badge on right.
* **Hover State (Interactive Cards):** Background transitions to `#1C2734`, Border `#38D996` (200ms ease).

### 5.4 Data Tables (Keys, Audit Logs, Strategy Queue)
* **Header:** Background `#0B0F14`, Text `#92A0B2` (12px uppercase, tracking 0.05em), Height `40px`.
* **Row:** Height `48px` to `56px`, Border bottom `1px solid #253140`.
* **Hover State:** Row background `#1C2734`.
* **Action Menu:** Right-aligned three-dots button (`...`) triggering dropdown popover.
* **Empty State:** Centered illustration icon, Text *"No records found"*, optional CTA.

### 5.5 Modals & Dialogs (Kill Switch, Key Rotation)
* **Backdrop:** Background `rgba(11, 15, 20, 0.75)` with `backdrop-filter: blur(4px)`.
* **Modal Box:** Width `480px` to `640px`, Background `#121923`, Border `1px solid #253140`, Radius `16px`, Padding `24px`.
* **Actions:** Right-aligned Cancel (Outline) + Confirm Action button.

### 5.6 Navigation Sidebar
* **Width:** `240px` (Expanded) / `64px` (Collapsed).
* **Active Item:** Background `#1C2734`, Left border accent `3px solid #38D996`, Text `#38D996`.
* **Inactive Item:** Text `#92A0B2`, Hover background `#1C2734` with Text `#EAF0F6`.

### 5.7 Toasts & Notifications
* **Position:** Top-Right, fixed `z-index: 9999`.
* **Width:** `360px`, Border radius `8px`, Padding `12px 16px`.
* **Success Toast:** Background `#121923`, Left border `4px solid #38D996`, Icon Check in `#38D996`.
* **Error Toast:** Background `#121923`, Left border `4px solid #FF657A`, Icon Alert in `#FF657A`.

---

## 6. Page Layout Specifications (Screen-by-Screen)

### 6.1 S21 — Platform Overview & Telemetry (`/overview`)
* **Layout:** Top Header (User role, Environment badge `SANDBOX / LIVE`, Current IST Clock) $\rightarrow$ 4 Metric Summary Cards $\rightarrow$ 2-Column Split:
  * **Card 1 (Total Users):** Large tabular metric `24,860` (+8.4% badge).
  * **Card 2 (Active Subscriptions):** `4,218` (Free tier note).
  * **Card 3 (Feed Health):** Badge `DEGRADED` (Yellow `#F3C969`), adapter breakdown pills.
  * **Card 4 (API Error Rate):** `0.42%` (vs threshold `0.50%`).
* **Left Column (Feed Latency & Incident Center):** Latency area chart (0–800ms) over last 60 minutes + Active incidents list.
* **Right Column (Strategy Review Queue & Governance):** Table of pending strategies (`NIFTY Momentum v1.4`, etc.) + Feature flag controls.

### 6.2 Key & Secret Management Hub (`/keys`)
* **Layout:** 3 Category Tabs: `Market Data Feeds`, `Broker OAuth Credentials`, `AI & Notifications`.
* **Key Item Card:**
  * Left: Provider Logo / Name (e.g. `Google Gemini API`, `Zerodha Kite Connect`, `Upstox Developer`).
  * Center: Masked Key string (`sk-gemini-••••••••9A12`), Last rotated timestamp, Status Badge (`Valid`, `Rate-Limited`, `Expired`).
  * Right: Action Buttons: `Test Ping`, `Rotate Key`, `Revoke`.
* **Rotate Key Modal:** Input field with validation, "Send Test Payload" button, save confirmation.

### 6.3 Strategy Review & Approval Queue (`/strategies`)
* **Layout:** Filterable table by Risk (`All`, `Low`, `Medium`, `High`) and Status (`Pending`, `Approved`, `Changes Requested`).
* **Table Columns:** Strategy Name, Owner, Target Instrument, Timeframe, Win Rate %, Profit Factor, Max Drawdown %, Actions (`Review`, `Approve`, `Reject`).
* **Review Drawer (Side Panel):** Slides out showing full rule logic (Entry, Exit, Stop Loss, Trailing Stop), In-sample vs Out-of-sample curves, and modeled cost assumptions.

### 6.4 Broker Adapters & Feed Health Monitor (`/feed-health`)
* **Layout:** Grid of 6 Broker Adapter Cards (Zerodha, Upstox, Angel One, Dhan, Fyers, 5paisa).
* **Card Details:** Connection Status (`Connected`, `Offline`, `Degraded`), WebSocket Latency (ms), Token Expiration countdown, Reconnect button, Force Sync button.

### 6.5 Feature Flags & Emergency Kill Switch (`/governance`)
* **Emergency Banner (Top):** Red border banner with toggle: **"MASTER KILL SWITCH: Halt All Signals & Paper Executions"**.
* **Feature Toggles List:**
  * `GLOBAL_FREE_TIER_OVERRIDE`: `true` (locked on for launch).
  * `F_AND_O_CHAIN_ENABLED`: `true`.
  * `AI_RESEARCH_ASSISTANT`: `true`.
  * `STRATEGY_AUTO_BACKTEST`: `true`.

### 6.6 Tamper-Evident Audit Trail (`/audit`)
* **Layout:** Search bar (by event ID, IP, admin user) + Date range filter + Paginated table.
* **Columns:** Timestamp (IST), Admin Email, Action Category, Target Resource, IP Address, Event Hash ID (`EVT 10482`).

---

## 7. Accessibility & Implementation Rules

1. **Contrast Standards (WCAG 2.1 AA):** All text elements must achieve at least `4.5:1` contrast ratio against their background surface (`#EAF0F6` on `#121923` achieves `13.8:1`).
2. **Keyboard Navigation:** Every interactive table row, button, modal close, and dropdown must be reachable via `Tab` and triggered with `Enter` / `Space`.
3. **Visible Focus Rings:** Every focused interactive element must render a visible `2px` focus ring (`#38D996` with `2px` offset). Never use `outline: none` without providing a focus ring.
4. **Non-Color Status Cues:** Never communicate errors or warnings through color alone. Error badges must include an alert SVG icon or text label (`ERROR: `, `DEGRADED: `).
5. **Screen Reader Labels:** Masked API fields must include `aria-label="API Secret Key masked for security"`.
