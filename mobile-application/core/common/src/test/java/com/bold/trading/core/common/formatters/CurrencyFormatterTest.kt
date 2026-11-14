package com.bold.trading.core.common.formatters

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class CurrencyFormatterTest {

    @Test
    fun formatInr_formatsCorrectIndianGrouping() {
        val tenLakhs = BigDecimal("1000000.00")
        val formatted = CurrencyFormatter.formatInr(tenLakhs)
        assertEquals("₹10,00,000.00", formatted)

        val smallAmount = BigDecimal("42680.50")
        assertEquals("₹42,680.50", CurrencyFormatter.formatInr(smallAmount))
    }

    @Test
    fun formatCompact_formatsLakhsAndCroresCorrectly() {
        val croreAmount = BigDecimal("25000000.00")
        assertEquals("₹2.50Cr", CurrencyFormatter.formatCompact(croreAmount))

        val lakhAmount = BigDecimal("1240000.00")
        assertEquals("₹12.40L", CurrencyFormatter.formatCompact(lakhAmount))
    }

    @Test
    fun formatPercentage_includesSignsProperly() {
        assertEquals("+1.25%", CurrencyFormatter.formatPercentage(1.25))
        assertEquals("-0.40%", CurrencyFormatter.formatPercentage(-0.40))
        assertEquals("0.00%", CurrencyFormatter.formatPercentage(0.0))
    }
}
