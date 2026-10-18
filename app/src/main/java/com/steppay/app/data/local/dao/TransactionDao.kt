package com.steppay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steppay.app.data.local.entity.CoinTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query("SELECT * FROM coin_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<CoinTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: CoinTransactionEntity): Long

    @Query("SELECT COUNT(*) FROM coin_transactions")
    suspend fun getTransactionCount(): Int
}