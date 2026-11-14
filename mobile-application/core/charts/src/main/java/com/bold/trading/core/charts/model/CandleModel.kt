package com.bold.trading.core.charts.model

data class CandleModel(
    val timestamp: Long,
    val open: Float,
    val high: Float,
    val low: Float,
    val close: Float,
    val volume: Long
) {
    val isBullish: Boolean get() = close >= open
}

object TechnicalIndicators {

    /**
     * Exponential Moving Average (EMA).
     * Multiplier = 2 / (period + 1)
     */
    fun calculateEMA(candles: List<CandleModel>, period: Int): List<Float?> {
        if (candles.size < period) return candles.map { null }

        val result = ArrayList<Float?>(candles.size)
        val k = 2.0f / (period + 1)

        // First EMA is simple average of initial 'period' bars
        var sma = 0f
        for (i in 0 until period) {
            sma += candles[i].close
            result.add(null)
        }
        var prevEma = sma / period
        result[period - 1] = prevEma

        for (i in period until candles.size) {
            val currentEma = (candles[i].close * k) + (prevEma * (1 - k))
            result.add(currentEma)
            prevEma = currentEma
        }

        return result
    }

    /**
     * Volume Weighted Average Price (VWAP).
     * Sum(Typical Price * Volume) / Sum(Volume)
     */
    fun calculateVWAP(candles: List<CandleModel>): List<Float?> {
        if (candles.isEmpty()) return emptyList()

        val result = ArrayList<Float?>(candles.size)
        var cumulativeTPV = 0.0
        var cumulativeVolume = 0L

        for (candle in candles) {
            val typicalPrice = (candle.high + candle.low + candle.close) / 3.0
            cumulativeTPV += typicalPrice * candle.volume
            cumulativeVolume += candle.volume

            val vwap = if (cumulativeVolume > 0) (cumulativeTPV / cumulativeVolume).toFloat() else candle.close
            result.add(vwap)
        }
        return result
    }

    /**
     * Relative Strength Index (RSI) with 14-period standard.
     */
    fun calculateRSI(candles: List<CandleModel>, period: Int = 14): List<Float?> {
        if (candles.size <= period) return candles.map { null }

        val result = ArrayList<Float?>(candles.size)
        for (i in 0 until period) {
            result.add(null)
        }

        var gainSum = 0f
        var lossSum = 0f

        for (i in 1..period) {
            val diff = candles[i].close - candles[i - 1].close
            if (diff >= 0) gainSum += diff else lossSum += -diff
        }

        var avgGain = gainSum / period
        var avgLoss = lossSum / period

        val firstRs = if (avgLoss == 0f) 100f else avgGain / avgLoss
        val firstRsi = 100f - (100f / (1f + firstRs))
        result.add(firstRsi)

        for (i in (period + 1) until candles.size) {
            val diff = candles[i].close - candles[i - 1].close
            val currentGain = if (diff > 0) diff else 0f
            val currentLoss = if (diff < 0) -diff else 0f

            avgGain = ((avgGain * (period - 1)) + currentGain) / period
            avgLoss = ((avgLoss * (period - 1)) + currentLoss) / period

            val rs = if (avgLoss == 0f) 100f else avgGain / avgLoss
            val rsi = 100f - (100f / (1f + rs))
            result.add(rsi)
        }

        return result
    }
}
