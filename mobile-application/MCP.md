# Model Context Protocol (MCP) Configuration: Mobile Application
**Document:** MCP.md  
**Product:** BOLD Android Trading & Research Platform  
**Target Platform:** Native Android (Kotlin, Jetpack Compose, Material 3, Android 10+ / API 29+)  
**Source of Truth:** [`mobile-application/PRD.md`](file:///d:/trading%20app/mobile-application/PRD.md), [`mobile-application/ARCHITECTURE.md`](file:///d:/trading%20app/mobile-application/ARCHITECTURE.md), and [`mobile-application/AUTH_SECURITY.md`](file:///d:/trading%20app/mobile-application/AUTH_SECURITY.md)  

---

## 1. Overview & Purpose of MCP in BOLD Mobile

The **Model Context Protocol (MCP)** enables AI coding assistants to interact with the Android project structure, inspect Gradle build logic, review Room SQLite local caches, inspect device emulator logs (Logcat), and verify Jetpack Compose UI rendering within explicit safety guardrails.

### Core Safety Mandate
* **Zero Live Execution Boundary:** The AI assistant is strictly forbidden from generating or executing code that connects the simulated paper trading engine to live broker order gateways.
* **Least Privilege:** Tools operate in read-only mode by default; file modifications require atomic review; real secrets or keystore credentials must never enter the model context.

---

## 2. Recommended MCP Servers

| MCP Server | Package / Command | Primary Purpose |
| :--- | :--- | :--- |
| **Filesystem Server** | `@modelcontextprotocol/server-filesystem` | Read, create, and edit Kotlin multi-module source code strictly within `mobile-application/`. |
| **Android ADB / Device Server** | `adb-mcp-server` (or local ADB CLI bridge) | Inspect connected emulators, capture Compose UI screenshots, and stream Logcat crash logs. |
| **PostgreSQL Server** | `@modelcontextprotocol/server-postgres` | Verify paper trading ledger entries (`paper_accounts`, `ledger_entries`) and instrument contract masters. |
| **Docs & Reference Server** | `@modelcontextprotocol/server-fetch` | Query official Android developer documentation and Jetpack Compose API references without web noise. |

---

## 3. Configuration Snippets

Add the following configuration to your MCP client (e.g., `.gemini/mcp_servers.json`, `claude_desktop_config.json`, or Antigravity IDE configuration):

```json
{
  "mcpServers": {
    "bold-mobile-filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "D:\\trading app\\mobile-application"
      ]
    },
    "bold-mobile-postgres": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-postgres",
        "postgresql://${POSTGRES_USER}:${POSTGRES_PASSWORD}@${POSTGRES_HOST}:${POSTGRES_PORT}/${POSTGRES_DB}"
      ],
      "env": {
        "POSTGRES_USER": "postgres",
        "POSTGRES_PASSWORD": "${POSTGRES_PASSWORD}",
        "POSTGRES_HOST": "localhost",
        "POSTGRES_PORT": "5432",
        "POSTGRES_DB": "bold_platform"
      }
    },
    "bold-android-adb": {
      "command": "python",
      "args": [
        "-m",
        "adb_mcp_bridge"
      ],
      "env": {
        "ANDROID_HOME": "C:\\Users\\Hp\\AppData\\Local\\Android\\Sdk",
        "ADB_PATH": "C:\\Users\\Hp\\AppData\\Local\\Android\\Sdk\\platform-tools\\adb.exe"
      }
    },
    "bold-docs-fetcher": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-fetch"
      ]
    }
  }
}
```

---

## 4. Allowed vs. Blocked Tool Operations

### 4.1 Android ADB & Device Tools
* **Allowed Tools:**
  * `adb_devices`: List connected physical devices and running emulators.
  * `adb_logcat_grep`: Stream filtered Logcat logs tagged with `BOLD_LOG` or `AndroidRuntime` for crash diagnosis.
  * `adb_screencap`: Capture screenshot of the running emulator to verify visual token accuracy (`#0B0F14`).
  * `adb_install`: Install debug builds (`app-debug.apk`) for automated UI testing.
* **Blocked Tools & Actions:**
  * `adb root` / `adb remount`: **Permanently Blocked** (violates zero-privilege boundary).
  * `adb shell rm -rf`: **Permanently Blocked**.
  * `adb shell wipe data`: **Blocked** without explicit user confirmation.

### 4.2 Filesystem & Codebase Tools
* **Allowed Tools:**
  * Create and edit Kotlin files in `:core:*` and `:feature:*`.
  * Read `local.properties.example` and Gradle configurations.
* **Blocked Tools & Actions:**
  * Modifying `build.gradle.kts` to add unapproved third-party dependencies.
  * Creating files outside `D:\trading app\mobile-application`.
  * Reading private signing keys (`release.keystore`, `key.properties`).

### 4.3 Database & Paper Trading Tools
* **Allowed Tools:**
  * Querying simulated account balances in `paper_accounts` and historical `market_candles`.
* **Blocked Tools & Actions:**
  * **Zero Broker Write Rule:** Calling any endpoint or tool capable of placing live broker orders.
  * Modifying Room database entities without simultaneously updating `DATABASE.md`.

---

## 5. Permission & Safety Rules

1. **Strict Sandboxing:** Filesystem operations are strictly confined to `mobile-application/`.
2. **Decimal Safety Enforcement:** The AI assistant must reject or refactor any code introducing binary floating-point numbers (`Float`, `Double`) for monetary calculations, substituting `BigDecimal` or integer minor paise.
3. **No Unapproved Dependencies:** The assistant must not add new dependencies to `libs.versions.toml` or `build.gradle.kts` without explicit user permission.

---

## 6. Testing MCP Connections

### 6.1 Testing Android ADB MCP
Run an emulator query through the MCP tool:
```bash
adb devices
```
* **Expected Result:** Returns attached emulator (e.g., `emulator-5554 device`).

### 6.2 Testing Filesystem MCP
Request a file listing of `mobile-application/core/designsystem`:
* **Expected Result:** Returns `Color.kt`, `Typography.kt`, `Theme.kt`, and `components/`.

### 6.3 Testing PostgreSQL MCP
Query the paper trading ledger:
```sql
SELECT virtual_capital, available_cash FROM paper_accounts LIMIT 1;
```
* **Expected Result:** Returns initialized virtual balance: `1000000.00` (₹10,00,000).

---

## 7. Troubleshooting Common MCP Connection Errors

| Issue / Error | Probable Cause | Practical Fix |
| :--- | :--- | :--- |
| `adb: command not found` | Android SDK `platform-tools` is missing from system `PATH`. | Add `C:\Users\<User>\AppData\Local\Android\Sdk\platform-tools` to system PATH and restart MCP client. |
| `No devices/emulators found` | Android Emulator has not been booted in Android Studio. | Open Android Studio $\rightarrow$ Device Manager $\rightarrow$ Click **Play ▶** on your AVD (e.g., Pixel 9 API 34). |
| `Path traversal denied` | Attempted file read outside `mobile-application/`. | Verify file paths use absolute or relative paths rooted strictly inside `D:\trading app\mobile-application`. |
| `Postgres password authentication failed` | Incorrect or missing `${POSTGRES_PASSWORD}` environment variable. | Ensure `POSTGRES_PASSWORD` is set in your user environment or shell profile before starting the MCP client. |
| `Fetch tool blocked external URL` | Attempted to query an unauthorized origin. | Only official developer domains (`developer.android.com`, `kotlinlang.org`) are permitted for documentation lookups. |
