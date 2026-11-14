package com.bold.trading.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(tableName = "paper_accounts")
data class PaperAccountEntity(
    @PrimaryKey
    val id: String = "account-default",
    val userId: String = "user-default",
    val virtualCapital: BigDecimal = BigDecimal("1000000.00"), // ₹10,00,000.00 virtual capital
    val availableCash: BigDecimal = BigDecimal("1000000.00"),
    val marginUsed: BigDecimal = BigDecimal("0.00"),
    val realizedPnl: BigDecimal = BigDecimal("0.00"),
    val unrealizedPnl: BigDecimal = BigDecimal("0.00"),
    val createdAtEpoch: Long = System.currentTimeMillis()
)
