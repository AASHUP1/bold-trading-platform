# BOLD Mobile Application (Android)

> Institutional-grade Indian equities & derivatives trading, algo research, and simulated paper-trading platform built with Kotlin and Jetpack Compose.

---

## Screenshot Placeholders

```text
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| S04 Home Dashboard: NIFTY 50, Virtual Portfolio Card (₹10,42,680), and Quick Actions        |
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| S07 Advanced Candlestick Chart: 15m Candles, EMA 20/50, VWAP, and Strategy Overlays         |
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| S11 F&O Trade Desk: Option Chain, Greeks (Delta, Gamma, Theta, Vega), and Payoff Curve      |
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| S14 Paper Trading Terminal: Order Ticket, Stop-Loss / Target Stepper, and Simulation Banner   |
+---------------------------------------------------------------------------------------------+
```

---

## Key Features

* **Institutional Dark Terminal:** Sleek, high-contrast, distraction-free trading UI built on an 8dp grid with tabular numerals (`tnum`) for price stability.
* **Paper Trading Engine & Ledger:** Realistic simulated execution initialized with ₹10,00,000 virtual capital, fee modeling (STT, GST, brokerage), and an append-only cash ledger.
* **Interactive Candlestick Charting:** Hardware-accelerated canvas charts with intervals from 1m to 1D, EMA 20/50, VWAP, RSI, MACD, and trade simulation triggers.
* **F&O Trade Desk:** Index option chain (NIFTY, BANK NIFTY) featuring live Greeks ($\Delta$, $\Gamma$, $\Theta$, $\nu$), strike-level open interest, PCR, and expiry payoff curves.
* **Algo Lab & Strategy Backtesting:** Visual condition rule builder with historical backtesting engine providing equity curves, win rates, and drawdown analysis.
* **Market Scanner & Real-Time Signals:** Pre-built technical scans (Breakout, Volume Spike, VWAP Reclaim) paired with a signals feed with defined entry and invalidation zones.
* **100% Free Launch Model:** Every platform feature is unlocked for all users at launch with zero credit card requirements or subscription paywalls.
* **Zero Live Execution Boundary:** Paper trading is physically separated from live broker endpoints. Broker integrations are strictly read-only for portfolio sync.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Language** | Kotlin 2.0+ |
| **UI Toolkit** | Jetpack Compose + Material 3 |
| **Architecture Pattern** | Unidirectional Data Flow (UDF / MVI) with ViewModel & `StateFlow` |
| **Dependency Injection** | Hilt (Dagger) |
| **Networking & Streaming**| Retrofit 2 + OkHttp 4 & OkHttp WebSocket |
| **Local Cache & Storage** | Room Database (SQLite) & Jetpack DataStore |
| **Charting Engine** | Custom Compose Canvas & Vico Charts |

---

## Quick Start (5 Steps)

1. **Open the project:**
   * Launch **Android Studio Ladybug (2024.2+)** or newer and open the `mobile-application` directory.
2. **Verify JDK and SDK:**
   * Ensure JDK 17+ is selected and Android SDK API Level 34 is installed.
3. **Configure environment properties:**
   * Verify or create `local.properties` in `mobile-application/`:
     ```properties
     bold.api.base.url=https://api.boldtrading.internal/v1/
     bold.ws.base.url=wss://api.boldtrading.internal/v1/stream/market
     bold.data.mode=mock
     ```
4. **Build and assemble the project:**
   ```bash
   ./gradlew assembleDebug
   ```
5. **Run on an Android device or emulator:**
   * Select a connected device running Android 10+ (API 29+) and click **Run ▶** (or run `./gradlew installDebug`).

---

## Environment Variables (`local.properties` / `BuildConfig`)

| Variable Name | Purpose | Example Value |
| :--- | :--- | :--- |
| `bold.api.base.url` | Backend REST API gateway route | `https://api.boldtrading.internal/v1/` |
| `bold.ws.base.url` | Real-time WebSocket streaming endpoint | `wss://api.boldtrading.internal/v1/stream/market` |
| `bold.data.mode` | Data integrity mode (`mock`, `delayed`, `live`) | `mock` |
| `bold.free.tier.override` | Global toggle enforcing 100% free mode | `true` |
| `bold.enable.http.logging` | Enables OkHttp logging interceptor for debugging | `true` |

---

## Folder Structure Overview

```text
mobile-application/
├── app/                             # Main application module, BoldNavHost, BottomNavBar
├── core/
│   ├── designsystem/                # Strict tokens (#0B0F14, #38D996), typography, buttons
│   ├── networking/                  # Retrofit REST client and OkHttp WebSocket manager
│   ├── database/                    # Room DB (CachedInstrument, CachedCandle, Dao)
│   ├── charts/                      # Hardware-accelerated Candlestick & Payoff Canvas
│   └── common/                      # Dispatchers, Rupee formatters, Flow extensions
└── feature/
    ├── onboarding/                  # Screens S01-S03 (Auth, OTP, Setup)
    ├── dashboard/                   # Screen S04 (Home terminal overview)
    ├── market/                      # Screen S05 (Market overview, sector heatmap)
    ├── watchlist/                   # Screen S06 (Watchlists & details)
    ├── charts/                      # Screen S07 (Advanced full-screen chart)
    ├── scanner/                     # Screen S08 (Market scanner presets & custom rules)
    ├── research/                    # Screens S09, S10 (Fundamentals & AI Assistant)
    ├── options/                     # Screen S11 (F&O trade desk & Option Chain)
    ├── algo/                        # Screen S12 (Strategy builder)
    ├── backtest/                    # Screen S13 (Backtest results & metrics)
    ├── papertrading/                # Screen S14 (Virtual terminal & order ticket)
    ├── signals/                     # Screen S15 (Real-time signals feed)
    ├── portfolio/                   # Screen S16 (Virtual ledger & trade journal)
    ├── brokers/                     # Screen S17 (Read-only broker sync hub)
    ├── notifications/               # Screen S18 (Push alerts & activity history)
    └── settings/                    # Screens S19, S20 (Profile & free settings)
```

---

## Available Scripts (Gradle Tasks)

| Task | Command | Purpose |
| :--- | :--- | :--- |
| **Assemble Debug** | `./gradlew assembleDebug` | Compiles and generates debug APK in `app/build/outputs/apk/debug/`. |
| **Unit Tests** | `./gradlew test` | Executes JUnit 5 & MockK unit tests across all feature modules. |
| **UI Instrumentation** | `./gradlew connectedAndroidTest` | Runs Compose UI tests on connected physical devices or emulators. |
| **Lint & Quality** | `./gradlew lintDebug` | Analyzes Kotlin/Compose code against Android Lint rules. |

---

## Contributing & License

* **Rules:** All contributions must strictly follow [`mobile-application/AGENTS.md`](file:///d:/trading%20app/mobile-application/AGENTS.md) and consume tokens from [`mobile-application/DESIGN.md`](file:///d:/trading%20app/mobile-application/DESIGN.md).
* **Source of Truth:** Never modify Room entities or REST contracts without updating [`mobile-application/DATABASE.md`](file:///d:/trading%20app/mobile-application/DATABASE.md) and [`mobile-application/API.md`](file:///d:/trading%20app/mobile-application/API.md).
* **License:** Proprietary. Copyright © 2026 BOLD Technologies. All rights reserved.
