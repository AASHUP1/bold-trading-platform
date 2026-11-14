package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "cached_instruments")
data class CachedInstrumentEntity(
    @PrimaryKey
    val id: String,
    val exchange: String, // "NSE" or "BSE"
    val symbol: String, // "RELIANCE", "NIFTY 50"
    val name: String,
    val instrumentType: String, // "equity", "index", "derivative"
    val tickSize: BigDecimal,
    val lotSize: Int,
    val lastTradedPrice: BigDecimal,
    val change: BigDecimal,
    val changePercent: BigDecimal,
    val high24h: BigDecimal,
    val low24h: BigDecimal,
    val volume: Long,
    val lastUpdatedEpoch: Long = System.currentTimeMillis()
) {
    // Convenience property delegates for Compose UI
    val ltp: BigDecimal get() = lastTradedPrice
    val changeAmount: BigDecimal get() = change
    val volume24h: Long get() = volume
    val instrumentId: String get() = id
    val isIndex: Boolean get() = instrumentType.equals("index", ignoreCase = true)

    val sector: String get() = when {
        symbol.contains("NIFTY") || symbol.contains("SENSEX") || isIndex -> "Index"
        symbol in listOf("RELIANCE", "ONGC") -> "Energy"
        symbol in listOf("TCS", "INFY", "WIPRO") -> "Technology"
        symbol in listOf("HDFCBANK", "ICICIBANK", "SBIN", "BANKNIFTY") -> "Banking"
        symbol in listOf("TATAMOTORS", "MARUTI") -> "Automobile"
        else -> "Equities"
    }
}
