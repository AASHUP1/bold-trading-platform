package com.bold.trading.feature.options.model

import com.bold.trading.core.charts.canvas.PayoffPoint
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.max

data class OptionGreeks(
    val delta: Float,
    val gamma: Float,
    val theta: Float,
    val vega: Float
)

data class OptionStrikeRow(
    val strike: BigDecimal,
    val isAtm: Boolean,
    val callLtp: BigDecimal,
    val callOi: Long,
    val callIv: Float,
    val callDelta: Float,
    val callVolume: Long,
    val putLtp: BigDecimal,
    val putOi: Long,
    val putIv: Float,
    val putDelta: Float,
    val putVolume: Long
)

enum class StrategyPreset(val displayName: String, val description: String) {
    BULL_CALL_SPREAD("Bull Call Spread", "Buy ATM Call + Sell OTM Call for moderate upside with defined risk"),
    BEAR_PUT_SPREAD("Bear Put Spread", "Buy ATM Put + Sell OTM Put for downward move with capped risk"),
    IRON_CONDOR("Iron Condor", "Sell OTM Put & Call, Buy further OTM wings for range-bound theta decay"),
    LONG_STRADDLE("Long Straddle", "Buy ATM Call + ATM Put for major volatility breakout in either direction")
}

data class OptionLeg(
    val strike: BigDecimal,
    val isCall: Boolean,
    val isBuy: Boolean,
    val quantity: Int,
    val premium: BigDecimal
)

data class StrategyPayoffResult(
    val points: List<PayoffPoint>,
    val maxProfit: BigDecimal?,
    val maxLoss: BigDecimal?,
    val breakevens: List<Float>,
    val netDebitCredit: BigDecimal,
    val isNetDebit: Boolean
)

object StrategyPayoffCalculator {

    fun calculatePayoff(
        legs: List<OptionLeg>,
        spotPrice: Float,
        lotSize: Int = 25
    ): StrategyPayoffResult {
        if (legs.isEmpty()) {
            return StrategyPayoffResult(
                points = emptyList(),
                maxProfit = BigDecimal.ZERO,
                maxLoss = BigDecimal.ZERO,
                breakevens = emptyList(),
                netDebitCredit = BigDecimal.ZERO,
                isNetDebit = true
            )
        }

        val rangePct = 0.06f // -6% to +6% around spot
        val minSpot = spotPrice * (1f - rangePct)
        val maxSpot = spotPrice * (1f + rangePct)
        val step = (maxSpot - minSpot) / 60f

        val points = ArrayList<PayoffPoint>(61)
        val breakevens = mutableListOf<Float>()

        var prevPnl: Float? = null
        var prevPrice: Float? = null

        var current = minSpot
        while (current <= maxSpot + (step / 2f)) {
            var totalPnl = 0f

            for (leg in legs) {
                val strike = leg.strike.toFloat()
                val prem = leg.premium.toFloat()
                val qty = leg.quantity * lotSize

                val intrinsic = if (leg.isCall) {
                    max(0f, current - strike)
                } else {
                    max(0f, strike - current)
                }

                val legPnl = if (leg.isBuy) {
                    (intrinsic - prem) * qty
                } else {
                    (prem - intrinsic) * qty
                }

                totalPnl += legPnl
            }

            points.add(PayoffPoint(current, totalPnl))

            if (prevPnl != null && prevPrice != null) {
                if ((prevPnl < 0f && totalPnl >= 0f) || (prevPnl > 0f && totalPnl <= 0f)) {
                    // Linear interpolation for breakeven
                    val zeroCross = prevPrice + ((0f - prevPnl) / (totalPnl - prevPnl)) * (current - prevPrice)
                    breakevens.add(zeroCross)
                }
            }

            prevPnl = totalPnl
            prevPrice = current
            current += step
        }

        val minPnl = points.minOf { it.pnl }
        val maxPnl = points.maxOf { it.pnl }

        // Calculate net initial debit / credit
        var netCost = BigDecimal.ZERO
        for (leg in legs) {
            val cost = leg.premium.multiply(BigDecimal(leg.quantity * lotSize))
            netCost = if (leg.isBuy) netCost.add(cost) else netCost.subtract(cost)
        }

        return StrategyPayoffResult(
            points = points,
            maxProfit = if (maxPnl > 500_000f) null else BigDecimal(maxPnl.toDouble()).setScale(2, RoundingMode.HALF_EVEN),
            maxLoss = if (minPnl < -500_000f) null else BigDecimal(abs(minPnl).toDouble()).setScale(2, RoundingMode.HALF_EVEN),
            breakevens = breakevens,
            netDebitCredit = netCost.abs().setScale(2, RoundingMode.HALF_EVEN),
            isNetDebit = netCost >= BigDecimal.ZERO
        )
    }
}
