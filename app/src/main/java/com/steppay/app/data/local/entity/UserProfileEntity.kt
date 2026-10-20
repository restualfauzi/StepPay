package com.steppay.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "User",
    val email: String = "",
    val photoUrl: String = "",
    val memberId: String = "SP100001",
    val badge: String = "Member",
    val dailyTargetSteps: Int = 5000,
    val todaySteps: Int = 0,
    val totalCoins: Int = 0,
    val rupiahBalance: Int = 0,
    val paymentMethod: String = "PayPal",
    val paymentAccount: String = "",
    val checksum: String = "",
    val referralCode: String = "",
    val referredBy: String = "",
    val shoeLevel: Int = 1,
    val waterGlasses: Int = 0,
    val lastWaterResetDate: String = ""
)
