package com.api.demo.infrastructure.gateway

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry

import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpServerErrorException

import java.util.UUID

@Component
class ExternalPaymentGatewayClient(
    private val restTemplate: RestTemplate,
    @Value("\${gateway.api.url}") private val apiUrl: String,
    @Value("\${gateway.api.token}") private val apiToken: String
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @CircuitBreaker(name = "paymentProvider", fallbackMethod = "fallback")
    @Retry(name = "paymentProvider")
    fun callExternalProvider(paymentId: UUID) {
        logger.info("Iniciando chamada externa para autorização do pagamento: $paymentId")

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_JSON
            setBearerAuth(apiToken)
            set("X-Request-ID", UUID.randomUUID().toString())
        }

        val body = mapOf(
            "external_id" to paymentId.toString(),
            "callback_url" to "https://sua-api.com/v1/payments/callback"
        )

        val request = HttpEntity(body, headers)

        try {
            val response = restTemplate.postForEntity("$apiUrl/v1/authorize", request, String::class.java)

            if (response.statusCode.is2xxSuccessful) {
                logger.info("Pagamento $paymentId enviado com sucesso para o provedor.")
            }
        } catch (ex: HttpClientErrorException) {
            logger.error("Erro de cliente ao chamar gateway (4xx): ${ex.message}")
            throw ex
        } catch (ex: HttpServerErrorException) {
            logger.error("Erro no servidor do gateway (5xx): ${ex.message}")
            throw ex
        }
    }

    fun fallback(paymentId: UUID, ex: Throwable) {
        logger.error("CRITICAL: Gateway Externo indisponível após retentativas. Pagamento $paymentId ficará PENDENTE para análise manual. Causa: ${ex.message}")

    }
}