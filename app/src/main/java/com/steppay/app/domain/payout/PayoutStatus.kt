package com.steppay.app.domain.payout

enum class PayoutStatus(val label: String) {
    PENDING("Pending"),
    PROCESSING("Processing"),
    PAID("Paid"),
    FAILED("Failed"),
    REVERSED("Reversed"),
    REQUIRES_REVIEW("Requires Review")
}