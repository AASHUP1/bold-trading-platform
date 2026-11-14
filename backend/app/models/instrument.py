from sqlalchemy import Column, String, Boolean, Numeric, DateTime, BigInteger, ForeignKey, Date, Index
from datetime import datetime, timezone
import uuid
from app.core.database import Base

def gen_uuid():
    return str(uuid.uuid4())

class Instrument(Base):
    __tablename__ = "instruments"

    id = Column(String(36), primary_key=True, default=gen_uuid)
    exchange = Column(String(10), nullable=False) # NSE, BSE, NFO
    symbol = Column(String(50), nullable=False, unique=True, index=True)
    name = Column(String(255), nullable=False)
    instrument_type = Column(String(20), nullable=False) # equity, index, future, option
    tick_size = Column(Numeric(10, 4), nullable=False, default=0.0500)
    lot_size = Column(BigInteger, nullable=False, default=1)
    strike_price = Column(Numeric(15, 2), nullable=True)
    option_type = Column(String(5), nullable=True) # CE, PE
    expiry_date = Column(Date, nullable=True)
    underlying_symbol = Column(String(50), nullable=True)
    is_active = Column(Boolean, nullable=False, default=True)
    created_at = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))

class MarketCandle(Base):
    __tablename__ = "market_candles"

    id = Column(BigInteger, primary_key=True, autoincrement=True)
    instrument_id = Column(String(36), ForeignKey("instruments.id", ondelete="CASCADE"), nullable=False, index=True)
    timeframe = Column(String(10), nullable=False) # 1m, 5m, 15m, 1H, 1D
    open_price = Column(Numeric(15, 2), nullable=False)
    high_price = Column(Numeric(15, 2), nullable=False)
    low_price = Column(Numeric(15, 2), nullable=False)
    close_price = Column(Numeric(15, 2), nullable=False)
    volume = Column(BigInteger, nullable=False, default=0)
    vwap = Column(Numeric(15, 2), nullable=True)
    open_interest = Column(BigInteger, nullable=True)
    exchange_timestamp = Column(DateTime(timezone=True), nullable=False, index=True)
    received_timestamp = Column(DateTime(timezone=True), default=lambda: datetime.now(timezone.utc))

    __table_args__ = (
        Index("idx_candles_inst_tf_ts", "instrument_id", "timeframe", "exchange_timestamp"),
    )
