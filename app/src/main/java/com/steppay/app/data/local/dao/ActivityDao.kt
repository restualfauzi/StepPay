package com.steppay.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.steppay.app.data.local.entity.ActivitySessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityDao {

    @Query("SELECT * FROM activity_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<ActivitySessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: ActivitySessionEntity): Long

    @Query("SELECT SUM(steps) FROM activity_sessions WHERE timestamp >= :startOfDayTimestamp")
    fun getTodayTotalSteps(startOfDayTimestamp: Long): Flow<Int?>

    @Query("SELECT * FROM activity_sessions ORDER BY timestamp DESC LIMIT 10")
    fun getRecentSessions(): Flow<List<ActivitySessionEntity>>

    @Query("DELETE FROM activity_sessions")
    suspend fun deleteAllSessions()
}