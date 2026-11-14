# Model Context Protocol (MCP) Configuration: Admin & Compliance Console
**Document:** MCP.md  
**Product:** BOLD Control Room (Admin Panel)  
**Target:** React 18+ (Vite, TypeScript, Tailwind CSS, Firebase Hosting)  
**Source of Truth:** [`admin-panel/PRD.md`](file:///d:/trading%20app/admin-panel/PRD.md), [`admin-panel/ARCHITECTURE.md`](file:///d:/trading%20app/admin-panel/ARCHITECTURE.md), and [`admin-panel/AUTH_SECURITY.md`](file:///d:/trading%20app/admin-panel/AUTH_SECURITY.md)  

---

## 1. Overview & Purpose of MCP in BOLD Admin

The **Model Context Protocol (MCP)** provides a secure, standardized interface allowing AI assistants to query the local database, inspect filesystem structures, test browser UI renders, and review Git history under strict, auditable permission boundaries.

### Core Safety Mandate
* **Principle of Least Privilege:** AI assistants are granted read-only access by default. Destructive actions (dropping database tables, modifying production keys, forcing Git pushes) are strictly blocked.
* **Zero Credential Exposure:** Real API keys, database passwords, and Firebase private keys are NEVER stored in MCP configuration files. All sensitive values use environment variable placeholders (`${POSTGRES_PASSWORD}`).

---

## 2. Recommended MCP Servers

| MCP Server | Package / Command | Primary Purpose |
| :--- | :--- | :--- |
| **PostgreSQL Server** | `@modelcontextprotocol/server-postgres` | Inspect table schemas (`api_keys_vault`, `strategy_reviews`, `audit_events`) and verify migration scripts. |
| **Filesystem Server** | `@modelcontextprotocol/server-filesystem` | Read and write component source code, Tailwind configurations, and unit tests strictly within `admin-panel/`. |
| **Puppeteer / Browser** | `@modelcontextprotocol/server-puppeteer` | Automate headful/headless browser verification of Screen S21 charts, key rotation modals, and WCAG contrast. |
| **GitHub Server** | `@modelcontextprotocol/server-github` | Review pull requests, check diffs, and inspect commit messages against `AGENTS.md` guidelines. |

---

## 3. Configuration Snippets

Add the following block to your MCP client configuration file (e.g., `.gemini/mcp_servers.json`, `claude_desktop_config.json`, or Antigravity IDE settings):

```json
{
  "mcpServers": {
    "bold-admin-postgres": {
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
    "bold-admin-filesystem": {
      "command": "npx",
      "args": [
        "-y",
        "@modelcontextprotocol/server-filesystem",
        "D:\\trading app\\admin-panel"
      ]
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

## 4. Allowed vs. Blocked Tool Operations

### 4.1 PostgreSQL MCP Server
* **Allowed Tools:**
  * `read_query`: Execute read-only `SELECT` queries on `feed_health_logs`, `feature_flags`, and `strategy_reviews`.
  * `list_tables`: List public tables to verify migration completion.
  * `describe_table`: Inspect column types and constraints.
* **Blocked Tools & Actions:**
  * `DROP TABLE`, `DROP DATABASE`, `TRUNCATE`: **Permanently Blocked**.
  * `DELETE`, `UPDATE` on `audit_events`: **Permanently Blocked** (tamper-evident immutable ledger).
  * `SELECT encrypted_payload FROM api_keys_vault`: **Blocked** (prevents raw secret leakage into LLM context).

### 4.2 Filesystem MCP Server
* **Allowed Tools:**
  * `read_file`, `write_file`, `edit_file`, `list_directory` strictly within `D:\trading app\admin-panel`.
* **Blocked Tools & Actions:**
  * Reading or writing outside the workspace directory (`C:\Windows`, `C:\Users\...\AppData`, root drives).
  * Direct deletion of repository root files (`package.json`, `tsconfig.json`).

### 4.3 Browser / Puppeteer MCP Server
* **Allowed Tools:**
  * `navigate`: Navigate to local dev server `http://localhost:5173`.
  * `screenshot`: Capture render states of Screen S21 metric cards and latency area charts.
  * `click`, `fill`: Test form validation in `RotateKeyDialog.tsx` and `KillSwitchModal.tsx`.
* **Blocked Tools & Actions:**
  * Entering real production credit card details or live broker passwords.
  * Attempting to solve or bypass CAPTCHAs or Cloudflare anti-bot gates.

---

## 5. Permission & Safety Rules

1. **Read-Only by Default:** The AI assistant must always start with read-only inspection tools before suggesting or executing modifications.
2. **Explicit Confirmation for Destructive Actions:**
   * Modifying database schemas requires explicit developer confirmation and an updated `DATABASE.md`.
   * Re-deploying Firebase Hosting or modifying `firebase.json` headers requires confirmation.
3. **No Secret Ingestion:** The AI assistant must reject reading raw `.env` files containing production secrets. Only `.env.example` templates may be processed.

---

## 6. Testing MCP Connections

### 6.1 Testing PostgreSQL MCP
Run a query through the MCP tool:
```sql
SELECT table_name FROM information_schema.tables WHERE table_schema = 'public';
```
* **Expected Result:** Returns list of tables including `api_keys_vault` and `feed_health_logs`.

### 6.2 Testing Filesystem MCP
Request a directory listing of `admin-panel/src`:
* **Expected Result:** Successfully returns `app/`, `components/`, `features/`, `lib/`, and `types/`.

### 6.3 Testing Browser MCP
Instruct the browser tool to navigate to `http://localhost:5173` and capture a screenshot:
* **Expected Result:** Captures visual proof of the BOLD dark login card (`#121923` surface) with zero console errors.

---

## 7. Troubleshooting Common MCP Connection Errors

| Issue / Error | Probable Cause | Practical Fix |
| :--- | :--- | :--- |
| `ECONNREFUSED 127.0.0.1:5432` | PostgreSQL container/service is stopped. | Verify PostgreSQL is running: `docker ps` or check Windows service `postgresql-x64-16`. |
| `Failed to spawn npx command` | Node.js / npx is not in system PATH. | Ensure Node.js 20+ is in PATH: verify with `node -v` and `npx -v`. |
| `Path is outside the allowed directory` | Filesystem server restricted path boundary. | In `mcpServers.bold-admin-filesystem.args`, verify the path points to `D:\trading app\admin-panel`. |
| `Browser failed to launch: Chromium revision not found` | Puppeteer failed to download bundled browser. | Run `npx puppeteer browsers install chrome` in your terminal. |
| `GitHub 401 Unauthorized` | Invalid or expired GitHub Personal Access Token. | Generate a new classic PAT with `repo` scope and update the `GITHUB_PAT` environment variable. |
