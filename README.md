# BOLD Trading & Research Platform

> Institutional-grade Indian equities and derivatives trading, algo research, and simulated paper-trading platform with a zero-cost compliance console.

---

## Screenshot Placeholders

```text
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| BOLD Mobile: Dark Institutional Terminal (S04 Dashboard, S07 Advanced Charts, S14 Paper)   |
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| BOLD Admin Console: S21 Control Room (Feed Latency, Universal Key Vault, Strategy Queue)    |
+---------------------------------------------------------------------------------------------+
```

---

## Key Features

* **Native Android Terminal (Screens S01–S20):** High-contrast, dark-themed trading terminal (`#0B0F14` / `#121923`) built with Kotlin and Jetpack Compose.
* **Realistic Paper Trading Ledger:** Virtual trading initialized with ₹10,00,000 virtual capital, fee simulation (STT, GST, brokerage), and an append-only cash ledger.
* **F&O Trade Desk & Greeks:** Live option chain for NIFTY & BANK NIFTY featuring Delta, Gamma, Theta, Vega, strike OI, PCR, and expiry payoff curves.
* **Algo Lab & Backtesting:** Visual condition rule builder with historical simulation, win rates, profit factors, and equity curves.
* **Universal Secret & Key Vault (Admin):** Encrypted management, live testing, and zero-downtime rotation of third-party API keys (Market Data, Gemini AI, Broker OAuth, FCM).
* **Control Room & Feed Health (Admin S21):** Real-time monitoring of adapter latencies, error rates (< 0.50%), and emergency platform kill switches.
* **100% Free Launch Model:** Every platform feature is unlocked for all registered users without credit card requirements or paywalls.
* **Zero Live Execution Boundary:** Physical isolation between simulated paper trading and read-only broker connections.

---

## Tech Stack

| Subproject | Framework / Language | Infrastructure / Hosting | State / Storage |
| :--- | :--- | :--- | :--- |
| **[`admin-panel/`](file:///d:/trading%20app/admin-panel)** | React 18+ (TypeScript), Vite, Tailwind CSS | Firebase Hosting (Free Spark Tier) | TanStack Query v5, Zustand |
| **[`mobile-application/`](file:///d:/trading%20app/mobile-application)** | Kotlin 2.0+, Jetpack Compose, Material 3 | Android 10+ (API 29+) | MVI StateFlow, Room SQLite, DataStore |
| **Shared Platform** | Python FastAPI REST & WebSockets | Docker / Managed Cloud | PostgreSQL 16+ (Encrypted Vault), Redis |

---

## Quick Start (5 Steps)

1. **Clone the repository:**
   ```bash
   git clone https://github.com/bold-trading/bold-platform.git
   cd "trading app"
   ```
2. **Start the Admin Console (Web):**
   ```bash
   cd admin-panel && npm install && npm run dev
   ```
   *Admin console runs at `http://localhost:5173`.*
3. **Open Mobile Project in Android Studio:**
   * Open `mobile-application/` in **Android Studio Ladybug (2024.2+)** with JDK 17+.
4. **Configure Local Environment:**
   * Verify endpoints in `mobile-application/local.properties` and `admin-panel/.env`.
5. **Assemble and Run Android App:**
   ```bash
   cd mobile-application && ./gradlew assembleDebug
   ```

---

## Environment Variables

| Subproject | Variable Name | Purpose | Example Value |
| :--- | :--- | :--- | :--- |
| **Admin** | `VITE_FIREBASE_API_KEY` | Firebase Web Client Auth Key | `AIzaSyA1B2C3...` |
| **Admin** | `VITE_API_BASE_URL` | BOLD Backend REST API Base URL | `https://api.boldtrading.internal/v1` |
| **Mobile** | `bold.api.base.url` | Backend REST Gateway Endpoint | `https://api.boldtrading.internal/v1/` |
| **Mobile** | `bold.ws.base.url` | Real-time Market WebSocket Endpoint | `wss://api.boldtrading.internal/v1/stream/market` |
| **Mobile** | `bold.data.mode` | Data integrity mode (`mock`, `delayed`, `live`) | `mock` |
| **Mobile** | `bold.free.tier.override` | Enforces 100% free mode across all screens | `true` |

---

## Monorepo Folder Structure Overview

```text
D:\trading app\
├── AGENTS.md             # Rulebook for all AI Coding Assistants (< 100 lines)
├── README.md             # Monorepo Project Overview
│
├── admin-panel/          # Web Admin & Compliance Console (React + Firebase Hosting)
│   ├── PRD.md            # Product Requirements Document
│   ├── DESIGN.md         # Web Design System Specification
│   ├── ARCHITECTURE.md   # Web System Architecture & Telemetry Flows
│   ├── DATABASE.md       # PostgreSQL Secret Vault & Audit Schema
│   ├── API.md            # REST API Specification (Secret Rotation & Governance)
│   ├── AGENTS.md         # Admin Assistant Guidelines
│   ├── README.md         # Admin Subproject Documentation
│   └── src/              # React Source Code
│
└── mobile-application/   # Android Mobile App (Kotlin + Jetpack Compose)
    ├── PRD.md            # Product Requirements Document
    ├── DESIGN.md         # Mobile UI Design System (Tokens & S01-S20 Layouts)
    ├── ARCHITECTURE.md   # Multi-Module Android Architecture & UDF Flow
    ├── DATABASE.md       # Primary PostgreSQL & Android Room SQLite Schemas
    ├── API.md            # REST & WebSocket API Specification (Paper Trading & F&O)
    ├── AGENTS.md         # Mobile Assistant Guidelines
    ├── README.md         # Mobile Subproject Documentation
    ├── app/              # Application Assembly & Navigation Graph
    ├── core/             # Shared Design System, Networking, Database, Canvas Charts
    └── feature/          # Feature Modules (S01 to S20)
```

---

## Available Scripts

### Admin Panel (npm)
* `npm run dev`: Starts Vite local development server on port `5173`.
* `npm run build`: Type-checks and bundles static assets for Firebase Hosting.
* `npm run lint`: Analyzes code quality using ESLint.
* `npm test`: Runs Vitest unit and component test suites.

### Mobile Application (Gradle)
* `./gradlew assembleDebug`: Compiles and generates debug APK.
* `./gradlew test`: Runs JUnit 5 and MockK unit tests across all feature modules.
* `./gradlew connectedAndroidTest`: Runs Compose UI instrumentation tests on an emulator/device.
* `./gradlew lintDebug`: Runs Android Lint analysis.

---

## Contributing & License

* **Rules:** Every contributor and AI agent must adhere strictly to [`AGENTS.md`](file:///d:/trading%20app/AGENTS.md) and consume tokens from [`DESIGN.md`](file:///d:/trading%20app/mobile-application/DESIGN.md).
* **Source-of-Truth Hierarchy:** `PRD.md` $\rightarrow$ `DESIGN.md` $\rightarrow$ `ARCHITECTURE.md` $\rightarrow$ `DATABASE.md` $\rightarrow$ `API.md`.
* **License:** Proprietary. Copyright © 2026 BOLD Technologies. All rights reserved.
