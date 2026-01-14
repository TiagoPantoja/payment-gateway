package com.api.demo.infrastructure.persistence

import api.kotlin.demo.domain.model.PaymentStatus

import jakarta.persistence.*

import java.math.BigDecimal
import java.util.UUID

@Entity
@Table(name = "payments")
class PaymentEntity(
    @Id val id: UUID,
    val amount: BigDecimal,
    val currency: String,
    @Column(unique = true) val idempotencyKey: String,
    @Enumerated(EnumType.STRING) val status: PaymentStatus
)