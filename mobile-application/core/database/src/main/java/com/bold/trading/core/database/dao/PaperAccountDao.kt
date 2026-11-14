package com.bold.trading.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.bold.trading.core.database.entity.LedgerEntryEntity
import com.bold.trading.core.database.entity.PaperAccountEntity
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface PaperAccountDao {

    @Query("SELECT * FROM paper_accounts WHERE id = :accountId LIMIT 1")
    fun getPaperAccount(accountId: String = "account-default"): Flow<PaperAccountEntity?>

    @Query("SELECT * FROM ledger_entries WHERE paperAccountId = :accountId ORDER BY createdAtEpoch DESC")
    fun getLedgerEntries(accountId: String = "account-default"): Flow<List<LedgerEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccount(account: PaperAccountEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertLedgerEntry(entry: LedgerEntryEntity)

    @Query("SELECT * FROM cached_paper_positions ORDER BY symbol ASC")
    fun getAllPositions(): Flow<List<com.bold.trading.core.database.entity.CachedPaperPositionEntity>>

    @Query("SELECT * FROM cached_paper_positions WHERE instrumentId = :instrumentId LIMIT 1")
    suspend fun getPositionByInstrumentId(instrumentId: String): com.bold.trading.core.database.entity.CachedPaperPositionEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePosition(position: com.bold.trading.core.database.entity.CachedPaperPositionEntity)

    @Query("DELETE FROM cached_paper_positions WHERE instrumentId = :instrumentId")
    suspend fun deletePosition(instrumentId: String)

    @Query("DELETE FROM cached_paper_positions")
    suspend fun deleteAllPositions()

    @Query("UPDATE paper_accounts SET availableCash = :newCash, marginUsed = :newMargin WHERE id = :accountId")
    suspend fun updateCashAndMargin(accountId: String, newCash: BigDecimal, newMargin: BigDecimal)

    @Query("UPDATE paper_accounts SET realizedPnl = realizedPnl + :pnlDelta WHERE id = :accountId")
    suspend fun addRealizedPnl(accountId: String, pnlDelta: BigDecimal)

    @Transaction
    suspend fun resetAccountToDefault() {
        deleteAllPositions()
        val initialCapital = BigDecimal("1000000.00")
        val resetAccount = PaperAccountEntity(
            id = "account-default",
            virtualCapital = initialCapital,
            availableCash = initialCapital,
            marginUsed = BigDecimal.ZERO,
            realizedPnl = BigDecimal.ZERO,
            unrealizedPnl = BigDecimal.ZERO
        )
        insertAccount(resetAccount)

        val resetEntry = LedgerEntryEntity(
            id = "reset-${System.currentTimeMillis()}",
            paperAccountId = "account-default",
            entryType = "ACCOUNT_RESET",
            amount = initialCapital,
            balanceAfter = initialCapital,
            description = "Virtual account reset to ₹10,00,000 initial capital"
        )
        insertLedgerEntry(resetEntry)
    }

    @Query("SELECT COUNT(*) FROM paper_accounts")
    suspend fun getCount(): Int
}
