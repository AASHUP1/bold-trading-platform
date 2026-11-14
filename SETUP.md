# Local Environment Setup & Developer Onboarding Guide

> Complete, step-by-step, beginner-friendly instructions for installing, configuring, running, testing, and troubleshooting the BOLD Trading & Research Platform (React Admin Console, Android Mobile Application, and Backend Services) locally on **Windows**, **macOS**, and **Linux**.

---

## 1. Prerequisites & Version Requirements

Ensure the following tools are installed before proceeding. The required versions have been verified for stability.

| Software / Tool | Minimum Version | Recommended Version | Verification Command |
| :--- | :--- | :--- | :--- |
| **Git** | `2.40.0+` | `2.45.x+` | `git --version` |
| **Node.js** | `v20.12.0` (LTS) | `v22.21.x` | `node -v` |
| **npm** | `10.5.0+` | `11.5.x+` | `npm -v` |
| **Java Development Kit (JDK)** | `JDK 17` | `OpenJDK 21 (Temurin)` | `java -version` |
| **Android Studio** | `Iguana (2023.2+)` | `Ladybug (2024.2+)` | Open Studio $\rightarrow$ Help $\rightarrow$ About |
| **Android SDK Platform** | `API 29` (Android 10) | `API 34` (Android 14) | SDK Manager $\rightarrow$ SDK Platforms |
| **Python** (for Backend API) | `3.11.0` | `3.12.x` / `3.13.x` | `python --version` (or `python3`) |
| **PostgreSQL** | `15.0+` | `16.x` | `psql --version` |
| **Redis** | `7.0+` | `7.2.x` | `redis-cli --version` |

### Verifying Prerequisites on Your Machine

#### Windows (PowerShell):
```powershell
git --version
node -v
npm -v
java -version
python --version
```

#### macOS / Linux (bash / zsh):
```bash
git --version
node -v
npm -v
java -version
python3 --version
```

---

## 2. Repository Cloning & Workspace Structure

### Step 2.1: Clone the Repository

#### Windows (PowerShell):
```powershell
git clone https://github.com/bold-trading/bold-platform.git "d:\trading app"
Set-Location -Path "d:\trading app"
```

#### macOS / Linux (bash / zsh):
```bash
git clone https://github.com/bold-trading/bold-platform.git ~/bold-platform
cd ~/bold-platform
```

### Step 2.2: Workspace Structure Verification
Ensure your local workspace displays the following directories:
```text
D:\trading app\ (or ~/bold-platform/)
├── admin-panel/          # React 18+ web console for telemetry & secret management
├── mobile-application/   # Android native Kotlin & Jetpack Compose app
├── AGENTS.md             # Rulebook for AI coding assistants
├── ARCHITECTURE.md       # Full architecture & multi-module specs
├── DATABASE.md           # Database design, schemas, indexes, and seed SQL
├── API.md                # REST & WebSocket API specification
└── README.md             # Project overview
```

---

## 3. Subsystem Installation & Environment Configuration

### 3.1 Admin & Compliance Console (`admin-panel/`)

#### Step 1: Navigate and install dependencies
```bash
cd admin-panel
npm install
```

#### Step 2: Configure Environment Variables
Create the `.env` file from the provided template:

**Windows (PowerShell):**
```powershell
Copy-Item -Path ".env.example" -Destination ".env"
```

**macOS / Linux:**
```bash
cp .env.example .env
```

#### Step 3: Populate `.env` with Development Values
Open `admin-panel/.env` in your editor and verify:
```env
# Firebase Web Client Configuration (For Admin Authentication)
VITE_FIREBASE_API_KEY="AIzaSyA_DEMO_KEY_FOR_LOCAL_DEV"
VITE_FIREBASE_AUTH_DOMAIN="bold-trading-admin.firebaseapp.com"
VITE_FIREBASE_PROJECT_ID="bold-trading-admin"
VITE_FIREBASE_STORAGE_BUCKET="bold-trading-admin.appspot.com"
VITE_FIREBASE_MESSAGING_SENDER_ID="123456789012"
VITE_FIREBASE_APP_ID="1:123456789012:web:abcdef123456"

# BOLD Backend REST API Base URL
VITE_API_BASE_URL="http://localhost:8000/v1"

# Environment Mode
VITE_APP_ENV="sandbox"
```

---

### 3.2 Mobile Application (`mobile-application/`)

#### Step 1: Open the Project in Android Studio
1. Launch **Android Studio Ladybug (2024.2+)**.
2. Select **Open** $\rightarrow$ Navigate to `D:\trading app\mobile-application` (or `~/bold-platform/mobile-application`).
3. Allow Gradle to perform the initial project sync.

#### Step 2: Configure `local.properties`
In the `mobile-application/` root directory, create or edit `local.properties`:

