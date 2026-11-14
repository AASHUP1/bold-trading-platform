package com.bold.trading.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.bold.trading.core.database.entity.CachedCandleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for local offline cached candles.
 * Follows DATABASE.md Section 3.2 & ARCHITECTURE.md Section 4.
 */
@Dao
interface CandleDao {

    @Query("SELECT * FROM cached_market_candles WHERE symbol = :symbol AND timeframe = :timeframe ORDER BY timestamp ASC")
    fun getCandles(symbol: String, timeframe: String): Flow<List<CachedCandleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCandles(candles: List<CachedCandleEntity>)

    @Query("DELETE FROM cached_market_candles WHERE symbol = :symbol AND timeframe = :timeframe")
    suspend fun deleteCandles(symbol: String, timeframe: String)

    @Query("SELECT COUNT(*) FROM cached_market_candles WHERE symbol = :symbol AND timeframe = :timeframe")
    suspend fun getCandleCount(symbol: String, timeframe: String): Int
}
