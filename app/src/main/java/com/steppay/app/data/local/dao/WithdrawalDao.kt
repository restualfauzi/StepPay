package com.steppay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steppay.app.data.local.entity.WithdrawalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WithdrawalDao {

    @Query("SELECT * FROM withdrawals ORDER BY timestamp DESC")
    fun getAllWithdrawals(): Flow<List<WithdrawalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWithdrawal(withdrawal: WithdrawalEntity): Long

    @Query("SELECT COUNT(*) FROM withdrawals")
    suspend fun getWithdrawalCount(): Int
}