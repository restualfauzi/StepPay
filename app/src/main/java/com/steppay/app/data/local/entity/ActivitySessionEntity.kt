package com.steppay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "activity_sessions")
data class ActivitySessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: String, // "Walking" or "Running"
    val steps: Int,
    val distanceKm: Double,
    val durationSeconds: Long,
    val calories: Int,
    val coinsEarned: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val routeJson: String = "" // Serialized lat/lng points for GPS route
)