**Windows (PowerShell):**
```properties
## Android SDK Location (Adjust to your user directory)
sdk.dir=C\:\\Users\\Hp\\AppData\\Local\\Android\\Sdk

## BOLD Backend API Endpoints (Local Emulator Loopback: 10.0.2.2 points to host localhost)
bold.api.base.url=http://10.0.2.2:8000/v1/
bold.ws.base.url=ws://10.0.2.2:8000/v1/stream/market

## Runtime Data Mode (mock | delayed | live)
bold.data.mode=mock

## 100% Free Launch Override
bold.free.tier.override=true
bold.enable.http.logging=true
```

**macOS / Linux:**
```properties
## Android SDK Location
sdk.dir=/Users/your_user/Library/Android/sdk

bold.api.base.url=http://10.0.2.2:8000/v1/
bold.ws.base.url=ws://10.0.2.2:8000/v1/stream/market
bold.data.mode=mock
bold.free.tier.override=true
bold.enable.http.logging=true
```
> [!NOTE]
> When testing on an **Android Emulator**, use `10.0.2.2` instead of `localhost` to connect to services running on your development host machine. When testing on a **physical device over Wi-Fi**, substitute your computer's local IP address (e.g., `http://192.168.1.50:8000/v1/`).

---

## 4. Database Setup & Seeding (PostgreSQL 16+ & Redis)

The platform requires **PostgreSQL 16+** with the `pgcrypto` extension for AES-256 key encryption, and **Redis 7+** for the sub-millisecond emergency kill switch.

### Option A: Using Docker (Recommended for Quick Local Setup)

If Docker Desktop is installed and running:
```bash
# 1. Start PostgreSQL with pgcrypto
docker run -d --name bold-postgres \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=bold_platform \
  -p 5432:5432 \
  postgres:16-alpine

# 2. Start Redis Cache
docker run -d --name bold-redis \
  -p 6379:6379 \
  redis:7-alpine
```

### Option B: Using Native Local PostgreSQL

