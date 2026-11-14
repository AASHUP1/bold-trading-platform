package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_watchlists")
data class CachedWatchlistEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false
)

@Entity(
    tableName = "cached_watchlist_items",
    primaryKeys = ["watchlistId", "instrumentId"]
)
data class CachedWatchlistItemEntity(
    val watchlistId: String,
    val instrumentId: String,
    val symbol: String,
    val sortOrder: Int = 0
)
