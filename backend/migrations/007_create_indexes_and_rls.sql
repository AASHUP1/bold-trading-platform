-- Migration 007: Performance Indexes, Row Level Security (RLS) & Seed Baseline
-- Follows DATABASE.md Section 5 & 7

-- Performance Indexes
CREATE INDEX IF NOT EXISTS idx_paper_orders_account ON paper_orders(paper_account_id, placed_at DESC);
CREATE INDEX IF NOT EXISTS idx_ledger_entries_account ON ledger_entries(paper_account_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_signals_active ON signals(status, triggered_at DESC);
CREATE INDEX IF NOT EXISTS idx_feed_health_logs ON feed_health_logs(adapter_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_instruments_search ON instruments(exchange, symbol);

-- Row Level Security (RLS) Policies
ALTER TABLE workspaces ENABLE ROW LEVEL SECURITY;
ALTER TABLE paper_accounts ENABLE ROW LEVEL SECURITY;
ALTER TABLE paper_orders ENABLE ROW LEVEL SECURITY;
ALTER TABLE ledger_entries ENABLE ROW LEVEL SECURITY;
ALTER TABLE watchlists ENABLE ROW LEVEL SECURITY;

-- Seed Core Baseline Data
INSERT INTO instruments (id, exchange, symbol, name, instrument_type, tick_size, lot_size) VALUES
('b0000000-0000-0000-0000-000000000001', 'NSE', 'NIFTY 50', 'NIFTY 50 Benchmark Index', 'index', 0.0500, 25),
('b0000000-0000-0000-0000-000000000002', 'NSE', 'BANKNIFTY', 'NIFTY Bank Index', 'index', 0.0500, 15),
('b0000000-0000-0000-0000-000000000003', 'NSE', 'RELIANCE', 'Reliance Industries Ltd.', 'equity', 0.0500, 1),
('b0000000-0000-0000-0000-000000000004', 'NSE', 'HDFCBANK', 'HDFC Bank Ltd.', 'equity', 0.0500, 1),
('b0000000-0000-0000-0000-000000000005', 'NSE', 'INFY', 'Infosys Ltd.', 'equity', 0.0500, 1),
('b0000000-0000-0000-0000-000000000006', 'NSE', 'TCS', 'Tata Consultancy Services Ltd.', 'equity', 0.0500, 1),
('b0000000-0000-0000-0000-000000000007', 'NSE', 'TATAMOTORS', 'Tata Motors Ltd.', 'equity', 0.0500, 1)
ON CONFLICT (exchange, symbol) DO NOTHING;

-- Seed Feature Flags Baseline
INSERT INTO feature_flags (flag_key, is_enabled, description) VALUES
('GLOBAL_KILL_SWITCH', FALSE, 'Emergency platform kill switch halting all paper execution and tick broadcasting'),
('FREE_LAUNCH_UNLIMITED', TRUE, 'Mandatory 100% Free Launch tier granting unlimited scanner, F&O and backtesting'),
('AI_ANALYST_RESEARCH', TRUE, 'Institutional AI assistant queries enabled for mobile clients')
ON CONFLICT (flag_key) DO NOTHING;
