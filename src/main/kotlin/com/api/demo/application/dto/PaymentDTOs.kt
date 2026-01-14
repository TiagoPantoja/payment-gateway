package com.api.demo.application.dto

import java.math.BigDecimal
import java.util.UUID

data class PaymentRequest(
    val amount: BigDecimal,
    val currency: String,
    val idempotencyKey: String
)

data class PaymentResponse(
    val id: UUID,
    val status: String
)