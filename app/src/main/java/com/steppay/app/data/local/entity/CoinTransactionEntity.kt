package com.steppay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coin_transactions")
data class CoinTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val amountCoins: Int,
    val isPositive: Boolean, // true for earned +, false for spent -
    val timestamp: Long = System.currentTimeMillis(),
    val category: String // "ACTIVITY", "TARGET_BONUS", "DAILY_BONUS", "WITHDRAWAL"
)