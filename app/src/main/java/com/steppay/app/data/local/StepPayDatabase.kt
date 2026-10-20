package com.steppay.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.steppay.app.data.local.dao.ActivityDao
import com.steppay.app.data.local.dao.TransactionDao
import com.steppay.app.data.local.dao.UserProfileDao
import com.steppay.app.data.local.dao.WithdrawalDao
import com.steppay.app.data.local.entity.ActivitySessionEntity
import com.steppay.app.data.local.entity.CoinTransactionEntity
import com.steppay.app.data.local.entity.UserProfileEntity
import com.steppay.app.data.local.entity.WithdrawalEntity

@Database(
    entities = [
        ActivitySessionEntity::class,
        CoinTransactionEntity::class,
        WithdrawalEntity::class,
        UserProfileEntity::class,
    ],
    version = 6,
    exportSchema = false,
)
abstract class StepPayDatabase : RoomDatabase() {

    abstract fun activityDao(): ActivityDao
    abstract fun transactionDao(): TransactionDao
    abstract fun withdrawalDao(): WithdrawalDao
    abstract fun userProfileDao(): UserProfileDao

    companion object {
        @Volatile
        private var INSTANCE: StepPayDatabase? = null

        fun getDatabase(context: Context): StepPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    StepPayDatabase::class.java,
                    "steppay_database",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}