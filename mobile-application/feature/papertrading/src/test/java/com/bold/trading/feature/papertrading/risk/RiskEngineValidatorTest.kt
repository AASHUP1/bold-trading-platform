package com.bold.trading.feature.papertrading.risk

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class RiskEngineValidatorTest {

    private val virtualCapital = BigDecimal("1000000.00") // ₹10 Lakhs
    private val availableCash = BigDecimal("1000000.00")

    @Test
    fun validateOrder_normalRiskOrder_isApproved() {
        // Buy 10 shares of RELIANCE @ ₹3000, SL @ ₹2950 (Risk = ₹500, which is << ₹10,000 budget)
        val result = RiskEngineValidator.validateOrder(
            virtualCapital = virtualCapital,
            availableCash = availableCash,
            realizedLossToday = BigDecimal.ZERO,
            entryPrice = BigDecimal("3000.00"),
            quantity = 10,
            stopLoss = BigDecimal("2950.00"),
            isBuy = true
        )

        assertTrue(result is RiskValidationResult.Approved)
    }

    @Test
    fun validateOrder_exceedsRiskBudget_isRejected() {
        // Buy 300 shares @ ₹3000, SL @ ₹2900 (Risk = 300 * ₹100 = ₹30,000 > ₹10,000 max budget)
        val result = RiskEngineValidator.validateOrder(
            virtualCapital = virtualCapital,
            availableCash = availableCash,
            realizedLossToday = BigDecimal.ZERO,
            entryPrice = BigDecimal("3000.00"),
            quantity = 300,
            stopLoss = BigDecimal("2900.00"),
            isBuy = true
        )

        assertTrue(result is RiskValidationResult.Rejected)
        val error = (result as RiskValidationResult.Rejected).reason
        assertTrue(error.contains("Defensive Risk Guardrail breach"))
    }

    @Test
    fun validateOrder_insufficientCash_isRejected() {
        // Order value ₹12,00,000 > available cash ₹10,00,000
        val result = RiskEngineValidator.validateOrder(
            virtualCapital = virtualCapital,
            availableCash = availableCash,
            realizedLossToday = BigDecimal.ZERO,
            entryPrice = BigDecimal("3000.00"),
            quantity = 400,
            stopLoss = BigDecimal("2990.00"),
            isBuy = true
        )

        assertTrue(result is RiskValidationResult.Rejected)
        val error = (result as RiskValidationResult.Rejected).reason
        assertTrue(error.contains("Insufficient virtual cash"))
    }

    @Test
    fun validateOrder_dailyLossCircuitBreaker_isRejected() {
        // Daily loss ₹25,000 > 2.0% (₹20,000)
        val result = RiskEngineValidator.validateOrder(
            virtualCapital = virtualCapital,
            availableCash = availableCash,
            realizedLossToday = BigDecimal("-25000.00"),
            entryPrice = BigDecimal("3000.00"),
            quantity = 1,
            stopLoss = BigDecimal("2990.00"),
            isBuy = true
        )

        assertTrue(result is RiskValidationResult.Rejected)
        val error = (result as RiskValidationResult.Rejected).reason
        assertTrue(error.contains("Daily loss circuit breaker triggered"))
    }
}
