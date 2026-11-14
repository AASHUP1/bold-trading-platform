package com.bold.trading.core.networking.ticker

import com.bold.trading.core.database.BoldDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.random.Random

/**
 * Dynamic Market Tick Engine.
 * Simulates high-frequency tick streams for liquid NSE/BSE core assets,
 * updating the Room Database directly to ensure the app is 100% dynamic from launch.
 */
class MarketTickEngine(
    private val database: BoldDatabase,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var tickerJob: Job? = null

    fun startStreaming() {
        if (tickerJob?.isActive == true) return

        tickerJob = scope.launch {
            while (isActive) {
                delay(1500) // 1.5 second institutional tick interval

                val instruments = database.instrumentDao().getAllInstruments().firstOrNull() ?: emptyList()
                if (instruments.isNotEmpty()) {
                    // Pick 1-3 random instruments to tick per cycle
                    val targetCount = Random.nextInt(1, 4)
                    instruments.shuffled().take(targetCount).forEach { inst ->
                        // Calculate realistic +/- 0.05% to 0.15% micro-fluctuation
                        val percentageDelta = (Random.nextDouble() - 0.49) * 0.0015
                        val currentLtp = inst.lastTradedPrice
                        val tickDelta = currentLtp.multiply(BigDecimal.valueOf(percentageDelta))
                            .setScale(2, RoundingMode.HALF_EVEN)

                        val newLtp = currentLtp.add(tickDelta).coerceAtLeast(BigDecimal("1.00"))
                        val newChange = inst.change.add(tickDelta)
                        val prevClose = newLtp.subtract(newChange).coerceAtLeast(BigDecimal("0.01"))
                        val newChangePercent = newChange.multiply(BigDecimal("100.00"))
                            .divide(prevClose, 2, RoundingMode.HALF_EVEN)

                        database.instrumentDao().updateQuote(
                            symbol = inst.symbol,
                            newLtp = newLtp,
                            newChange = newChange,
                            newChangePercent = newChangePercent
                        )
                    }
                }
            }
        }
    }

    fun stopStreaming() {
        tickerJob?.cancel()
        tickerJob = null
    }
}
