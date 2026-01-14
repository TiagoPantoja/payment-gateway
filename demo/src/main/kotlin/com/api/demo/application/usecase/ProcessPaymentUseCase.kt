package com.api.demo.application.usecase

import api.kotlin.demo.domain.model.Payment

import com.api.demo.application.dto.PaymentRequest
import com.api.demo.application.dto.PaymentResponse
import com.api.demo.domain.repository.PaymentRepository
import com.api.demo.infrastructure.gateway.ExternalPaymentGatewayClient

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import java.time.Duration

@Service
class ProcessPaymentUseCase(
    private val paymentRepository: PaymentRepository,
    private val redisTemplate: StringRedisTemplate,
    private val gatewayClient: ExternalPaymentGatewayClient
) {
    @Transactional
    fun execute(request: PaymentRequest): PaymentResponse {
        val existingPayment = paymentRepository.findByIdempotencyKey(request.idempotencyKey)
        if (existingPayment != null) {
            return PaymentResponse(existingPayment.id, existingPayment.status.name)
        }

        val lockKey = "payment_lock:${request.idempotencyKey}"
        val isNew = redisTemplate.opsForValue().setIfAbsent(lockKey, "LOCKED", Duration.ofMinutes(10)) ?: false
        if (!isNew) throw RuntimeException("Transação em processamento.")

        val payment = Payment(amount = request.amount, currency = request.currency, idempotencyKey = request.idempotencyKey)
        val savedPayment = paymentRepository.save(payment)

        gatewayClient.callExternalProvider(savedPayment.id)

        return PaymentResponse(savedPayment.id, "ACCEPTED")
    }
}