package com.api.demo.infrastructure.persistence

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

import java.util.UUID

@Repository
interface JpaPaymentRepository : JpaRepository<PaymentEntity, UUID> {
    fun findByIdempotencyKey(idempotencyKey: String): PaymentEntity?
}