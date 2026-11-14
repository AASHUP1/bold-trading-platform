from pydantic import BaseModel
from typing import List, Optional, Any
from decimal import Decimal

class BacktestRequest(BaseModel):
    strategy_name: str
    timeframe: str = "15m"
    rules: List[dict]
    target_percent: float = 2.0
    stop_loss_percent: float = 1.0

class BacktestResponse(BaseModel):
    strategy_name: str
    win_rate: float
    profit_factor: float
    max_drawdown: float
    total_trades: int
    winning_trades: int
    losing_trades: int
    final_equity: float
    equity_curve: List[float]

class SignalResponse(BaseModel):
    id: str
    symbol: str
    direction: str
    strategy_name: str
    entry_min: Decimal
    entry_max: Decimal
    stop_loss: Decimal
    target_1: Decimal
    target_2: Decimal
    target_3: Decimal
    confidence_percent: int
    status: str
    timestamp_text: str
