package com.steppay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey
    val transactionNumber: String,
    val method: String,
    val accountNumber: String,
    val amountRupiah: Int,
    val coinCost: Int,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
