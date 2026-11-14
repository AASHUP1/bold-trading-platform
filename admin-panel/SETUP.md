# Admin Panel Local Setup Guide

> Fast-track guide for setting up and running the BOLD Admin & Compliance Console (React 18+ on Firebase Hosting). For full monorepo setup instructions, see the master [`SETUP.md`](file:///d:/trading%20app/SETUP.md).

---

## 1. Prerequisites
* **Node.js**: `v20.x` or `v22.x` (LTS) — check with `node -v`
* **npm**: `10.x+` or `11.x+` — check with `npm -v`
* **Firebase CLI** (optional for deployment): `npm install -g firebase-tools`

---

## 2. Quick Setup (3 Steps)

1. **Install dependencies:**
   ```bash
   cd admin-panel
   npm install
   ```

2. **Configure environment variables:**
   ```bash
   # Windows (PowerShell)
   Copy-Item .env.example .env

   # macOS / Linux
   cp .env.example .env
   ```
   *Verify that `.env` contains your Firebase client credentials and `VITE_API_BASE_URL="http://localhost:8000/v1"`.*

3. **Start the local development server:**
   ```bash
   npm run dev
   ```
   *Open your browser at `http://localhost:5173`.*

---

## 3. Available npm Scripts

| Script | Command | Purpose |
| :--- | :--- | :--- |
| **Dev Server** | `npm run dev` | Launches Vite HMR server on port `5173`. |
| **Production Build** | `npm run build` | Compiles TypeScript and creates optimized static assets in `dist/`. |
| **Code Linting** | `npm run lint` | Runs ESLint for TypeScript code quality and token adherence. |
| **Unit Tests** | `npm test` | Runs Vitest for component and utility test suites. |
| **Firebase Deploy** | `npm run deploy` | Bundles and deploys static assets to Firebase Hosting. |

---

## 4. Common Troubleshooting

* **Port 5173 occupied (`EADDRINUSE`):**
  Run Vite on an alternate port: `npm run dev -- --port 5174`.
* **Missing Firebase credentials:**
  Ensure `.env` exists in `admin-panel/` and starts with the prefix `VITE_`.
* **CORS errors when contacting backend:**
  Ensure the backend at `http://localhost:8000` enables CORS for `http://localhost:5173`.
