from decimal import Decimal
from typing import Tuple

class RiskEngineService:

    MAX_TRADE_RISK_PERCENT = Decimal("0.01") # 1.0%
    MAX_DAILY_LOSS_PERCENT = Decimal("0.02") # 2.0%

    @classmethod
    def validate_order(
        cls,
        virtual_capital: Decimal,
        available_cash: Decimal,
        daily_realized_loss: Decimal,
        price: Decimal,
        quantity: int,
        stop_loss: Decimal = None
    ) -> Tuple[bool, str]:
        """
        Server-side institutional risk rules enforcement:
        1. Daily circuit breaker: Rejects if daily realized losses exceed 2.0% of virtual capital.
        2. Per-trade risk budget: Rejects if loss at stop_loss exceeds 1.0% of virtual capital.
        3. Sufficient margin check.
        """
        # Rule 1: Daily Circuit Breaker
        max_daily_loss = virtual_capital * cls.MAX_DAILY_LOSS_PERCENT
        if daily_realized_loss < -max_daily_loss:
            return False, f"Daily circuit breaker breached: Realized losses exceed maximum 2.0% (₹{max_daily_loss:,.2f}). Trading locked until next session."

        # Rule 2: Margin Availability
        order_value = price * Decimal(quantity)
        if order_value > available_cash:
            return False, f"Insufficient margin: Required ₹{order_value:,.2f}, available cash is ₹{available_cash:,.2f}."

        # Rule 3: Per-Trade Risk Budget (if stop loss is defined)
        if stop_loss is not None and stop_loss > 0:
            price_risk = (price - stop_loss).abs()
            total_trade_risk = price_risk * Decimal(quantity)
            max_allowed_risk = virtual_capital * cls.MAX_TRADE_RISK_PERCENT

            if total_trade_risk > max_allowed_risk:
                return False, f"Risk budget exceeded: Projected trade loss ₹{total_trade_risk:,.2f} exceeds your 1.0% limit (₹{max_allowed_risk:,.2f}). Reduce lot size."

        return True, "Risk validation passed"
