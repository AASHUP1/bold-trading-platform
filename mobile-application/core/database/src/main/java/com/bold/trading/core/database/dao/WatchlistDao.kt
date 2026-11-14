package com.bold.trading.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bold.trading.core.database.entity.CachedWatchlistEntity
import com.bold.trading.core.database.entity.CachedWatchlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistDao {

    @Query("SELECT * FROM cached_watchlists ORDER BY sortOrder ASC")
    fun getAllWatchlists(): Flow<List<CachedWatchlistEntity>>

    @Query("SELECT * FROM cached_watchlist_items WHERE watchlistId = :watchlistId ORDER BY sortOrder ASC")
    fun getItemsForWatchlist(watchlistId: String): Flow<List<CachedWatchlistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlists(watchlists: List<CachedWatchlistEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItems(items: List<CachedWatchlistItemEntity>)

    @Query("DELETE FROM cached_watchlist_items WHERE watchlistId = :watchlistId AND instrumentId = :instrumentId")
    suspend fun removeItem(watchlistId: String, instrumentId: String)

    @Query("SELECT COUNT(*) FROM cached_watchlists")
    suspend fun getCount(): Int
}
