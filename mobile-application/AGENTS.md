# AI Coding Agent Rulebook: BOLD Mobile Application

## 1. Project Summary
BOLD Mobile is a native Android institutional trading and research platform built with Kotlin and Jetpack Compose. It provides real-time market discovery, technical charting with indicators, an F&O options desk with Greeks, and an algo strategy builder with backtesting. It features a realistic paper trading terminal and double-entry virtual ledger initialized with ₹10,00,000 virtual capital. Crucially, all features are 100% free at initial launch, and simulated orders are strictly isolated with zero live broker execution.

## 2. Source-of-Truth Hierarchy (Strict Priority Order)
1. `PRD.md` - Functional requirements, risk engine thresholds, and 100% free launch model.
2. `DESIGN.md` - Compose design tokens (#0B0F14, #38D996, #FF657A), tabular fonts (`tnum`), 48dp targets.
3. `ARCHITECTURE.md` - Multi-module architecture (`core:*`, `feature:*`), UDF / MVI pattern, Hilt DI.
4. `DATABASE.md` - PostgreSQL paper ledger schemas, Room SQLite local cache, and zero-float monetary rules.
5. `API.md` - REST endpoints, WebSocket event contracts, idempotency keys, error envelope.

## 3. DO Rules
- **Follow Multi-Module Structure:** Keep feature logic inside `:feature:<name>` and shared code in `:core:<name>`.
- **Reuse Design Primitives:** Use `BoldTheme.colors`, `BoldButton`, and design tokens from `core:designsystem`.
- **Enforce Unidirectional Data Flow:** State flows down via `StateFlow<UiState>`; events flow up via `UiEvent`.
- **Tabular Numerals Everywhere:** Always apply `FontFeatureSettings("tnum")` on prices, quantities, and P&L.
- **Handle Every State:** Always provide explicit Composable states for Loading, Error, Offline, and Stale data.
- **Strict Money Precision:** Use `BigDecimal` or integer minor paise units. Never use `Float` or `Double` for money.

## 4. DO NOT Rules
- **DO NOT Route Live Orders:** The paper trading engine must NEVER place live orders to broker APIs.
- **DO NOT Change Tech Stack:** Kotlin + Jetpack Compose + Hilt + Retrofit/Room is locked.
- **DO NOT Add Dependencies:** Do not add Gradle dependencies or plugins without explicit user confirmation.
- **DO NOT Alter Schemas Silently:** Never modify Room entities or backend models without updating `DATABASE.md`.
- **DO NOT Delete Working Code:** Preserve existing working features; perform precise, targeted updates only.
- **DO NOT Hardcode Secrets:** Never embed API keys or broker tokens in APK source files.

## 5. Workflow & Conflict Resolution
1. **Plan First:** Outline proposed architectural changes and file modifications before coding.
2. **Execute:** Write clean, type-safe, idiomatic Kotlin code complying with Compose best practices.
3. **Summarize:** Provide a concise summary of changes and how they adhere to specifications.
- **Conflict Rule:** If a prompt conflicts with any source-of-truth document, **STOP and ask for clarification** before proceeding.
