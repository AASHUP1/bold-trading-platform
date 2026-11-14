-- Migration 001: Users and Workspaces
-- Follows DATABASE.md Section 3.1 & PRD.md Section 5.1

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(255) NULL UNIQUE,
    full_name VARCHAR(100) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'suspended', 'deactivated')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS workspaces (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    trading_universe VARCHAR(30) NOT NULL DEFAULT 'NSE_CASH_FNO' CHECK (trading_universe IN ('NSE_CASH', 'NSE_FNO', 'NSE_CASH_FNO', 'BSE_ALL')),
    ui_density VARCHAR(20) NOT NULL DEFAULT 'intermediate' CHECK (ui_density IN ('compact', 'intermediate', 'expanded')),
    risk_per_trade_percent NUMERIC(5, 2) NOT NULL DEFAULT 1.00 CHECK (risk_per_trade_percent > 0 AND risk_per_trade_percent <= 5.00),
    daily_loss_limit_percent NUMERIC(5, 2) NOT NULL DEFAULT 2.00 CHECK (daily_loss_limit_percent > 0 AND daily_loss_limit_percent <= 10.00),
    fcm_device_token TEXT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
