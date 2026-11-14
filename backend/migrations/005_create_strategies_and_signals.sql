-- Migration 005: Quant Strategies, Versions, Backtests & Signals Stream
-- Follows DATABASE.md Section 3.5 & Screens S12, S13, S15

CREATE TABLE IF NOT EXISTS strategies (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    description TEXT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'draft' CHECK (status IN ('draft', 'submitted', 'approved', 'rejected')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS strategy_versions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    strategy_id UUID NOT NULL REFERENCES strategies(id) ON DELETE CASCADE,
    version_number INT NOT NULL DEFAULT 1,
    ast_json JSONB NOT NULL, -- Condition tree (EMA, RSI, VWAP)
    timeframe VARCHAR(10) NOT NULL DEFAULT '15m',
    target_percent NUMERIC(5, 2) NOT NULL DEFAULT 2.00,
    stop_loss_percent NUMERIC(5, 2) NOT NULL DEFAULT 1.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_strategy_version UNIQUE (strategy_id, version_number)
);

CREATE TABLE IF NOT EXISTS signals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    strategy_version_id UUID NOT NULL REFERENCES strategy_versions(id) ON DELETE CASCADE,
    instrument_id UUID NOT NULL REFERENCES instruments(id),
    symbol VARCHAR(50) NOT NULL,
    direction VARCHAR(4) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
    entry_min NUMERIC(15, 2) NOT NULL,
    entry_max NUMERIC(15, 2) NOT NULL,
    stop_loss NUMERIC(15, 2) NOT NULL,
    target_1 NUMERIC(15, 2) NOT NULL,
    target_2 NUMERIC(15, 2) NOT NULL,
    target_3 NUMERIC(15, 2) NOT NULL,
    confidence_percent INT NOT NULL CHECK (confidence_percent >= 0 AND confidence_percent <= 100),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'TRIGGERED', 'EXPIRED', 'STOPPED_OUT')),
    triggered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NULL
);
