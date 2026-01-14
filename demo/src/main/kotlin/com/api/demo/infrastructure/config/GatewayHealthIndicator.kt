package com.api.demo.infrastructure.config

import org.springframework.boot.actuate.health.Health
import org.springframework.boot.actuate.health.HealthIndicator
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate

@Component
class GatewayHealthIndicator(private val restTemplate: RestTemplate) : HealthIndicator {

    override fun health(): Health {
        return try {
            val response = restTemplate.getForEntity("https://api.external-gateway.com/health", String::class.java)
            if (response.statusCode.is2xxSuccessful) Health.up().withDetail("gateway", "online").build()
            else Health.down().withDetail("gateway", "error code: ${response.statusCode}").build()
        } catch (ex: Exception) {
            Health.down(ex).withDetail("gateway", "unreachable").build()
        }
    }
}