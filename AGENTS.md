# AI Coding Agent Rulebook: BOLD Trading Platform Monorepo

## 1. Project Summary
BOLD is an institutional-grade Indian equities and derivatives trading, research, and paper-trading platform. It consists of a native Android application (Kotlin, Jetpack Compose) and a web-based Admin & Compliance Console (React, Firebase Hosting). The platform features real-time charts, options analytics, an algo strategy lab, and a simulated paper ledger initialized with ₹10,00,000 virtual capital. All features are 100% free at launch, with complete isolation between paper trading and read-only broker connections.

## 2. Source-of-Truth Hierarchy (Strict Priority Order)
1. `PRD.md` (Product Requirements: functional scope, user personas, MVP boundaries, free launch mandate)
2. `DESIGN.md` (UI Design System: strict hex tokens, typography, component states, screen layouts)
3. `ARCHITECTURE.md` (System Architecture: folder structures, state management, data flow, tech stack)
4. `DATABASE.md` (Database Design: PostgreSQL schemas, Room SQLite cache, append-only ledgers, RLS)
5. `API.md` (API Specification: REST endpoints, WebSocket contracts, error envelopes, versioning)

## 3. DO Rules
- **Respect Boundaries:** Mobile code stays in `mobile-application/`; Admin web code stays in `admin-panel/`.
- **Follow Defined Structures:** Adhere strictly to the directory trees in each subproject's `ARCHITECTURE.md`.
- **Reuse Existing Code:** Reuse shared UI components and design tokens; do not create duplicate variants.
- **Write Small, Focused Functions:** Maintain single-responsibility functions with clear type signatures.
- **Document Complex Logic:** Add concise comments for financial calculations, risk formulas, and cryptographic operations.
- **Handle All States:** Every feature must handle loading (skeletons), error (alerts), empty, and stale data states.
- **Decimal Safety:** Always use decimal-safe arithmetic (`BigDecimal` / `NUMERIC(15, 2)`) for monetary amounts.

## 4. DO NOT Rules
- **DO NOT Change Tech Stack:** Admin is React + Vite + Firebase Hosting; Mobile is Kotlin + Jetpack Compose.
- **DO NOT Add Libraries:** Never install npm or Gradle dependencies without prior user approval.
- **DO NOT Alter Schemas Silently:** Never modify database tables or Room entities without updating `DATABASE.md`.
- **DO NOT Rewrite Working Code:** Make surgical, targeted edits. Never delete working features unless asked.
- **DO NOT Hardcode Secrets:** Never embed API keys, OAuth secrets, or credentials in client code.
- **DO NOT Place Live Orders:** The paper trading engine must NEVER connect to live broker order gateways.

## 5. Workflow & Conflict Resolution
1. **Plan First:** Outline the exact plan and affected files before writing any code.
2. **Execute Cleanly:** Write clean, minimal, production-grade code adhering to the design and architecture docs.
3. **Summarize:** Conclude every task with a concise summary of changes and verification steps.
- **Conflict Rule:** If any user instruction conflicts with a source-of-truth file, **STOP and ask the user** before proceeding.
