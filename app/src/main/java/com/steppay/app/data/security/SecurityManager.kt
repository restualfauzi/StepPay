package com.steppay.app.data.security

import android.os.Build
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object SecurityManager {

    private const val HMAC_SECRET = "StepPay_Secure_Crypto_Salt_Key_2026_x89f!"
    const val MAX_STEPS_PER_SECOND = 10

    /**
     * Calculates SHA-256 HMAC checksum for coin balances to prevent SQLite & Memory tampering
     */
    fun calculateCoinChecksum(memberId: String, totalCoins: Int, todaySteps: Int): String {
        val payload = "MEMBER:$memberId|COINS:$totalCoins|STEPS:$todaySteps"
        return try {
            val sha256HMAC = Mac.getInstance("HmacSHA256")
            val secretKey = SecretKeySpec(HMAC_SECRET.toByteArray(Charsets.UTF_8), "HmacSHA256")
            sha256HMAC.init(secretKey)
            val hash = sha256HMAC.doFinal(payload.toByteArray(Charsets.UTF_8))
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            payload.hashCode().toString()
        }
    }

    /**
     * Validates step rate from physical hardware sensor to block mechanical shakers & speed hacks
     */
    fun isValidStepIncrement(stepDelta: Int, timeDeltaSeconds: Long): Boolean {
        if (timeDeltaSeconds <= 0) return stepDelta <= MAX_STEPS_PER_SECOND
        val stepsPerSecond = stepDelta.toDouble() / timeDeltaSeconds.toDouble()
        return stepsPerSecond <= MAX_STEPS_PER_SECOND
    }

    /**
     * Detects if device is running on an emulator used for automated farming
     */
    fun isEmulator(): Boolean {
        return (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk" == Build.PRODUCT)
    }
}
