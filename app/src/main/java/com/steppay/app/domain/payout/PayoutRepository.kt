package com.steppay.app.domain.payout

class PayoutRepository : PayoutProvider {

    private val availableMethods = listOf(
        PayoutMethod("paypal", "PayPal", "Fast international transfer", listOf("US", "CA", "GB", "AU", "DE", "FR", "NL", "JP", "KR", "SG", "BR"), 5.0, 1.5, 1),
        PayoutMethod("dana", "DANA", "E-Wallet Indonesia", listOf("ID"), 10000.0, 0.0, 1),
        PayoutMethod("bank_transfer", "Bank Transfer", "Direct bank deposit", listOf("US", "CA", "GB", "ID", "SG"), 20.0, 2.0, 3),
        PayoutMethod("payoneer", "Payoneer", "Cross-border payment", listOf("US", "CA", "GB", "AU", "DE", "FR", "NL", "JP", "KR", "SG", "BR", "ID"), 10.0, 1.0, 2)
    )

    override suspend fun getAvailableMethods(countryCode: String): List<PayoutMethod> {
        return availableMethods.filter { it.supportedCountries.contains(countryCode.uppercase()) }
    }

    override suspend fun submitPayoutRequest(request: PayoutRequest): Result<PayoutRequest> {
        // Validation
        if (request.recipientAccount.isBlank()) {
            return Result.failure(IllegalArgumentException("Account details cannot be empty."))
        }
        
        // This is a network request simulator.
        // In a real application, this method would use Retrofit/OkHttp to submit the payout
        // to your secure financial backend for verification and processing.
        // DO NOT automatically return "Success" without a real server-side ledger check.
        // The backend should respond with a real PayoutStatus (e.g. REQUIRES_REVIEW or PENDING).
        
        // For development, we return a success state to progress the client UI flow.
        return Result.success(request.copy(status = PayoutStatus.PENDING))
    }
}