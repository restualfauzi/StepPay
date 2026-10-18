package com.steppay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steppay.app.data.local.entity.UserProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Query("UPDATE user_profile SET totalCoins = totalCoins + :coinsToAdd, rupiahBalance = rupiahBalance + :coinsToAdd WHERE id = 1")
    suspend fun addCoins(coinsToAdd: Int): Int

    @Query("UPDATE user_profile SET totalCoins = totalCoins - :coinsToDeduct, rupiahBalance = rupiahBalance - :coinsToDeduct WHERE id = 1")
    suspend fun deductCoins(coinsToDeduct: Int): Int

    @Query("UPDATE user_profile SET todaySteps = todaySteps + :steps WHERE id = 1")
    suspend fun addTodaySteps(steps: Int): Int

    @Query("UPDATE user_profile SET dailyTargetSteps = :newTarget WHERE id = 1")
    suspend fun updateDailyTarget(newTarget: Int): Int

    @Query("UPDATE user_profile SET name = :newName WHERE id = 1")
    suspend fun updateName(newName: String): Int

    @Query("UPDATE user_profile SET name = :name, email = :email, photoUrl = :photoUrl, paymentAccount = :email WHERE id = 1")
    suspend fun updateGoogleProfile(name: String, email: String, photoUrl: String): Int
}
