from fastapi import APIRouter, WebSocket, WebSocketDisconnect
import asyncio
import json
import random
from datetime import datetime, timezone

router = APIRouter(tags=["Real-Time WebSocket Market Stream"])

PRICES = {
    "NIFTY 50": 24360.50,
    "BANKNIFTY": 51820.00,
    "RELIANCE": 2980.20,
    "HDFCBANK": 1640.75,
    "INFY": 1865.40,
    "TCS": 4120.00,
    "TATAMOTORS": 940.10
}

@router.websocket("/v1/stream/market")
async def websocket_market_stream(websocket: WebSocket):
    await websocket.accept()
    subscribed_symbols = set(PRICES.keys())

    async def receiver():
        nonlocal subscribed_symbols
        try:
            while True:
                data_text = await websocket.receive_text()
                msg = json.loads(data_text)
                if msg.get("action") == "subscribe":
                    instruments = msg.get("instruments", [])
                    if instruments:
                        subscribed_symbols = set(instruments)
        except Exception:
            pass

    receiver_task = asyncio.create_task(receiver())

    try:
        while True:
            await asyncio.sleep(1.5) # 1.5s institutional tick interval
            # Randomly tick 1-3 subscribed instruments
            targets = random.sample(list(subscribed_symbols), min(3, len(subscribed_symbols)))
            for sym in targets:
                if sym in PRICES:
                    delta_pct = (random.random() - 0.49) * 0.0015
                    new_p = round(PRICES[sym] * (1.0 + delta_pct), 2)
                    PRICES[sym] = new_p
                    tick_payload = {
                        "event": "market.tick",
                        "symbol": sym,
                        "last_price": new_p,
                        "exchange_timestamp": datetime.now(timezone.utc).isoformat(),
                        "data_mode": "delayed"
                    }
                    await websocket.send_text(json.dumps(tick_payload))
    except (WebSocketDisconnect, asyncio.CancelledError):
        pass
    finally:
        receiver_task.cancel()
