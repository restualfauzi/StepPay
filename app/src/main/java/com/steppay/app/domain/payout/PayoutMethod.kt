package com.steppay.app.domain.payout

data class PayoutMethod(
    val id: String,
    val name: String,             // e.g. "PayPal", "Bank Transfer", "Payoneer", "DANA"
    val description: String,
    val supportedCountries: List<String>, // Country codes e.g. ["US", "CA", "GB", "ID"]
    val minPayoutAmount: Double,
    val feePercentage: Double,
    val estimatedDays: Int
)