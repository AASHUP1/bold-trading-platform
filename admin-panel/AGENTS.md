# AI Coding Agent Rulebook: BOLD Admin Panel

## 1. Project Summary
BOLD Admin Panel is a high-precision, zero-hosting-cost web console built with React 18+ and TypeScript, deployed on Firebase Hosting. It acts as the mission-control center for the BOLD trading platform to monitor system telemetry, manage feed health, and approve quant strategies. Crucially, it provides a centralized vault to rotate and test all third-party API keys without code redeployments. At launch, it enforces a global free-tier policy ensuring all mobile features are accessible at zero cost.

## 2. Source-of-Truth Hierarchy (Strict Priority Order)
1. `PRD.md` - Core features, user personas, MVP scope, free-tier mandate.
2. `DESIGN.md` - Strict design tokens (#0B0F14, #121923, #38D996), typography, component states.
3. `ARCHITECTURE.md` - Multi-module folder structure, Vite setup, TanStack Query, Zustand.
4. `DATABASE.md` - PostgreSQL schema, AES-256 encrypted vault, append-only audit events, RLS.
5. `API.md` - Endpoints, standard error envelope, pagination, rate limits.

## 3. DO Rules
- **Follow Folder Structure:** Place code strictly in `src/features/`, `src/components/ui/`, or `src/lib/`.
- **Reuse Existing Components:** Consume shared Shadcn/Radix components; never build duplicate buttons or inputs.
- **Small Focused Functions:** Keep functions under 40 lines with a single, clear responsibility.
- **Comment Complex Logic:** Document secret encryption, HMAC hashing, and kill-switch state transitions.
- **Comprehensive UI States:** Every view must handle `loading` (skeletons), `error` (toast/alert), and `empty` states.

## 4. DO NOT Rules
- **DO NOT Change Tech Stack:** React + Vite + Tailwind + Firebase Hosting is locked.
- **DO NOT Add New Libraries:** Do not install npm packages without explicit user approval.
- **DO NOT Alter Database Silently:** Never change table structures without updating `DATABASE.md`.
- **DO NOT Rewrite Working Code:** Make surgical, targeted edits. Do not refactor unrelated files.
- **DO NOT Hardcode Secrets:** Never hardcode API keys, secrets, or Firebase private keys in source code.

## 5. Workflow & Conflict Resolution
1. **Plan First:** State what files will be created or modified before writing code.
2. **Execute:** Implement minimal, tested, type-safe code following the design tokens.
3. **Summarize:** Provide a concise summary of exact changes and verification results.
- **Conflict Rule:** If a user request conflicts with any source-of-truth document, **STOP and ask for clarification** before writing any code.
