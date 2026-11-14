package com.bold.trading.core.charts.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TechnicalIndicatorsTest {

    private fun createMockCandles(count: Int, startPrice: Float = 100f): List<CandleModel> {
        return (0 until count).map { i ->
            val p = startPrice + i * 2f
            CandleModel(
                timestamp = 1000L * i,
                open = p - 1f,
                high = p + 2f,
                low = p - 2f,
                close = p + 1f,
                volume = 1000L
            )
        }
    }

    @Test
    fun calculateEMA_period20_returnsNullForInitialValuesAndValidForRest() {
        val candles = createMockCandles(30)
        val ema = TechnicalIndicators.calculateEMA(candles, 20)

        assertEquals(30, ema.size)
        // Values before period - 1 must be null
        for (i in 0 until 19) {
            assertNull("Index $i should be null", ema[i])
        }
        assertNotNull("Index 19 should have SMA/EMA base", ema[19])
        assertNotNull("Index 29 should have EMA", ema[29])
        assertTrue("EMA should trend upward with upward candles", ema[29]!! > ema[19]!!)
    }

    @Test
    fun calculateVWAP_computesCumulativePriceVolume() {
        val candles = createMockCandles(10)
        val vwap = TechnicalIndicators.calculateVWAP(candles)

        assertEquals(10, vwap.size)
        vwap.forEach { assertNotNull(it) }
        assertTrue("VWAP must be positive", vwap.last()!! > 0f)
    }

    @Test
    fun calculateRSI_computes14PeriodRsiWithin0And100() {
        val candles = createMockCandles(25)
        val rsi = TechnicalIndicators.calculateRSI(candles, 14)

        assertEquals(25, rsi.size)
        val lastRsi = rsi.last()
        assertNotNull(lastRsi)
        assertTrue("RSI must be between 0 and 100", lastRsi!! in 0f..100f)
        assertTrue("Monotonically increasing series must have high RSI", lastRsi > 70f)
    }
}