#### Windows (PowerShell):
1. Install PostgreSQL via the official installer from [enterprisedb.com](https://www.enterprisedb.com/downloads/postgres-postgresql-downloads).
2. Open PowerShell and create the database:
```powershell
& "C:\Program Files\PostgreSQL\16\bin\createdb.exe" -U postgres bold_platform
```

#### macOS (via Homebrew):
```bash
brew install postgresql@16 redis
brew services start postgresql@16
brew services start redis
createdb -U postgres bold_platform
```

#### Linux (Ubuntu/Debian):
```bash
sudo apt update && sudo apt install -y postgresql-16 redis-server
sudo systemctl start postgresql redis-server
sudo -u postgres createdb bold_platform
```

---

### Step 4.2: Run Database Migrations & Initial Seed Data

Connect to PostgreSQL and apply the schema definitions from [`DATABASE.md`](file:///d:/trading%20app/admin-panel/DATABASE.md):

**Execute via `psql` command line:**
```bash
psql -U postgres -d bold_platform -c "CREATE EXTENSION IF NOT EXISTS pgcrypto;"
```

Apply the migration tables in the required order:
1. `admin_users` and `api_keys_vault`
2. `feature_flags` and `strategy_reviews`
3. `feed_health_logs` and `audit_events`
4. `users`, `workspaces`, and `instruments`
5. `paper_accounts`, `paper_orders`, and `ledger_entries`
6. `watchlists`, `strategies`, `signals`, and `alerts`

**Verify Database Tables:**
```sql
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
```
*Expected output: lists 14 tables including `api_keys_vault`, `paper_accounts`, `instruments`, and `feature_flags`.*

---

## 5. Running the Project Locally

Follow these steps to boot up all components.

### 5.1 Start the Admin Console (Web)
```bash
cd admin-panel
npm run dev
```
* **Local Web URL:** `http://localhost:5173`
* **Verification:** Open your browser to `http://localhost:5173`. You should see the BOLD dark-themed interface (`#0B0F14` background) prompting for admin login.

---

### 5.2 Start the Backend REST & WebSocket Server (if running mock API)
From the repository root or backend service directory:
```bash
# Windows
uvicorn app.main:app --reload --port 8000

# macOS / Linux
python3 -m uvicorn app.main:app --reload --port 8000
```
* **API Documentation (Swagger UI):** `http://localhost:8000/docs`
* **Health Check URL:** `http://localhost:8000/v1/market/status`

---

### 5.3 Launch the Android Mobile Application

#### Using Android Studio (Recommended):
1. In Android Studio, ensure an Android Virtual Device (AVD) is running (e.g., **Pixel 8 or Pixel 9 with API 34**).
2. Select `app` in the run configurations dropdown.
3. Click the green **Run ▶** button (`Shift + F10`).

#### Using the Terminal:

**Windows (PowerShell):**
```powershell
cd mobile-application
.\gradlew.bat assembleDebug
.\gradlew.bat installDebug
```

**macOS / Linux:**
```bash
cd mobile-application
./gradlew assembleDebug
./gradlew installDebug
```

* **Verification:** The BOLD splash screen (`S01`) appears on your emulator with the watermarked banner: `● ILLUSTRATIVE / Live quote not connected`.

---

## 6. Testing

### 6.1 Admin Panel Tests

```bash
cd admin-panel

# Run unit and component test suites (Vitest)
npm test

# Run a single test file
npx vitest run src/features/keys/components/KeyListTable.test.tsx

# Run ESLint code quality checks
npm run lint
```

### 6.2 Mobile Application Tests

**Windows (PowerShell):**
```powershell
cd mobile-application

# Run all unit tests across all feature modules
.\gradlew.bat test

# Run a specific module's unit tests (e.g., Paper Trading)
.\gradlew.bat :feature:papertrading:test

# Run Android Lint analysis
.\gradlew.bat lintDebug

# Run Compose UI instrumentation tests (requires active emulator/device)
.\gradlew.bat connectedAndroidTest
```

**macOS / Linux:**
```bash
cd mobile-application
./gradlew test
./gradlew :feature:papertrading:test
./gradlew lintDebug
./gradlew connectedAndroidTest
```

---

## 7. Troubleshooting (Common Issues & Practical Fixes)

| # | Issue / Error Symptom | Cause | Practical Fix |
| :- | :--- | :--- | :--- |
| **1** | `'adb' is not recognized as an internal or external command` | Android SDK `platform-tools` is not added to the system `PATH`. | **Windows:** Add `C:\Users\<YourUser>\AppData\Local\Android\Sdk\platform-tools` to your User Environment Variables `PATH`. Restart your terminal.<br>**macOS/Linux:** Add `export PATH=$PATH:$HOME/Library/Android/sdk/platform-tools` to `~/.zshrc` or `~/.bashrc`. |
| **2** | `Unsupported class file major version` or Gradle JDK incompatibility | Active JDK in your terminal does not match the JDK configured in Android Studio (e.g., using Java 8 or an unaligned early preview). | Open Android Studio $\rightarrow$ **Settings** $\rightarrow$ **Build, Execution, Deployment** $\rightarrow$ **Build Tools** $\rightarrow$ **Gradle** $\rightarrow$ Set **Gradle JDK** to **Embedded JDK 17** or **OpenJDK 21 (Temurin)**. |
| **3** | `'docker' is not recognized as a cmdlet or program` | Docker Desktop is not installed or the Docker CLI is not in your system `PATH`. | Install Docker Desktop from [docker.com](https://www.docker.com/). If you do not wish to use Docker, use native PostgreSQL 16+ as outlined in [Section 4, Option B](#option-b-using-native-local-postgresql). |
| **4** | `ECONNREFUSED 127.0.0.1:5432` (PostgreSQL Connection Failed) | The PostgreSQL service is not running or is bound to a different port. | **Windows:** Open `services.msc`, locate `postgresql-x64-16`, and click **Start**.<br>**macOS:** Run `brew services restart postgresql@16`.<br>**Linux:** Run `sudo systemctl restart postgresql`. |
| **5** | `Port 5173 or 8000 already in use` (`EADDRINUSE`) | Another process (e.g., previous server instance) is occupying the port. | **Windows:** Identify and terminate process: `netstat -ano \| findstr :5173` then `taskkill /PID <PID> /F`.<br>**macOS/Linux:** `lsof -ti:5173 \| xargs kill -9`. Alternatively, start Vite on another port: `npm run dev -- --port 5174`. |
| **6** | `Failed to install the following Android SDK packages as some licences have not been accepted` | Android SDK licenses need explicit CLI acceptance. | Run the SDK license agreement command in your terminal:<br>**Windows:** `& "$env:LOCALAPPDATA\Android\Sdk\cmdline-tools\latest\bin\sdkmanager.bat" --licenses`<br>**macOS/Linux:** `$HOME/Library/Android/sdk/cmdline-tools/latest/bin/sdkmanager --licenses` (Press `y` to accept all). |
| **7** | `java.net.ConnectException: Failed to connect to localhost:8000` from Android Emulator | Android emulators run in an isolated virtual network; `localhost` inside the emulator refers to the emulator itself, not your PC. | In `mobile-application/local.properties`, change `localhost` to `10.0.2.2` (the special emulator alias for host loopback): `bold.api.base.url=http://10.0.2.2:8000/v1/`. |
| **8** | Node.js `EPERM: operation not permitted, mkdir ...` during `npm install` on Windows | Antivirus file lock or temporary sandbox file permission issue. | Run PowerShell as Administrator, or clean the npm cache and rerun: `npm cache clean --force` followed by `npm install`. |
| **9** | `CLEARTEXT communication to 10.0.2.2 not permitted by network security policy` | Android 9+ blocks unencrypted HTTP requests by default. | Ensure `mobile-application/app/src/main/res/xml/network_security_config.xml` includes cleartext traffic permission for `10.0.2.2` and `localhost` during debug builds. |

---

## 8. Definition of Done for a Fresh Developer Setup

Your local setup is 100% verified when:
1. `npm run dev` in `admin-panel/` opens `http://localhost:5173` without console errors.
2. `./gradlew assembleDebug` in `mobile-application/` compiles cleanly with code 0.
3. The Android emulator renders the BOLD Home Dashboard (`S04`) and allows opening a candlestick chart (`S07`).
4. Submitting a test paper order on Screen `S14` successfully calculates simulated fees and updates virtual cash.
