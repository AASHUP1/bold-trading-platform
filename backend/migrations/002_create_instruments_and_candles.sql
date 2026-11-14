-- Migration 002: Instruments and Market Candles
-- Follows DATABASE.md Section 3.2 & API.md Section 2.2

CREATE TABLE IF NOT EXISTS instruments (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    exchange VARCHAR(10) NOT NULL CHECK (exchange IN ('NSE', 'BSE', 'NFO')),
    symbol VARCHAR(50) NOT NULL,
    name VARCHAR(255) NOT NULL,
    instrument_type VARCHAR(20) NOT NULL CHECK (instrument_type IN ('equity', 'index', 'future', 'option')),
    tick_size NUMERIC(10, 4) NOT NULL DEFAULT 0.0500,
    lot_size INT NOT NULL DEFAULT 1,
    strike_price NUMERIC(15, 2) NULL,
    option_type VARCHAR(5) NULL CHECK (option_type IN ('CE', 'PE') OR option_type IS NULL),
    expiry_date DATE NULL,
    underlying_symbol VARCHAR(50) NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_exchange_symbol UNIQUE (exchange, symbol)
);

CREATE TABLE IF NOT EXISTS market_candles (
    id BIGSERIAL PRIMARY KEY,
    instrument_id UUID NOT NULL REFERENCES instruments(id) ON DELETE CASCADE,
    timeframe VARCHAR(10) NOT NULL CHECK (timeframe IN ('1m', '5m', '15m', '1H', '1D')),
    open_price NUMERIC(15, 2) NOT NULL,
    high_price NUMERIC(15, 2) NOT NULL,
    low_price NUMERIC(15, 2) NOT NULL,
    close_price NUMERIC(15, 2) NOT NULL,
    volume BIGINT NOT NULL DEFAULT 0,
    vwap NUMERIC(15, 2) NULL,
    open_interest BIGINT NULL,
    exchange_timestamp TIMESTAMPTZ NOT NULL,
    received_timestamp TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_market_candles_inst_tf_ts 
ON market_candles(instrument_id, timeframe, exchange_timestamp DESC);
