from sqlalchemy import Column, String, Boolean, Integer, Numeric, DateTime, ForeignKey, Text, JSON, LargeBinary
from datetime import datetime, timezone
import uuid
from app.core.database import Base

def gen_uuid():
    return str(uuid.uuid4())

class AdminUser(Base):
    __tablename__ = "admin_users"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    email = Column(String(255), unique=True, nullable=False, index=True)
    full_name = Column(String(100), nullable=False)
    role = Column(String(30), nullable=False, default="Ops Admin")
    is_active = Column(Boolean, nullable=False, default=True)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

class ApiKeyVault(Base):
    __tablename__ = "api_keys_vault"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    provider = Column(String(50), unique=True, nullable=False)
    key_category = Column(String(30), nullable=False, default="BROKER_FEED")
    masked_value = Column(String(50), nullable=False)
    encrypted_payload = Column(LargeBinary, nullable=False)
    status = Column(String(20), nullable=False, default="ACTIVE")
    updated_by = Column(String(36), ForeignKey("admin_users.id"), nullable=True)
    last_tested_at = Column(DateTime(timezone=True), nullable=True)
    last_rotated_at = Column(DateTime(timezone=True), nullable=True)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

class FeatureFlag(Base):
    __tablename__ = "feature_flags"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    flag_key = Column(String(50), unique=True, nullable=False, index=True)
    is_enabled = Column(Boolean, nullable=False, default=False)
    description = Column(Text, nullable=False)
    updated_by = Column(String(36), ForeignKey("admin_users.id"), nullable=True)
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

class FeedHealthLog(Base):
    __tablename__ = "feed_health_logs"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    adapter_id = Column(String(50), nullable=False, index=True)
    latency_ms = Column(Integer, nullable=False)
    error_rate = Column(Numeric(5, 4), nullable=False, default=0.0000)
    status = Column(String(20), nullable=False, default="HEALTHY")
    recorded_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)

class AuditEvent(Base):
    __tablename__ = "audit_events"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    event_hash = Column(String(64), unique=True, nullable=False, index=True)
    actor_id = Column(String(36), ForeignKey("admin_users.id"), nullable=True)
    actor_email = Column(String(255), nullable=False)
    action_category = Column(String(50), nullable=False)
    target_resource = Column(String(100), nullable=False)
    state_before = Column(JSON, nullable=True)
    state_after = Column(JSON, nullable=True)
    ip_address = Column(String(45), nullable=True)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)
