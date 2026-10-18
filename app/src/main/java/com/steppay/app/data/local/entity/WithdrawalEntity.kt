package com.steppay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "withdrawals")
data class WithdrawalEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val method: String, // e.g., "DANA"
    val accountNumber: String,
    val amountRupiah: Int,
    val coinCost: Int,
    val status: String, // "Berhasil", "Menunggu", "Gagal"
    val timestamp: Long = System.currentTimeMillis(),
    val transactionNumber: String
)