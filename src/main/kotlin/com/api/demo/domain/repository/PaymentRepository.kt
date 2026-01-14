package com.api.demo.domain.repository

import api.kotlin.demo.domain.model.Payment

import java.util.UUID

interface PaymentRepository {
    fun save(payment: Payment): Payment
    fun findById(id: UUID): Payment?
    fun findByIdempotencyKey(key: String): Payment?
}