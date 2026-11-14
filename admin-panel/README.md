# BOLD Admin & Compliance Console (Control Room)

> High-precision web operations console for telemetry monitoring, universal API key management, strategy governance, and emergency platform kill switches.

---

## Screenshot Placeholders

```text
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| S21 Platform Overview Dashboard: Active Users, Feed Latency (0-800ms), and Incident Center   |
+---------------------------------------------------------------------------------------------+
| [SCREENSHOT PLACEHOLDER]                                                                    |
| Universal Key & Secret Management Hub: Masked Secrets, Upstream Pings, and Rotation Modals  |
+---------------------------------------------------------------------------------------------+
```

---

## Key Features

* **Universal Key & Secret Vault:** Securely store, mask (`••••••••9A12`), test ping, and rotate external API keys (Market Data, Gemini AI, Broker OAuth, FCM) with zero backend redeployment.
* **Telemetry & Feed Health (Screen S21):** Real-time monitoring of NSE/BSE adapter latency, error rates (< 0.50% threshold), and degraded data alerts.
* **Emergency Platform Kill Switch:** High-visibility toggle to freeze live signal distribution and paper order simulations during anomalous feed events.
* **Strategy Approval Queue:** Formal review and sign-off workflow for quant strategies (win rates, drawdowns, risk levels) before mobile broadcasting.
* **Global Free-Tier Governance:** One-click feature flag controls enforcing 100% free access for all registered mobile users at launch.
* **Tamper-Evident Audit Trail:** SHA-256 hashed log recording every administrative action, IP address, and timestamp.

---

## Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Framework & Language** | React 18+ with TypeScript |
| **Build Tool** | Vite |
| **Styling & Components** | Tailwind CSS + Shadcn UI (Radix Primitives) |
| **State Management** | TanStack Query v5 (React Query) & Zustand |
| **Authentication** | Firebase Authentication (Admin Roles) |
| **Hosting** | Firebase Hosting (Free Spark Tier) |
| **Backend Integration** | FastAPI (Python) + PostgreSQL & Redis |

---

## Quick Start (5 Steps)

1. **Navigate to the admin folder:**
   ```bash
   cd admin-panel
   ```
2. **Copy environment variables:**
   ```bash
   cp .env.example .env
   ```
3. **Install dependencies:**
   ```bash
   npm install
   ```
4. **Start the local development server:**
   ```bash
   npm run dev
   ```
   *Dashboard opens at `http://localhost:5173`.*
5. **Build and deploy to Firebase Hosting:**
   ```bash
   npm run build && firebase deploy --only hosting
   ```

---

## Environment Variables

| Variable Name | Purpose | Example Value |
| :--- | :--- | :--- |
| `VITE_FIREBASE_API_KEY` | Firebase Web Client API key for authentication | `AIzaSyA1B2C3D4E5F6G7H8` |
| `VITE_FIREBASE_AUTH_DOMAIN` | Firebase Authentication domain | `bold-trading-admin.firebaseapp.com` |
| `VITE_FIREBASE_PROJECT_ID` | Firebase project identifier | `bold-trading-admin` |
| `VITE_API_BASE_URL` | BOLD Backend REST API base route | `https://api.boldtrading.internal/v1` |
| `VITE_APP_ENV` | Application environment identifier | `production` |

---

## Folder Structure Overview

```text
admin-panel/
├── src/
│   ├── app/                # Root App, Providers, and React Router v6
│   ├── components/
│   │   ├── ui/             # Buttons, Inputs, Dialogs, Tables (DESIGN.md strict)
│   │   ├── layout/         # AdminLayout, 240px Sidebar, and Topbar
│   │   └── feedback/       # ErrorBoundary, KillSwitchModal, Skeletons
│   ├── features/
│   │   ├── auth/           # Firebase Admin login
│   │   ├── overview/       # Screen S21 Telemetry & Telemetry Charts
│   │   ├── keys/           # Universal Key & Secret Manager Hub
│   │   ├── strategies/     # Strategy Review & Approval Queue
│   │   ├── feed-health/    # Broker Adapters & Feed Health
│   │   ├── governance/     # Feature Flags & Emergency Kill Switch
│   │   └── audit/          # Tamper-Evident Audit Trail
│   ├── lib/                # Axios API client, Firebase SDK, Zod validations
│   └── stores/             # Zustand UI stores (modals, active filters)
├── firebase.json           # Firebase Hosting rewrites & headers
└── vite.config.ts          # Vite build configuration
```

---

## Available Scripts

| Script | Command | Purpose |
| :--- | :--- | :--- |
| **Development** | `npm run dev` | Starts Vite HMR dev server on port `5173`. |
| **Build** | `npm run build` | Runs TypeScript compiler (`tsc`) and bundles optimized static assets. |
| **Lint** | `npm run lint` | Runs ESLint to enforce TypeScript code conventions and token rules. |
| **Test** | `npm test` | Runs Vitest for component and utility unit test suites. |

---

## Contributing & License

* **Rules:** All contributions must strictly respect [`admin-panel/AGENTS.md`](file:///d:/trading%20app/admin-panel/AGENTS.md) and consume design tokens from [`admin-panel/DESIGN.md`](file:///d:/trading%20app/admin-panel/DESIGN.md).
* **Source of Truth:** Never alter API contracts or database models without consulting [`admin-panel/PRD.md`](file:///d:/trading%20app/admin-panel/PRD.md).
* **License:** Proprietary. Copyright © 2026 BOLD Technologies. All rights reserved.
