package com.bold.trading.feature.papertrading.risk

import java.math.BigDecimal
import java.math.RoundingMode

sealed interface RiskValidationResult {
    data object Approved : RiskValidationResult
    data class Rejected(val reason: String) : RiskValidationResult
}

/**
 * Institutional Pre-Trade Defensive Risk Engine Validator mandated by PRD Section 7 & PLAN.md Task 5.2.
 * Enforces strict capital preservation guardrails:
 * 1. Per-trade risk budget must not exceed 1.0% of virtual capital (₹10,000 for ₹10,00,000 account).
 * 2. Order value must not exceed available virtual cash balance.
 * 3. Daily loss limit cap at 2.0% (blocks trading when daily drawdown limit is breached).
 */
object RiskEngineValidator {

    private val MAX_RISK_PER_TRADE_PERCENT = BigDecimal("0.01") // 1.0% max risk per trade
    private val MAX_DAILY_LOSS_PERCENT = BigDecimal("0.02") // 2.0% max daily loss

    fun validateOrder(
        virtualCapital: BigDecimal,
        availableCash: BigDecimal,
        realizedLossToday: BigDecimal,
        entryPrice: BigDecimal,
        quantity: Int,
        stopLoss: BigDecimal?,
        isBuy: Boolean
    ): RiskValidationResult {
        // 1. Check daily loss circuit breaker
        val maxAllowedDailyLoss = virtualCapital.multiply(MAX_DAILY_LOSS_PERCENT)
        if (realizedLossToday.abs() >= maxAllowedDailyLoss && realizedLossToday < BigDecimal.ZERO) {
            return RiskValidationResult.Rejected(
                "Daily loss circuit breaker triggered: Cumulative loss exceeds 2.0% limit (₹${maxAllowedDailyLoss.setScale(2, RoundingMode.HALF_EVEN)}). Trading locked to preserve virtual capital."
            )
        }

        // 2. Check sufficient margin / cash
        val totalOrderValue = entryPrice.multiply(BigDecimal(quantity))
        if (totalOrderValue > availableCash) {
            return RiskValidationResult.Rejected(
                "Insufficient virtual cash: Required ₹${totalOrderValue.setScale(2, RoundingMode.HALF_EVEN)}, but available cash is ₹${availableCash.setScale(2, RoundingMode.HALF_EVEN)}."
            )
        }

        // 3. Check per-trade risk budget
        val maxAllowedTradeRisk = virtualCapital.multiply(MAX_RISK_PER_TRADE_PERCENT)
        if (stopLoss != null && stopLoss > BigDecimal.ZERO) {
            val riskPerShare = if (isBuy) {
                (entryPrice - stopLoss).coerceAtLeast(BigDecimal.ZERO)
            } else {
                (stopLoss - entryPrice).coerceAtLeast(BigDecimal.ZERO)
            }
            val totalTradeRisk = riskPerShare.multiply(BigDecimal(quantity))

            if (totalTradeRisk > maxAllowedTradeRisk) {
                return RiskValidationResult.Rejected(
                    "Defensive Risk Guardrail breach: Total trade risk ₹${totalTradeRisk.setScale(2, RoundingMode.HALF_EVEN)} exceeds maximum allowed 1.0% budget (₹${maxAllowedTradeRisk.setScale(2, RoundingMode.HALF_EVEN)}). Reduce position size or tighten stop-loss."
                )
            }
        }

        return RiskValidationResult.Approved
    }
}
