package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import java.math.BigDecimal

/**
 * Cached OHLCV historical candle entity for offline chart viewing.
 * Follows DATABASE.md Section 3.2: market_candles & PRD.md Section 6.1.
 */
@Entity(
    tableName = "cached_market_candles",
    primaryKeys = ["symbol", "timeframe", "timestamp"],
    indices = [
        Index(value = ["symbol", "timeframe"]),
        Index(value = ["symbol", "timeframe", "timestamp"])
    ]
)
data class CachedCandleEntity(
    val symbol: String,
    val timeframe: String, // "1m", "5m", "15m", "1H", "1D"
    val timestamp: Long,
    val open: BigDecimal,
    val high: BigDecimal,
    val low: BigDecimal,
    val close: BigDecimal,
    val volume: Long,
    val vwap: BigDecimal? = null
)
