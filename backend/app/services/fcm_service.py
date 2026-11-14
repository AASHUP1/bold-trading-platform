from app.core.firebase import send_fcm_notification
from typing import Optional
from decimal import Decimal

class FCMNotificationService:

    @classmethod
    async def notify_order_filled(
        cls,
        fcm_token: Optional[str],
        symbol: str,
        direction: str,
        quantity: int,
        fill_price: Decimal,
        receipt_id: str
    ):
        if not fcm_token:
            return
        title = f"Order Executed: {direction} {quantity}x {symbol}"
        body = f"Simulated order {receipt_id} filled at ₹{fill_price:,.2f}. Margin updated."
        data = {
            "type": "ORDER_FILLED",
            "symbol": symbol,
            "receipt_id": receipt_id,
            "direction": direction
        }
        await send_fcm_notification(token=fcm_token, title=title, body=body, data=data)

    @classmethod
    async def notify_risk_alert(
        cls,
        fcm_token: Optional[str],
        message: str
    ):
        if not fcm_token:
            return
        title = "⚠️ Risk Limit Alert"
        data = {"type": "RISK_ALERT"}
        await send_fcm_notification(token=fcm_token, title=title, body=message, data=data)

    @classmethod
    async def notify_signal_alert(
        cls,
        fcm_token: Optional[str],
        symbol: str,
        direction: str,
        strategy_name: str,
        entry_price: Decimal
    ):
        if not fcm_token:
            return
        title = f"New Signal: {direction} {symbol}"
        body = f"{strategy_name} triggered entry at ₹{entry_price:,.2f}."
        data = {
            "type": "SIGNAL_TRIGGERED",
            "symbol": symbol,
            "strategy": strategy_name
        }
        await send_fcm_notification(token=fcm_token, title=title, body=body, data=data)
