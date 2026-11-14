package com.bold.trading.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface InstrumentDao {

    @Query("SELECT * FROM cached_instruments ORDER BY symbol ASC")
    fun getAllInstruments(): Flow<List<CachedInstrumentEntity>>

    @Query("SELECT * FROM cached_instruments WHERE symbol = :symbol LIMIT 1")
    fun getInstrumentBySymbol(symbol: String): Flow<CachedInstrumentEntity?>

    @Query("SELECT * FROM cached_instruments WHERE symbol LIKE '%' || :query || '%' OR name LIKE '%' || :query || '%'")
    fun searchInstruments(query: String): Flow<List<CachedInstrumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInstruments(instruments: List<CachedInstrumentEntity>)

    @Query("UPDATE cached_instruments SET lastTradedPrice = :newLtp, change = :newChange, changePercent = :newChangePercent, high24h = MAX(high24h, :newLtp), low24h = MIN(low24h, :newLtp), lastUpdatedEpoch = :now WHERE symbol = :symbol")
    suspend fun updateQuote(
        symbol: String,
        newLtp: BigDecimal,
        newChange: BigDecimal,
        newChangePercent: BigDecimal,
        now: Long = System.currentTimeMillis()
    )

    @Query("SELECT COUNT(*) FROM cached_instruments")
    suspend fun getCount(): Int
}
