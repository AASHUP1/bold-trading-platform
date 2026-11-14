package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "cached_paper_positions")
data class CachedPaperPositionEntity(
    @PrimaryKey
    val instrumentId: String,
    val symbol: String,
    val quantity: Int, // Positive = Long, Negative = Short
    val averageEntryPrice: BigDecimal,
    val currentLtp: BigDecimal,
    val unrealizedPnl: BigDecimal,
    val updatedEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "offline_trade_journal")
data class OfflineTradeJournalEntity(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val pnlAmount: BigDecimal,
    val processRating: Int, // 1 to 5 stars
    val notes: String,
    val tags: String, // e.g. "#VWAP,#Disciplined"
    val timestampEpoch: Long = System.currentTimeMillis()
)
