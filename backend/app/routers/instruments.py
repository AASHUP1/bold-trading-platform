from fastapi import APIRouter, Depends, Query, HTTPException
from sqlalchemy.ext.asyncio import AsyncSession
from sqlalchemy import select
from app.core.database import get_db
from app.models.instrument import Instrument
from app.schemas.market import InstrumentResponse, CandleResponse, CandleItem, OptionChainResponse, OptionStrikeRow, OptionGreek
from decimal import Decimal
import math
from datetime import datetime, timezone, timedelta

router = APIRouter(prefix="/v1", tags=["Market Data & Instruments"])

INITIAL_ASSETS = [
    {"exchange": "NSE", "symbol": "NIFTY 50", "name": "NIFTY 50 Benchmark Index", "type": "index", "lot": 25, "price": Decimal("24360.50"), "change": Decimal("156.80"), "pct": Decimal("0.65")},
    {"exchange": "NSE", "symbol": "BANKNIFTY", "name": "NIFTY Bank Index", "type": "index", "lot": 15, "price": Decimal("51820.00"), "change": Decimal("218.40"), "pct": Decimal("0.42")},
    {"exchange": "NSE", "symbol": "RELIANCE", "name": "Reliance Industries Ltd", "type": "equity", "lot": 1, "price": Decimal("2980.20"), "change": Decimal("33.80"), "pct": Decimal("1.15")},
    {"exchange": "NSE", "symbol": "HDFCBANK", "name": "HDFC Bank Ltd", "type": "equity", "lot": 1, "price": Decimal("1640.75"), "change": Decimal("-4.95"), "pct": Decimal("-0.30")},
    {"exchange": "NSE", "symbol": "INFY", "name": "Infosys Ltd", "type": "equity", "lot": 1, "price": Decimal("1865.40"), "change": Decimal("15.70"), "pct": Decimal("0.85")},
    {"exchange": "NSE", "symbol": "TCS", "name": "Tata Consultancy Services Ltd", "type": "equity", "lot": 1, "price": Decimal("4120.00"), "change": Decimal("20.50"), "pct": Decimal("0.50")},
    {"exchange": "NSE", "symbol": "TATAMOTORS", "name": "Tata Motors Ltd", "type": "equity", "lot": 1, "price": Decimal("940.10"), "change": Decimal("19.30"), "pct": Decimal("2.10")}
]

@router.get("/instruments/search", response_model=list[InstrumentResponse])
async def search_instruments(
    q: str = Query("", description="Symbol or name query"),
    exchange: str = Query("NSE", description="Exchange filter")
):
    results = []
    q_upper = q.upper().strip()
    for asset in INITIAL_ASSETS:
        if not q_upper or q_upper in asset["symbol"] or q_upper in asset["name"].upper():
            results.append(
                InstrumentResponse(
                    id=f"inst-{asset['symbol'].lower().replace(' ', '')}",
                    exchange=asset["exchange"],
                    symbol=asset["symbol"],
                    name=asset["name"],
                    instrument_type=asset["type"],
                    tick_size=Decimal("0.0500"),
                    lot_size=asset["lot"],
                    last_price=asset["price"],
                    change=asset["change"],
                    change_percent=asset["pct"]
                )
            )
    return results

@router.get("/market/candles", response_model=CandleResponse)
async def get_candles(
    symbol: str = Query("NIFTY 50", description="Trading symbol"),
    timeframe: str = Query("15m", description="Timeframe 1m, 5m, 15m, 1H, 1D"),
    limit: int = Query(100, ge=10, le=250)
):
    base_price = Decimal("24250.00")
    for a in INITIAL_ASSETS:
        if a["symbol"] == symbol:
            base_price = a["price"]
            break

    candles = []
    now = datetime.now(timezone.utc)
    interval_minutes = 15 if timeframe == "15m" else (1 if timeframe == "1m" else (60 if timeframe == "1H" else 1440))
    current_p = base_price - Decimal("120.00")

    for i in range(limit, 0, -1):
        ts = now - timedelta(minutes=i * interval_minutes)
        op = current_p
        hi = op + Decimal("15.50")
        lo = op - Decimal("12.20")
        cl = op + Decimal("6.40")
        current_p = cl
        candles.append(
            CandleItem(
                open=op,
                high=hi,
                low=lo,
                close=cl,
                volume=450000 + (i * 2100),
                vwap=op + Decimal("2.50"),
                timestamp=ts.isoformat()
            )
        )

    return CandleResponse(
        instrument_id=f"inst-{symbol.lower().replace(' ', '')}",
        symbol=symbol,
        timeframe=timeframe,
        data_mode="delayed",
        candles=candles
    )

@router.get("/market/option-chain", response_model=OptionChainResponse)
async def get_option_chain(
    underlying: str = Query("NIFTY 50", description="Underlying symbol"),
    expiry: str = Query("2026-11-26", description="Option expiry date")
):
    spot_price = Decimal("24836.30")
    atm_strike = (spot_price / Decimal("50.00")).quantize(Decimal("1")) * Decimal("50.00")

    strikes = []
    for offset in range(-10, 11):
        strike_val = atm_strike + Decimal(offset * 50)
        dist = float(strike_val - spot_price)

        call_ltp = max(5.0, 150.0 - (dist * 0.45))
        put_ltp = max(5.0, 150.0 + (dist * 0.45))
        delta = 0.50 - (dist / 1000.0)

        strikes.append(
            OptionStrikeRow(
                strike=strike_val,
                call=OptionGreek(
                    ltp=Decimal(f"{call_ltp:.2f}"),
                    oi=6840000 - abs(offset * 120000),
                    oi_change=8.2 - (offset * 0.4),
                    iv=14.8,
                    delta=max(0.01, min(0.99, delta)),
                    gamma=0.0018,
                    theta=-8.4,
                    vega=12.6
                ),
                put=OptionGreek(
                    ltp=Decimal(f"{put_ltp:.2f}"),
                    oi=6210000 - abs(offset * 110000),
                    oi_change=-3.1 + (offset * 0.3),
                    iv=15.2,
                    delta=max(-0.99, min(-0.01, delta - 1.0)),
                    gamma=0.0018,
                    theta=-8.1,
                    vega=12.4
                )
            )
        )

    return OptionChainResponse(
        underlying=underlying,
        underlying_price=spot_price,
        pcr=1.12,
        atm_strike=atm_strike,
        strikes=strikes
    )
