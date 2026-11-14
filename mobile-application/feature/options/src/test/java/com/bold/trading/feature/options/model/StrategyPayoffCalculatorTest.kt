package com.bold.trading.feature.options.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class StrategyPayoffCalculatorTest {

    @Test
    fun calculatePayoff_bullCallSpread_hasDefinedMaxProfitAndMaxLoss() {
        // Buy 24350 CE @ 120, Sell 24450 CE @ 65 (Net Debit = 55 points)
        val legs = listOf(
            OptionLeg(strike = BigDecimal("24350"), isCall = true, isBuy = true, quantity = 1, premium = BigDecimal("120.00")),
            OptionLeg(strike = BigDecimal("24450"), isCall = true, isBuy = false, quantity = 1, premium = BigDecimal("65.00"))
        )

        val result = StrategyPayoffCalculator.calculatePayoff(legs, 24360f, lotSize = 25)

        assertEquals(61, result.points.size)
        assertTrue(result.isNetDebit)
        assertEquals(BigDecimal("1375.00"), result.netDebitCredit) // 55 * 25
        assertNotNull(result.maxProfit)
        assertNotNull(result.maxLoss)
        assertTrue("Breakevens should be detected", result.breakevens.isNotEmpty())
    }

    @Test
    fun calculatePayoff_emptyLegs_returnsZeroResult() {
        val result = StrategyPayoffCalculator.calculatePayoff(emptyList(), 24000f, 25)
        assertEquals(0, result.points.size)
        assertEquals(BigDecimal.ZERO, result.netDebitCredit)
    }
}
