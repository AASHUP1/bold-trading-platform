-- Migration 006: Admin Control Room, Encrypted Key Vault, Audit Trail & Feed Health
-- Follows admin-panel/DATABASE.md & PRD.md Section 5

CREATE TABLE IF NOT EXISTS admin_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) NOT NULL UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'Ops Admin' CHECK (role IN ('Super Admin', 'Ops Admin', 'Compliance Officer', 'Risk Manager')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS api_keys_vault (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    provider VARCHAR(50) NOT NULL UNIQUE, -- 'Zerodha Kite', 'Upstox', 'Angel One', 'Dhan HQ', 'Fyers'
    key_category VARCHAR(30) NOT NULL DEFAULT 'BROKER_FEED',
    masked_value VARCHAR(50) NOT NULL,
    encrypted_payload BYTEA NOT NULL, -- Encrypted using AES-256-GCM via pgcrypto/KMS
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'PENDING', 'EXPIRED', 'REVOKED')),
    updated_by UUID NULL REFERENCES admin_users(id),
    last_tested_at TIMESTAMPTZ NULL,
    last_rotated_at TIMESTAMPTZ NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feature_flags (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    flag_key VARCHAR(50) NOT NULL UNIQUE,
    is_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    description TEXT NOT NULL,
    updated_by UUID NULL REFERENCES admin_users(id),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS feed_health_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    adapter_id VARCHAR(50) NOT NULL,
    latency_ms INT NOT NULL,
    error_rate NUMERIC(5, 4) NOT NULL DEFAULT 0.0000,
    status VARCHAR(20) NOT NULL DEFAULT 'HEALTHY',
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tamper-Evident Append-Only Audit Trail
CREATE TABLE IF NOT EXISTS audit_events (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    event_hash VARCHAR(64) NOT NULL UNIQUE,
    actor_id UUID NULL REFERENCES admin_users(id),
    actor_email VARCHAR(255) NOT NULL,
    action_category VARCHAR(50) NOT NULL,
    target_resource VARCHAR(100) NOT NULL,
    state_before JSONB NULL,
    state_after JSONB NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);
