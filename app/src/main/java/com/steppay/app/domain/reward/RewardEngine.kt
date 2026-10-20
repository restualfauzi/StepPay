package com.steppay.app.domain.reward

import kotlin.math.roundToInt

class RewardEngine {

    companion object {
        // BALANCED SWEET-SPOT ECONOMY ($1.00 USD = 100,000 Coins)
        const val STEPS_PER_COIN = 10 // 1 coin per 10 steps (10k steps = 1,000 Coins)
        const val TARGET_COMPLETION_BONUS = 500 // 500 coins bonus for reaching daily goal
        const val MAX_DAILY_COINS = 2000 // Max 2,000 coins per day from pure walking
        const val DOLLAR_CENT_PER_COIN = 0.001 // 100,000 Coins = 100 Cents ($1.00)

        fun calculateActivityCoins(steps: Int): Int {
            if (steps <= 0) return 0
            val coins = steps / STEPS_PER_COIN
            return coins.coerceAtMost(MAX_DAILY_COINS)
        }

        fun calculateRupiahValue(coins: Int): Int {
            // Kita kembalikan nilai USD Cents jika dibutuhkan, atau konversi ekuivalen
            return (coins * DOLLAR_CENT_PER_COIN).roundToInt()
        }

        fun estimateCalories(steps: Int): Int {
            // Average ~0.045 kcal per step
            return (steps * 0.045).roundToInt()
        }

        fun estimateDistanceKm(steps: Int): Double {
            // Average step length ~0.75 meters (0.00075 km)
            val distance = steps * 0.00075
            return (distance * 100).roundToInt() / 100.0
        }
    }
}