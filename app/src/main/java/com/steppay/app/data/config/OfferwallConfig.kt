package com.steppay.app.data.config

object OfferwallConfig {
    const val TAPJOY_SDK_KEY = "a7nXZM0vQ66TSR7EJWVkZgEC26Jr8Q3wFbR8Uj5M3PjpjR5tqv-AysgCDrAW"
    const val BITLABS_API_KEY = "3e1c540f-28f0-4744-80d5-519213368339"

    fun getBitLabsUrl(userId: String): String {
        val safeUserId = userId.ifBlank { "steppay_user_101" }
        return "https://web.bitlabs.ai/?token=$BITLABS_API_KEY&uid=$safeUserId"
    }

    fun getTapjoyUrl(userId: String): String {
        val safeUserId = userId.ifBlank { "steppay_user_101" }
        return "https://offerwall.tapjoy.com/my_offers?sdk_key=$TAPJOY_SDK_KEY&user_id=$safeUserId"
    }
}
