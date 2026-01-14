package com.api.demo.infrastructure.web

import com.api.demo.application.dto.PaymentRequest
import com.api.demo.application.dto.PaymentResponse
import com.api.demo.application.usecase.GetPaymentStatusUseCase
import com.api.demo.application.usecase.ProcessPaymentUseCase

import org.springframework.http.HttpStatus

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

import java.util.UUID

@RestController
@RequestMapping("/v1/payments")
class PaymentController(
    private val processPaymentUseCase: ProcessPaymentUseCase,
    private val getStatusUseCase: GetPaymentStatusUseCase
) {

    @PostMapping
    fun create(@RequestBody request: PaymentRequest): ResponseEntity<PaymentResponse> {
        val response = processPaymentUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response)
    }

    // NOVO: Verificação de Status
    @GetMapping("/{id}")
    fun getStatus(@PathVariable id: UUID): ResponseEntity<PaymentResponse> {
        return ResponseEntity.ok(getStatusUseCase.execute(id))
    }

    @PatchMapping("/{id}/cancel")
    fun cancel(@PathVariable id: UUID): ResponseEntity<Void> {
        return ResponseEntity.noContent().build()
    }
}