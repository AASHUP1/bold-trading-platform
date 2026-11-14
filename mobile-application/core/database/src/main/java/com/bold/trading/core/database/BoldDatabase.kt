package com.bold.trading.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.bold.trading.core.database.converter.Converters
import com.bold.trading.core.database.dao.CandleDao
import com.bold.trading.core.database.dao.InstrumentDao
import com.bold.trading.core.database.dao.PaperAccountDao
import com.bold.trading.core.database.dao.WatchlistDao
import com.bold.trading.core.database.entity.CachedCandleEntity
import com.bold.trading.core.database.entity.CachedInstrumentEntity
import com.bold.trading.core.database.entity.CachedPaperPositionEntity
import com.bold.trading.core.database.entity.CachedWatchlistEntity
import com.bold.trading.core.database.entity.CachedWatchlistItemEntity
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.OfflineTradeJournalEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import com.bold.trading.core.database.seed.DatabaseSeeder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        CachedInstrumentEntity::class,
        PaperAccountEntity::class,
        LedgerEntryEntity::class,
        CachedWatchlistEntity::class,
        CachedWatchlistItemEntity::class,
        CachedPaperPositionEntity::class,
        OfflineTradeJournalEntity::class,
        CachedCandleEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class BoldDatabase : RoomDatabase() {

    abstract fun instrumentDao(): InstrumentDao
    abstract fun paperAccountDao(): PaperAccountDao
    abstract fun watchlistDao(): WatchlistDao
    abstract fun candleDao(): CandleDao

    companion object {
        @Volatile
        private var INSTANCE: BoldDatabase? = null

        fun getInstance(context: Context, scope: CoroutineScope = CoroutineScope(Dispatchers.IO)): BoldDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BoldDatabase::class.java,
                    "bold_trading.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed database dynamically on initial creation
                        scope.launch {
                            INSTANCE?.let { database ->
                                database.instrumentDao().insertInstruments(DatabaseSeeder.INITIAL_INSTRUMENTS)
                                database.paperAccountDao().insertAccount(DatabaseSeeder.INITIAL_PAPER_ACCOUNT)
                                database.paperAccountDao().insertLedgerEntry(DatabaseSeeder.INITIAL_LEDGER_ENTRY)
                                database.watchlistDao().insertWatchlists(listOf(DatabaseSeeder.INITIAL_WATCHLIST))
                                database.watchlistDao().insertWatchlistItems(DatabaseSeeder.INITIAL_WATCHLIST_ITEMS)
                                database.candleDao().insertCandles(DatabaseSeeder.INITIAL_CANDLES)
                            }
                        }
                    }
                })
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
