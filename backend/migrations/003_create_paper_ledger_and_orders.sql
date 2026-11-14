-- Migration 003: Paper Accounts, Paper Orders, Positions & Immutable Double-Entry Ledger
-- Follows DATABASE.md Section 3.3 & PRD.md Section 5.3

CREATE TABLE IF NOT EXISTS paper_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    virtual_capital NUMERIC(15, 2) NOT NULL DEFAULT 1000000.00, -- ₹10,00,000 Starting Virtual Capital
    available_cash NUMERIC(15, 2) NOT NULL DEFAULT 1000000.00,
    margin_used NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    realized_pnl NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    unrealized_pnl NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    reset_count INT NOT NULL DEFAULT 0,
    last_reset_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_paper_account_balances CHECK (available_cash >= 0 AND margin_used >= 0)
);

CREATE TABLE IF NOT EXISTS paper_orders (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    paper_account_id UUID NOT NULL REFERENCES paper_accounts(id) ON DELETE CASCADE,
    instrument_id UUID NOT NULL REFERENCES instruments(id),
    client_order_id VARCHAR(50) NOT NULL UNIQUE, -- Idempotency key from mobile app
    direction VARCHAR(4) NOT NULL CHECK (direction IN ('BUY', 'SELL')),
    order_type VARCHAR(10) NOT NULL CHECK (order_type IN ('MARKET', 'LIMIT', 'SL_MARKET', 'SL_LIMIT')),
    product_type VARCHAR(10) NOT NULL DEFAULT 'MIS' CHECK (product_type IN ('MIS', 'CNC', 'NRML')),
    quantity INT NOT NULL CHECK (quantity > 0),
    requested_price NUMERIC(15, 2) NULL,
    trigger_price NUMERIC(15, 2) NULL,
    stop_loss NUMERIC(15, 2) NULL,
    target NUMERIC(15, 2) NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'FILLED', 'REJECTED', 'CANCELLED')),
    rejection_reason TEXT NULL,
    filled_quantity INT NOT NULL DEFAULT 0,
    average_fill_price NUMERIC(15, 2) NULL,
    placed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    filled_at TIMESTAMPTZ NULL
);

CREATE TABLE IF NOT EXISTS paper_positions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    paper_account_id UUID NOT NULL REFERENCES paper_accounts(id) ON DELETE CASCADE,
    instrument_id UUID NOT NULL REFERENCES instruments(id),
    symbol VARCHAR(50) NOT NULL,
    net_quantity INT NOT NULL,
    average_buy_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    average_sell_price NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    realized_pnl NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    unrealized_pnl NUMERIC(15, 2) NOT NULL DEFAULT 0.00,
    last_price NUMERIC(15, 2) NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_paper_account_instrument UNIQUE (paper_account_id, instrument_id)
);

-- Immutable Append-Only Ledger
CREATE TABLE IF NOT EXISTS ledger_entries (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    paper_account_id UUID NOT NULL REFERENCES paper_accounts(id) ON DELETE CASCADE,
    entry_type VARCHAR(30) NOT NULL CHECK (entry_type IN ('INITIAL_DEPOSIT', 'BUY_FILL', 'SELL_FILL', 'BROKERAGE_FEE', 'STATUTORY_TAX', 'ACCOUNT_RESET')),
    amount NUMERIC(15, 2) NOT NULL,
    balance_after NUMERIC(15, 2) NOT NULL,
    reference_id VARCHAR(100) NULL, -- Corresponds to paper_orders.client_order_id or fill ID
    description TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
