# Payment Gateway

**Gateway de Pagamentos** desenvolvido em **Kotlin** e **Spring Boot**. O foco principal está no **processamento  de transações** e garantia de **consistência eventual**, **idempotência** e **resiliência crítica**.

## Problemas Reais Resolvidos

1. **Prevenção de Cobrança Dupla:** Implementação de **travas de idempotência** em múltiplas camadas com Redis.
2. **Resiliência contra Falhas Externas:** Uso do padrão **Circuit Breaker** para evitar que a indisponibilidade de um provedor cause um efeito cascata e derrube a API.
3. Arquitetura baseada em **Clean Architecture**, facilitando a troca de provedores sem afetar a regra de negócio.

## Stack do Projeto

* **Linguagem:** Kotlin (Java 21)
* **Framework:** Spring Boot 3.x
* **Persistência:** PostgreSQL & Spring Data JPA
* **Cache & Idempotência:** Redis
* **Resiliência:** Resilience4j (Circuit Breaker & Retry)
* **Testes:** MockK & JUnit 5
* **Containerização:** Docker & Docker Compose

## Decisões Arquiteturais

O projeto segue a **Clean Architecture**, dividida em:
- **Domain:** Entidades puras e contratos (interfaces).
- **Application:** Casos de uso que orquestram a lógica (Idempotência + Chamada ao Gateway).
- **Infrastructure:** Detalhes técnicos como persistência, clientes HTTP e configurações de resiliência.
---

## Monitoramento e Observabilidade

O projeto utiliza o **Spring Boot Actuator** com **Micrometer** para expor métricas operacionais. Monitorando a saúde da API e o desempenho das integrações em tempo real.

### Endpoints de Verificação:
* **Health Check:** `GET /actuator/health`
    * Fornece o status  da aplicação, incluindo conectividade com PostgreSQL, Redis e a saúde do Circuit Breaker.
* **Métricas Prometheus:** `GET /actuator/prometheus`
    * Expõe métricas para coleta pelo Prometheus (latência, taxa de erro, chamadas ao banco, etc).

### Verificadores Customizados:
O `GatewayHealthIndicator` realiza um *heartbeat* no provedor de pagamento. Se estiver indisponível, a API sinaliza o status `DOWN` ou `OUT_OF_SERVICE`.

---

## Como Rodar o Projeto

### 1. Pré-requisitos
* Docker e Docker Compose instalados.
* JDK 21 instalado (opcional se usar apenas Docker).

### 2. Subindo a Infraestrutura
Na raiz, inicie o banco de dados e o cache:
```bash
docker-compose up -d
```

### 3. Rodando a Aplicação
```bash
./gradlew bootRun
```
A API estará disponível em `http://localhost:8080`.

### 4. Testes
Para rodar os testes unitários:
```bash
./gradlew test
```

## Endpoints
**A. Processar Pagamento**: `POST /v1/payments`

**Header**: `Idempotency-Key: f47ac10b-58cc-4372-a567-0e02b2c3d479`
**Body**:
```json
{
  "amount": 250.00,
  "currency": "BRL",
  "idempotencyKey": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
}
```

**B. Consultar Status**: `GET /v1/payments/{id}`

**C. Teste de Idempotência**:
1. Envie o POST acima uma vez. (Status esperado: `202 Accepted`)
2. Envie o mesmo POST novamente com a mesma `Idempotency-Key`. (Status esperado: `409 Conflict` ou retorno do pagamento original).

## Dockerização
Para gerar a imagem Docker otimizada:
```bash
docker build -t payment-gateway .
```

