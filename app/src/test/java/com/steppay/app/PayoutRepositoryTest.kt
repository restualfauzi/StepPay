package com.steppay.app

import com.steppay.app.domain.payout.PayoutRepository
import com.steppay.app.domain.payout.PayoutRequest
import com.steppay.app.domain.payout.PayoutStatus
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PayoutRepositoryTest {

    private lateinit var payoutRepository: PayoutRepository

    @Before
    fun setUp() {
        payoutRepository = PayoutRepository()
    }

    @Test
    fun getAvailableMethods_filtersByCountry() = runBlocking {
        val usMethods = payoutRepository.getAvailableMethods("US")
        assertTrue(usMethods.any { it.id == "paypal" })

        val idMethods = payoutRepository.getAvailableMethods("ID")
        assertTrue(idMethods.any { it.id == "dana" })
    }

    @Test
    fun submitPayoutRequest_validatesAccount() = runBlocking {
        val invalidRequest = PayoutRequest(
            id = "req_1",
            userId = "user_1",
            methodId = "paypal",
            amountCoins = 1000,
            amountLocalCurrency = 10.0,
            currencyCode = "USD",
            recipientAccount = "",
            idempotencyKey = "key_1"
        )
        val result = payoutRepository.submitPayoutRequest(invalidRequest)
        assertTrue(result.isFailure)

        val validRequest = invalidRequest.copy(recipientAccount = "test@example.com")
        val successResult = payoutRepository.submitPayoutRequest(validRequest)
        assertTrue(successResult.isSuccess)
        assertEquals(PayoutStatus.PENDING, successResult.getOrNull()?.status)
    }
}