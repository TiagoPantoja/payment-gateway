package com.api.demo.application.usecase

import com.api.demo.application.dto.PaymentResponse
import com.api.demo.domain.repository.PaymentRepository

import org.springframework.stereotype.Service

import java.util.UUID

@Service
class GetPaymentStatusUseCase(private val paymentRepository: PaymentRepository) {

    fun execute(id: UUID): PaymentResponse {
        val payment = paymentRepository.findById(id)
            ?: throw RuntimeException("Pagamento não encontrado")

        return PaymentResponse(payment.id, payment.status.name)
    }
}