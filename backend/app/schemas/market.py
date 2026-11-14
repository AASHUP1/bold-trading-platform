from pydantic import BaseModel
from typing import List, Optional
from datetime import datetime
from decimal import Decimal

class InstrumentResponse(BaseModel):
    id: str
    exchange: str
    symbol: str
    name: str
    instrument_type: str
    tick_size: Decimal
    lot_size: int
    last_price: Decimal
    change: Decimal
    change_percent: Decimal

class CandleItem(BaseModel):
    open: Decimal
    high: Decimal
    low: Decimal
    close: Decimal
    volume: int
    vwap: Optional[Decimal] = None
    timestamp: str

class CandleResponse(BaseModel):
    instrument_id: str
    symbol: str
    timeframe: str
    data_mode: str = "live"
    candles: List[CandleItem]

class OptionGreek(BaseModel):
    ltp: Decimal
    oi: int
    oi_change: float
    iv: float
    delta: float
    gamma: float
    theta: float
    vega: float

class OptionStrikeRow(BaseModel):
    strike: Decimal
    call: OptionGreek
    put: OptionGreek

class OptionChainResponse(BaseModel):
    underlying: str
    underlying_price: Decimal
    pcr: float
    atm_strike: Decimal
    strikes: List[OptionStrikeRow]
