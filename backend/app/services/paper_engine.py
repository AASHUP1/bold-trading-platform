from decimal import Decimal, ROUND_HALF_UP
from typing import Tuple, Dict
import uuid

class PaperExecutionService:

    FLAT_BROKERAGE = Decimal("20.00")
    STT_RATE = Decimal("0.00025") # 0.025% for intraday equity
    GST_RATE = Decimal("0.18") # 18% on brokerage

    @classmethod
    def calculate_charges(cls, order_value: Decimal) -> Dict[str, Decimal]:
        brokerage = cls.FLAT_BROKERAGE
        stt = (order_value * cls.STT_RATE).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)
        gst = (brokerage * cls.GST_RATE).quantize(Decimal("0.01"), rounding=ROUND_HALF_UP)
        total_charges = brokerage + stt + gst
        return {
            "brokerage": brokerage,
            "stt": stt,
            "gst": gst,
            "total_charges": total_charges
        }

    @classmethod
    def execute_market_order(
        cls,
        direction: str,
        price: Decimal,
        quantity: int,
        current_cash: Decimal
    ) -> Tuple[Decimal, Decimal, str]:
        order_value = price * Decimal(quantity)
        charges = cls.calculate_charges(order_value)
        total_charges = charges["total_charges"]

        if direction == "BUY":
            cash_delta = -(order_value + total_charges)
        else:
            cash_delta = order_value - total_charges

        new_cash = current_cash + cash_delta
        receipt_id = f"SIM-{uuid.uuid4().hex[:8].upper()}"
        return new_cash, total_charges, receipt_id
