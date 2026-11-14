# Master Model Context Protocol (MCP) Specification: BOLD Monorepo Platform
**Document:** MCP.md  
**Product:** BOLD Trading & Research Platform (Monorepo)  
**Parent Specifications:** `PRD.md`, `ARCHITECTURE.md`, `DATABASE.md`, `API.md`, `AUTH_SECURITY.md`  
**Security Standard:** Principle of Least Privilege & Zero Live Order Capability  

---

## 1. Overview & Architectural Role of MCP

The **Model Context Protocol (MCP)** provides open, standardized interfaces through which AI assistants inspect project source code, query development databases, test Web UI renders, and observe Android emulator logs while strictly enforcing security, financial precision, and physical isolation boundaries.

---

## 2. Server Inventory & Responsibilities

| MCP Server | Implementation | Responsibility in BOLD |
| :--- | :--- | :--- |
| **PostgreSQL Server** | `@modelcontextprotocol/server-postgres` | Inspect database schemas, verify paper ledger reconciliations, and audit test logs. |
| **Filesystem Server** | `@modelcontextprotocol/server-filesystem` | Read, create, and refactor code strictly within `D:\trading app\`. |
| **Browser Server** | `@modelcontextprotocol/server-puppeteer` | Automate visual verification of React Admin Console (Screen S21, charts, modals). |
| **Android ADB Server**| Local ADB CLI / Python MCP Bridge | Inspect connected emulators, stream Logcat crash logs, and capture Compose UI screenshots. |
| **GitHub Server** | `@modelcontextprotocol/server-github` | Review pull requests, branch differences, and commit hygiene against `AGENTS.md`. |
| **Docs Fetch Server** | `@modelcontextprotocol/server-fetch` | Query official Jetpack Compose and React documentation without external web pollution. |

---

## 3. Master MCP Configuration Snippet

Add this JSON block to your AI client configuration (e.g., `mcp_servers.json`, `claude_desktop_config.json`, or Antigravity IDE settings):

```json
{
  "mcpServers": {
    "bold-monorepo-filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "D:\\trading app"
      ]
    },
    "bold-postgres": {
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
    "bold-admin-browser": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-puppeteer"
      ],
      "env": {
        "PUPPETEER_HEADLESS": "true"
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
    "bold-github": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-github"
      ],
      "env": {
        "GITHUB_PERSONAL_ACCESS_TOKEN": "${GITHUB_PAT}"
      }
    }
  }
}
```

---

## 4. Allowed vs. Blocked Tool Operations (Least Privilege Matrix)

| Server | Allowed Operations | Permanently Blocked Operations |
| :--- | :--- | :--- |
| **PostgreSQL** | `SELECT` on public tables, schema inspection (`list_tables`, `describe_table`), query planning. | `DROP`, `TRUNCATE`, modifying `audit_events`, querying raw `encrypted_payload` secrets. |
| **Filesystem** | Reading/editing files within `admin-panel/` and `mobile-application/`. | Traversal outside `D:\trading app\`, modifying root OS directories, reading private keystores. |
| **Browser** | Navigating to `http://localhost:5173`, capturing S21 screenshots, UI testing. | Submitting real payment cards, bypassing anti-bot challenges or CAPTCHAs. |
| **Android ADB**| `adb devices`, `adb logcat`, capturing Compose screenshots, installing debug APKs. | `adb root`, `adb shell rm -rf`, wiping emulator partitions, flashing firmware. |
| **GitHub** | Checking commit history, diffing branches, opening pull requests. | Force pushing (`git push -f`), deleting protected branches (`main`). |

---

## 5. Safety & Permission Rules

1. **Read-Only by Default:** The AI assistant must prioritize reading specifications and existing code before proposing modifications.
2. **Absolute Paper Trading Isolation:** The AI assistant is **strictly blocked** from writing any tool or code that routes simulated paper orders to live broker order gateways.
3. **Zero Hardcoded Secrets:** All secrets must use environment variable placeholders (`${POSTGRES_PASSWORD}`); plain-text credentials must never be written to code or committed.
4. **Mandatory Confirmation for Destructive Actions:**
   * Modifying database schemas requires developer approval and an updated `DATABASE.md`.
   * Resetting database state or wiping local caches requires explicit user confirmation.
5. **Decimal Safety Enforcement:** Binary floating point (`Float`/`Double`) is forbidden for financial amounts; the AI assistant must enforce `BigDecimal` / `NUMERIC(15, 2)`.

---

## 6. How to Test MCP Connections

1. **Test Filesystem Connection:** Request directory listing of `admin-panel/` $\rightarrow$ verifies file access.
2. **Test PostgreSQL Connection:** Run `SELECT count(*) FROM instruments;` $\rightarrow$ verifies database read access.
3. **Test Browser Connection:** Navigate to `http://localhost:5173` and capture screenshot $\rightarrow$ verifies Puppeteer rendering.
4. **Test Android ADB Connection:** Execute `adb devices` $\rightarrow$ verifies attached emulator presence.

---

## 7. Troubleshooting Common MCP Connection Errors

| Issue / Error | Probable Cause | Practical Fix |
| :--- | :--- | :--- |
| `Connection refused: localhost:5432` | PostgreSQL is not running. | Verify PostgreSQL service is active: `netstat -ano \| findstr :5432`. |
| `Path traversal denied: path outside boundary` | Path traverses outside `D:\trading app`. | Verify all file paths use absolute or relative paths rooted strictly inside `D:\trading app`. |
| `'adb' is not recognized` | Android SDK `platform-tools` is not in PATH. | Add `C:\Users\<User>\AppData\Local\Android\Sdk\platform-tools` to system PATH and restart MCP client. |
| `Puppeteer failed to launch: Chromium not found` | Chromium binary missing. | Run `npx puppeteer browsers install chrome` in the terminal. |
| `GitHub 401 Bad Credentials` | Expired or invalid GitHub Personal Access Token. | Generate a new classic PAT with `repo` scope and update the `${GITHUB_PAT}` environment variable. |

---

## 8. Subproject MCP Guides

* **[`admin-panel/MCP.md`](file:///d:/trading%20app/admin-panel/MCP.md):** Focuses on React testing, Puppeteer automation for Screen S21 charts, and PostgreSQL key vault inspection.
* **[`mobile-application/MCP.md`](file:///d:/trading%20app/mobile-application/MCP.md):** Focuses on Android ADB emulator bridge, Jetpack Compose UI verification, Room SQLite queries, and physical paper trading isolation.
