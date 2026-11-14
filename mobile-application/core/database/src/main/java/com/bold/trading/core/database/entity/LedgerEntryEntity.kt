package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "ledger_entries")
data class LedgerEntryEntity(
    @PrimaryKey
    val id: String,
    val paperAccountId: String = "account-default",
    val orderId: String? = null,
    val entryType: String, // "INITIAL_DEPOSIT", "BUY_DEBIT", "SELL_CREDIT", "SIMULATED_FEE", "ACCOUNT_RESET"
    val amount: BigDecimal,
    val balanceAfter: BigDecimal,
    val description: String,
    val createdAtEpoch: Long = System.currentTimeMillis()
)
