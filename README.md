# API de Votação Cooperativa

API REST para gerenciar sessões de votação de pautas em um contexto cooperativista.

## Tecnologias

- **Java 17**
- **Spring Boot 3.2.3**
- **PostgreSQL 15**
- **Flyway** (versionamento de banco)
- **Docker & Docker Compose**
- **JUnit 5 + Mockito** (testes)
- **Springdoc OpenAPI** (Swagger UI)

## Como executar

### Com Docker (recomendado)

```bash
docker compose up --build
```

A aplicação estará disponível em `http://localhost:8080`.

### Sem Docker (desenvolvimento local)

Requisitos: Java 17, Maven 3.9+, PostgreSQL rodando na porta 5432.

```bash
# Criar banco
createdb votacao

# Rodar aplicação
./mvnw spring-boot:run
```

## Endpoints

Base URL: `http://localhost:8080/api/v1`

| Método | Endpoint                          | Descrição                    |
|--------|-----------------------------------|------------------------------|
| POST   | `/api/v1/pautas`                  | Cadastrar nova pauta         |
| POST   | `/api/v1/pautas/{id}/sessoes`     | Abrir sessão de votação      |
| POST   | `/api/v1/pautas/{id}/votos`       | Registrar voto               |
| GET    | `/api/v1/pautas/{id}/resultado`   | Consultar resultado          |

### Exemplos de uso

**Criar pauta:**
```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo": "Reforma do estatuto", "descricao": "Votação sobre alterações no estatuto social"}'
```

**Abrir sessão (5 minutos):**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessoes \
  -H "Content-Type: application/json" \
  -d '{"duracaoMinutos": 5}'
```

**Abrir sessão (padrão 1 minuto):**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessoes
```

**Votar:**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId": "assoc-001", "voto": "SIM"}'
```

**Votar com CPF (bônus):**
```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId": "assoc-001", "voto": "SIM", "cpf": "12345678901"}'
```

**Consultar resultado:**
```bash
curl http://localhost:8080/api/v1/pautas/1/resultado
```

## Swagger UI

Disponível em: `http://localhost:8080/swagger-ui.html`

## Testes

```bash
./mvnw test
```

Os testes usam H2 em modo PostgreSQL, sem necessidade de banco externo.

## Decisões de Arquitetura

### Arquitetura por camadas
Organização simples: `controller → service → repository`. Cada camada tem responsabilidade clara, sem abstrações desnecessárias.

### Relacionamento Pauta ↔ SessaoVotacao (1:1)
Optei por `@OneToOne` com constraint `UNIQUE` no `pauta_id` da sessão. Uma pauta tem no máximo uma sessão de votação. Essa decisão simplifica a modelagem e reflete a regra de negócio — se futuramente fosse necessário reabrir votação, bastaria mudar para `@OneToMany`.

### Versionamento da API (`/api/v1`)
Estratégia de versionamento via path (URI versioning). É a abordagem mais explícita e fácil de rotear. Quando houver breaking changes, cria-se `/api/v2` com novo controller, mantendo `/api/v1` em paralelo até a depreciação.

### Validação de CPF (Bônus 1)
Implementado como `CpfValidatorClient` — um componente fake que simula serviço externo. Valida formato (11 dígitos) e retorna aleatoriamente `ABLE_TO_VOTE` ou `UNABLE_TO_VOTE`. Em produção, seria substituído por uma chamada HTTP real (RestTemplate/WebClient).

### Performance para alto volume (Bônus 2)
- **Índice em `voto.pauta_id`**: acelera contagem de votos e verificação de duplicidade.
- **Índice em `sessao_votacao.pauta_id`**: acelera busca de sessão por pauta.
- **Constraint UNIQUE `(pauta_id, associado_id)`**: garante unicidade no banco, independente da aplicação.
- **Consultas agregadas com COUNT no banco**: o resultado é calculado com `COUNT` direto no SQL — nunca carrega votos em memória.
- **Verificação de duplicidade com `EXISTS`**: mais eficiente que carregar o voto.

### Tratamento de exceções
`@RestControllerAdvice` centralizado com handlers para:
- `NotFoundException` → 404
- `BusinessException` → 422
- `InvalidCpfException` → 404
- `MethodArgumentNotValidException` → 400
- `DataIntegrityViolationException` → 409
- `Exception` genérica → 500

## Estrutura do Projeto

```
src/main/java/com/cooperativa/votacao/
├── VotacaoApplication.java
├── client/
│   └── CpfValidatorClient.java
├── config/
├── controller/
│   └── PautaController.java
├── dto/
│   ├── CpfValidationResponse.java
│   ├── ErrorResponse.java
│   ├── PautaRequest.java
│   ├── PautaResponse.java
│   ├── ResultadoResponse.java
│   ├── SessaoRequest.java
│   ├── SessaoResponse.java
│   ├── VotoRequest.java
│   └── VotoResponse.java
├── entity/
│   ├── Pauta.java
│   ├── SessaoVotacao.java
│   └── Voto.java
├── enums/
│   ├── StatusCpf.java
│   └── VotoEnum.java
├── exception/
│   ├── BusinessException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCpfException.java
│   └── NotFoundException.java
├── mapper/
│   ├── PautaMapper.java
│   ├── SessaoMapper.java
│   └── VotoMapper.java
├── repository/
│   ├── PautaRepository.java
│   ├── SessaoVotacaoRepository.java
│   └── VotoRepository.java
└── service/
    ├── PautaService.java
    ├── SessaoVotacaoService.java
    └── VotoService.java
```
