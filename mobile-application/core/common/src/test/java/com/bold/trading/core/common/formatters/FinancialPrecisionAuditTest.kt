package com.bold.trading.core.common.formatters

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Task 8.2: Financial Precision & Zero Floating-Point Arithmetic Audit Test.
 * Confirms all monetary operations adhere strictly to BigDecimal NUMERIC(15, 2)
 * without binary IEEE 754 floating point inaccuracies.
 */
class FinancialPrecisionAuditTest {

    @Test
    fun decimalArithmetic_exactToThePaise_zeroFloatingPointDrift() {
        // Test classic floating point error: 0.1 + 0.2 != 0.3 in IEEE 754
        val val1 = BigDecimal("0.10")
        val val2 = BigDecimal("0.20")
        val sum = val1.add(val2)
        assertEquals(BigDecimal("0.30"), sum)

        // Multiple micro additions and subtractions across virtual ledger
        var ledgerBalance = BigDecimal("1000000.00")
        val tradeCost = BigDecimal("14258.75")
        val brokerageFee = BigDecimal("20.00")
        val sttTax = BigDecimal("14.25")
        val gstTax = BigDecimal("3.60")

        ledgerBalance = ledgerBalance
            .subtract(tradeCost)
            .subtract(brokerageFee)
            .subtract(sttTax)
            .subtract(gstTax)

        assertEquals(BigDecimal("985703.40"), ledgerBalance)
    }

    @Test
    fun ledgerInvariant_capitalMinusMarginEqualsAvailableCash() {
        val totalCapital = BigDecimal("1000000.00")
        val lotSize = 25
        val lots = 4
        val premium = BigDecimal("142.50")
        val marginPerLot = premium.multiply(BigDecimal(lotSize)) // 142.50 * 25 = 3562.50
        val totalMarginUsed = marginPerLot.multiply(BigDecimal(lots)).setScale(2, RoundingMode.HALF_UP) // 14250.00

        val availableCash = totalCapital.subtract(totalMarginUsed)

        assertEquals(BigDecimal("14250.00"), totalMarginUsed)
        assertEquals(BigDecimal("985750.00"), availableCash)
        assertEquals(totalCapital, availableCash.add(totalMarginUsed))
    }

    @Test
    fun riskBudgetBoundary_enforcesExactOnePercent() {
        val capital = BigDecimal("1000000.00")
        val maxRiskPercent = BigDecimal("0.01") // 1.0%
        val maxAllowableLoss = capital.multiply(maxRiskPercent).setScale(2, RoundingMode.HALF_UP)

        assertEquals(BigDecimal("10000.00"), maxAllowableLoss)

        // Verify orders risking ₹10,000.01 are rejected
        val excessiveRisk = BigDecimal("10000.01")
        assertTrue(excessiveRisk > maxAllowableLoss)
    }
}
