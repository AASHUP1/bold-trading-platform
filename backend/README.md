# BOLD Backend API & Cloud PostgreSQL Deployment Guide

> **100% Free Plan Architecture:** Run your PostgreSQL Database on **Supabase** or **Neon**, push notifications on **Firebase Cloud Messaging (FCM)**, and host the Backend REST & WebSocket API on **Render** or **Railway** with **zero monthly subscription cost**.

---

## 1. Cloud PostgreSQL Setup (Free Tier)

You can choose either **Supabase** or **Neon** (both offer generous free PostgreSQL tiers):

### Option A: Supabase (Recommended)
1. Go to [supabase.com](https://supabase.com) and create a free account.
2. Click **"New Project"**, name it `bold-trading-db`, and choose **Singapore (ap-southeast-1)** for lowest latency to India.
3. Once created, go to **Project Settings $\rightarrow$ Database $\rightarrow$ Connection String $\rightarrow$ URI**.
4. Copy the URI string:
   ```
   postgresql://postgres:[YOUR-PASSWORD]@db.[PROJECT-REF].supabase.co:5432/postgres
   ```
5. Go to the **SQL Editor** tab in the Supabase Dashboard.
6. Open and run the migration files located in `backend/migrations/` in this exact order:
   * `001_create_users_and_workspaces.sql`
   * `002_create_instruments_and_candles.sql`
   * `003_create_paper_ledger_and_orders.sql`
   * `004_create_watchlists.sql`
   * `005_create_strategies_and_signals.sql`
   * `006_create_admin_vault_and_audit.sql`
   * `007_create_indexes_and_rls.sql`
   *(Or run `python migrations/run_migrations.py` with `DATABASE_URL` set in your terminal)*.

---

## 2. Firebase Push Notifications (FCM) Setup

1. Open [Firebase Console](https://console.firebase.google.com/) and select your project (`bold-trading-admin`).
2. Go to **Project Settings (⚙️) $\rightarrow$ Service Accounts**.
3. Under **Firebase Admin SDK**, select **Python** and click **"Generate new private key"**.
4. Download the generated `.json` file and save it as `backend/firebase-service-account.json`.
5. Set the environment variable:
   ```bash
   FIREBASE_CREDENTIALS_JSON="firebase-service-account.json"
   ```
   *(On cloud providers like Render, you can paste the raw JSON text directly into the `FIREBASE_CREDENTIALS_JSON` environment variable!)*.

---

## 3. Deploying the Backend API (Render Free Tier)

1. Push your repository to GitHub or GitLab.
2. Sign up at [render.com](https://render.com) (Free Tier).
3. Click **"New +" $\rightarrow$ "Web Service"** and connect your repository.
4. Set the configuration:
   * **Name:** `bold-trading-api`
   * **Root Directory:** `backend`
   * **Runtime:** `Python 3`
   * **Build Command:** `pip install -r requirements.txt`
   * **Start Command:** `uvicorn app.main:app --host 0.0.0.0 --port $PORT`
   * **Instance Type:** `Free`
5. Under **Environment Variables**, add:
   * `DATABASE_URL` = Your Supabase/Neon PostgreSQL connection URI
   * `JWT_SECRET` = A strong secret string (e.g. `bold_jwt_release_2026_quant`)
   * `FREE_LAUNCH_ACTIVE` = `true`
   * `FIREBASE_CREDENTIALS_JSON` = *(Pasted JSON content of your Firebase service account)*
6. Click **"Create Web Service"**.
7. Render will provide a free live HTTPS URL:
   ```
   https://bold-trading-api.onrender.com
   ```
   * **Interactive Swagger Documentation:** `https://bold-trading-api.onrender.com/docs`
   * **Health Check URL:** `https://bold-trading-api.onrender.com/v1/market/status`
   * **WebSocket Market Stream:** `wss://bold-trading-api.onrender.com/v1/stream/market`

---

## 4. Connecting the Android Mobile App

In your `mobile-application/local.properties` (or `mobile-application/ARCHITECTURE.md` configuration):

```properties
## Live Cloud API Endpoints
bold.api.base.url=https://bold-trading-api.onrender.com/v1/
bold.ws.base.url=wss://bold-trading-api.onrender.com/v1/stream/market

## Runtime Data Mode
bold.data.mode=live
bold.free.tier.override=true
```

When online, the Android app will seamlessly communicate with your live cloud backend and stream real-time ticks. If the user disconnects or has weak signal, the local Room SQLite DB automatically keeps the app running without interruption!
