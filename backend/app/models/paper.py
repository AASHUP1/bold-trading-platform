from sqlalchemy import Column, String, Integer, Numeric, DateTime, ForeignKey, Text, Index
from sqlalchemy.orm import relationship
from datetime import datetime, timezone
import uuid
from app.core.database import Base

def gen_uuid():
    return str(uuid.uuid4())

class PaperAccount(Base):
    __tablename__ = "paper_accounts"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    user_id = Column(String(36), ForeignKey("users.id", ondelete="CASCADE"), unique=True, nullable=False)
    virtual_capital = Column(Numeric(15, 2), nullable=False, default=1000000.00)
    available_cash = Column(Numeric(15, 2), nullable=False, default=1000000.00)
    margin_used = Column(Numeric(15, 2), nullable=False, default=0.00)
    realized_pnl = Column(Numeric(15, 2), nullable=False, default=0.00)
    unrealized_pnl = Column(Numeric(15, 2), nullable=False, default=0.00)
    reset_count = Column(Integer, nullable=False, default=0)
    last_reset_at = Column(DateTime(timezone=True), nullable=True)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), onupdate=lambda: datetime.now(timezone.utc))

    user = relationship("User", back_populates="paper_account")
    orders = relationship("PaperOrder", back_populates="account", cascade="all, delete-orphan")
    positions = relationship("PaperPosition", back_populates="account", cascade="all, delete-orphan")
    ledger_entries = relationship("LedgerEntry", back_populates="account", cascade="all, delete-orphan")

class PaperOrder(Base):
    __tablename__ = "paper_orders"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    paper_account_id = Column(String(36), ForeignKey("paper_accounts.id", ondelete="CASCADE"), nullable=False, index=True)
    instrument_id = Column(String(36), ForeignKey("instruments.id"), nullable=False)
    client_order_id = Column(String(50), unique=True, nullable=False, index=True)
    direction = Column(String(4), nullable=False) # BUY, SELL
    order_type = Column(String(10), nullable=False) # MARKET, LIMIT, SL_MARKET, SL_LIMIT
    product_type = Column(String(10), nullable=False, default="MIS") # MIS, CNC, NRML
    quantity = Column(Integer, nullable=False)
    requested_price = Column(Numeric(15, 2), nullable=True)
    trigger_price = Column(Numeric(15, 2), nullable=True)
    stop_loss = Column(Numeric(15, 2), nullable=True)
    target = Column(Numeric(15, 2), nullable=True)
    status = Column(String(20), nullable=False, default="PENDING") # PENDING, FILLED, REJECTED, CANCELLED
    rejection_reason = Column(Text, nullable=True)
    filled_quantity = Column(Integer, nullable=False, default=0)
    average_fill_price = Column(Numeric(15, 2), nullable=True)
    placed_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)
    filled_at = Column(DateTime(timezone=True), nullable=True)

    account = relationship("PaperAccount", back_populates="orders")
    instrument = relationship("Instrument")

class PaperPosition(Base):
    __tablename__ = "paper_positions"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    paper_account_id = Column(String(36), ForeignKey("paper_accounts.id", ondelete="CASCADE"), nullable=False, index=True)
    instrument_id = Column(String(36), ForeignKey("instruments.id"), nullable=False)
    symbol = Column(String(50), nullable=False)
    net_quantity = Column(Integer, nullable=False)
    average_buy_price = Column(Numeric(15, 2), nullable=False, default=0.00)
    average_sell_price = Column(Numeric(15, 2), nullable=False, default=0.00)
    realized_pnl = Column(Numeric(15, 2), nullable=False, default=0.00)
    unrealized_pnl = Column(Numeric(15, 2), nullable=False, default=0.00)
    last_price = Column(Numeric(15, 2), nullable=False)
    updated_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))

    account = relationship("PaperAccount", back_populates="positions")

class LedgerEntry(Base):
    __tablename__ = "ledger_entries"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    paper_account_id = Column(String(36), ForeignKey("paper_accounts.id", ondelete="CASCADE"), nullable=False, index=True)
    entry_type = Column(String(30), nullable=False) # INITIAL_DEPOSIT, BUY_FILL, SELL_FILL, BROKERAGE_FEE, STATUTORY_TAX, ACCOUNT_RESET
    amount = Column(Numeric(15, 2), nullable=False)
    balance_after = Column(Numeric(15, 2), nullable=False)
    reference_id = Column(String(100), nullable=True)
    description = Column(Text, nullable=False)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc), index=True)

    account = relationship("PaperAccount", back_populates="ledger_entries")
