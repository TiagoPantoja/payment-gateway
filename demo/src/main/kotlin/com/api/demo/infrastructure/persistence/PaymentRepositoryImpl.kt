package com.api.demo.infrastructure.persistence

import api.kotlin.demo.domain.model.Payment

import com.api.demo.domain.repository.PaymentRepository
import org.springframework.stereotype.Component

import java.util.UUID

@Component
class PaymentRepositoryImpl(
    private val jpaRepository: JpaPaymentRepository
) : PaymentRepository {

    override fun save(payment: Payment): Payment {
        val entity = PaymentEntity(
            id = payment.id,
            amount = payment.amount,
            currency = payment.currency,
            idempotencyKey = payment.idempotencyKey,
            status = payment.status
        )
        jpaRepository.save(entity)
        return payment
    }

    override fun findById(id: UUID): Payment? {
        return jpaRepository.findById(id).map { entity ->
            Payment(
                id = entity.id,
                amount = entity.amount,
                currency = entity.currency,
                idempotencyKey = entity.idempotencyKey,
                status = entity.status
            )
        }.orElse(null)
    }

    override fun findByIdempotencyKey(key: String): Payment? {
        return jpaRepository.findByIdempotencyKey(key)?.let { entity ->
            Payment(
                id = entity.id,
                amount = entity.amount,
                currency = entity.currency,
                idempotencyKey = entity.idempotencyKey,
                status = entity.status
            )
        }
    }
}