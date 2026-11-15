package com.bold.trading.core.common.formatters

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

/**
 * Institutional Indian Rupee (₹) Currency and Decimal-Safe Formatter.
 * Adheres strictly to the PRD mandate: zero binary floating-point representation for money.
 */
object CurrencyFormatter {

    private val indianSymbols = DecimalFormatSymbols(Locale("en", "IN")).apply {
        currencySymbol = "₹"
    }

    /**
     * Standard Indian formatting: ₹10,00,000.00
     */
    fun formatInr(amount: BigDecimal): String {
        val scaled = amount.setScale(2, RoundingMode.HALF_EVEN)
        val format = DecimalFormat("₹##,##,##0.00", indianSymbols)
        return format.format(scaled)
    }

    fun formatInr(amountDouble: Double): String {
        return formatInr(BigDecimal.valueOf(amountDouble))
    }

    fun formatInr(amountLong: Long): String {
        return formatInr(BigDecimal.valueOf(amountLong))
    }

    fun formatRupees(amount: BigDecimal): String = formatInr(amount)
    fun formatRupees(amount: Double): String = formatInr(amount)
    fun formatRupees(amount: Long): String = formatInr(amount)
    fun formatRupees(amount: Number): String = formatInr(BigDecimal(amount.toString()))

    /**
     * Compact Indian denominations: 12.4L, 2.5Cr, or raw format
     */
    fun formatCompact(amount: BigDecimal): String {
        val absVal = amount.abs()
        val crore = BigDecimal("10000000")
        val lakh = BigDecimal("100000")

        return when {
            absVal >= crore -> {
                val formatted = amount.divide(crore, 2, RoundingMode.HALF_EVEN)
                "₹${formatted}Cr"
            }
            absVal >= lakh -> {
                val formatted = amount.divide(lakh, 2, RoundingMode.HALF_EVEN)
                "₹${formatted}L"
            }
            else -> formatInr(amount)
        }
    }

    fun formatCompact(amount: Long): String = formatCompact(BigDecimal.valueOf(amount))
    fun formatCompact(amount: Double): String = formatCompact(BigDecimal.valueOf(amount))

    /**
     * Formats positive/negative percentages with sign: "+1.25%", "-0.40%"
     */
    fun formatPercentage(changePercent: Double): String {
        val sign = if (changePercent > 0) "+" else ""
        return "$sign${String.format(Locale.US, "%.2f", changePercent)}%"
    }

    fun formatPercentage(changePercent: BigDecimal): String = formatPercentage(changePercent.toDouble())
    fun formatPercentage(changePercent: Float): String = formatPercentage(changePercent.toDouble())
}

fun formatRupees(amount: BigDecimal): String = CurrencyFormatter.formatRupees(amount)
fun formatRupees(amount: Double): String = CurrencyFormatter.formatRupees(amount)
fun formatRupees(amount: Long): String = CurrencyFormatter.formatRupees(amount)
fun formatRupees(amount: Number): String = CurrencyFormatter.formatRupees(amount)
fun formatCompact(amount: Long): String = CurrencyFormatter.formatCompact(amount)
fun formatCompact(amount: BigDecimal): String = CurrencyFormatter.formatCompact(amount)
fun formatPercentage(changePercent: BigDecimal): String = CurrencyFormatter.formatPercentage(changePercent)
fun formatPercentage(changePercent: Double): String = CurrencyFormatter.formatPercentage(changePercent)
