package api.kotlin.demo.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Payment(
    val id: UUID = UUID.randomUUID(),
    val amount: BigDecimal,
    val currency: String,
    val idempotencyKey: String,
    var status: PaymentStatus = PaymentStatus.PENDING
)

enum class PaymentStatus { PENDING, PROCESSING, COMPLETED, FAILED }