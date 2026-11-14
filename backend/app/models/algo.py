from sqlalchemy import Column, String, Integer, Numeric, DateTime, ForeignKey, Text, JSON
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
import uuid
from app.core.database import Base

def gen_uuid():
    return str(uuid.uuid4())

class Strategy(Base):
    __tablename__ = "strategies"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), nullable=False, index=True)
    name = Column(String(100), nullable=False)
    description = Column(Text, nullable=True)
    status = Column(String(20), nullable=False, default="draft") # draft, submitted, approved, rejected
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

    versions = relationship("StrategyVersion", back_populates="strategy", cascade="all, delete-orphan")

class StrategyVersion(Base):
    __tablename__ = "strategy_versions"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    strategy_id = Column(String(36), ForeignKey("strategies.id", ondelete="CASCADE"), nullable=False, index=True)
    version_number = Column(Integer, nullable=False, default=1)
    ast_json = Column(JSON, nullable=False)
    timeframe = Column(String(10), nullable=False, default="15m")
    target_percent = Column(Numeric(5, 2), nullable=False, default=2.00)
    stop_loss_percent = Column(Numeric(5, 2), nullable=False, default=1.00)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))

    strategy = relationship("Strategy", back_populates="versions")
    signals = relationship("Signal", back_populates="version", cascade="all, delete-orphan")

class Signal(Base):
    __tablename__ = "signals"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    strategy_version_id = Column(String(36), ForeignKey("strategy_versions.id", ondelete="CASCADE"), nullable=False, index=True)
    instrument_id = Column(String(36), ForeignKey("instruments.id"), nullable=False)
    symbol = Column(String(50), nullable=False, index=True)
    direction = Column(String(4), nullable=False) # BUY, SELL
    entry_min = Column(Numeric(15, 2), nullable=False)
    entry_max = Column(Numeric(15, 2), nullable=False)
    stop_loss = Column(Numeric(15, 2), nullable=False)
    target_1 = Column(Numeric(15, 2), nullable=False)
    target_2 = Column(Numeric(15, 2), nullable=False)
    target_3 = Column(Numeric(15, 2), nullable=False)
    confidence_percent = Column(Integer, nullable=False)
    status = Column(String(20), nullable=False, default="ACTIVE") # ACTIVE, TRIGGERED, EXPIRED, STOPPED_OUT
    triggered_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)
    expires_at = Column(DateTime(timezone=True), nullable=True)

    version = relationship("StrategyVersion", back_populates="signals")
