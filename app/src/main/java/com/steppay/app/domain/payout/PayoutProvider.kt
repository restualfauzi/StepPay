package com.steppay.app.domain.payout

interface PayoutProvider {
    suspend fun getAvailableMethods(countryCode: String): List<PayoutMethod>
    suspend fun submitPayoutRequest(request: PayoutRequest): Result<PayoutRequest>
}