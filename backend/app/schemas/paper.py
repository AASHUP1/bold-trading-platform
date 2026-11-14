from pydantic import BaseModel, Field
from typing import List, Optional
from decimal import Decimal

class CreatePaperOrder(BaseModel):
    instrument_id: str
    symbol: str
    direction: str = Field(..., pattern="^(BUY|SELL)$")
    order_type: str = Field(..., pattern="^(MARKET|LIMIT|SL_MARKET|SL_LIMIT)$")
    quantity: int = Field(..., gt=0)
    requested_price: Optional[Decimal] = None
    trigger_price: Optional[Decimal] = None
    stop_loss: Optional[Decimal] = None
    target: Optional[Decimal] = None
    product_type: str = "MIS"

class PaperOrderResponse(BaseModel):
    id: str
    client_order_id: str
    symbol: str
    direction: str
    order_type: str
    quantity: int
    filled_quantity: int
    average_fill_price: Optional[Decimal] = None
    status: str
    rejection_reason: Optional[str] = None
    placed_at: str
    filled_at: Optional[str] = None

class PaperPositionResponse(BaseModel):
    id: str
    symbol: str
    net_quantity: int
    average_buy_price: Decimal
    average_sell_price: Decimal
    realized_pnl: Decimal
    unrealized_pnl: Decimal
    last_price: Decimal

class LedgerResponse(BaseModel):
    id: str
    entry_type: str
    amount: Decimal
    balance_after: Decimal
    reference_id: Optional[str] = None
    description: str
    created_at: str

class PaperAccountResponse(BaseModel):
    id: str
    virtual_capital: Decimal
    available_cash: Decimal
    margin_used: Decimal
    realized_pnl: Decimal
    unrealized_pnl: Decimal
    reset_count: int
