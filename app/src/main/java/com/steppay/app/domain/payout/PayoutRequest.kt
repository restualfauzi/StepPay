package com.steppay.app.domain.payout

data class PayoutRequest(
    val id: String,
    val userId: String,
    val methodId: String,
    val amountCoins: Int,
    val amountLocalCurrency: Double,
    val currencyCode: String,
    val recipientAccount: String,
    val status: PayoutStatus = PayoutStatus.PENDING,
    val timestamp: Long = System.currentTimeMillis(),
    val idempotencyKey: String
)