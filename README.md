# API de Votacao Cooperativa

API REST para gerenciar sessoes de votacao de pautas em cooperativas. Desenvolvida como desafio tecnico backend.

## Tecnologias

- **Java 17** + **Spring Boot 3.2.3**
- **PostgreSQL 15** com **Flyway** (migrations)
- **Docker & Docker Compose**
- **JUnit 5 + Mockito** (32 testes)
- **Springdoc OpenAPI** (Swagger UI)
- **Bucket4j** (rate limiting)
- **Lombok**

## Como executar

### Opcao 1: Com Docker (recomendado)

> Requisitos: **Docker** e **Docker Compose** instalados.

Basta um comando. O Docker cuida de subir o banco PostgreSQL e a aplicacao:

```bash
docker compose up --build
```

Aguarde ate ver no log:
```
votacao-app  | Started VotacaoApplication
```

| Servico     | URL                                    |
|-------------|----------------------------------------|
| API         | http://localhost:8081/api/v1/pautas     |
| Swagger UI  | http://localhost:8081/swagger-ui.html   |
| PostgreSQL  | localhost:5433 (usuario: postgres, senha: postgres) |

Para parar:
```bash
docker compose down        # mantem os dados do banco
docker compose down -v     # remove os dados do banco
```

---

### Opcao 2: Sem Docker (direto na maquina)

#### Requisitos

| Requisito       | Versao minima |
|-----------------|---------------|
| Java (JDK)      | 17            |
| Maven           | 3.9+          |
| PostgreSQL      | 12+           |

> O projeto inclui o Maven Wrapper (`mvnw`), entao nao e obrigatorio ter o Maven instalado globalmente.

#### Passo 1: Criar o banco de dados

Certifique-se de que o PostgreSQL esta rodando na porta 5432 e execute:

```bash
psql -U postgres -c "CREATE DATABASE votacao;"
```

Ou via `createdb` se disponivel:
```bash
createdb -U postgres votacao
```

#### Passo 2: Configurar credenciais (se necessario)

Por padrao, a aplicacao conecta com:
- **Host:** localhost
- **Porta:** 5432
- **Banco:** votacao
- **Usuario:** postgres
- **Senha:** postgres

Se suas credenciais forem diferentes, exporte as variaveis de ambiente antes de rodar:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=votacao
export DB_USER=seu_usuario
export DB_PASSWORD=sua_senha
```

#### Passo 3: Rodar a aplicacao

```bash
./mvnw spring-boot:run
```

Aguarde ate ver no log:
```
Started VotacaoApplication
```

| Servico     | URL                                    |
|-------------|----------------------------------------|
| API         | http://localhost:8080/api/v1/pautas     |
| Swagger UI  | http://localhost:8080/swagger-ui.html   |

> As migrations do Flyway sao executadas automaticamente na primeira inicializacao.

---

### Executar testes

```bash
./mvnw test
```

Os testes usam **H2 em modo PostgreSQL** (em memoria). Nao e necessario ter banco instalado para rodar os testes.

## Endpoints

Base URL: `http://localhost:8081/api/v1` (Docker) ou `http://localhost:8080/api/v1` (local)

| Metodo | Endpoint                          | Descricao                    |
|--------|-----------------------------------|------------------------------|
| POST   | `/api/v1/pautas`                  | Cadastrar nova pauta         |
| POST   | `/api/v1/pautas/{id}/sessoes`     | Abrir sessao de votacao      |
| POST   | `/api/v1/pautas/{id}/votos`       | Registrar voto               |
| GET    | `/api/v1/pautas/{id}/resultado`   | Consultar resultado          |

> O `{id}` nos endpoints e um UUID publico. O id interno (BIGINT) e usado apenas nos relacionamentos do banco.

### Exemplos de uso

**Criar pauta:**
```bash
curl -X POST http://localhost:8081/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo": "Reforma do estatuto", "descricao": "Alteracoes no estatuto social"}'
```

Resposta (201):
```json
{
  "id": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "titulo": "Reforma do estatuto",
  "descricao": "Alteracoes no estatuto social",
  "createdAt": "2026-03-28T19:00:00.000"
}
```

**Abrir sessao (120 segundos):**
```bash
curl -X POST http://localhost:8081/api/v1/pautas/{id}/sessoes \
  -H "Content-Type: application/json" \
  -d '{"duracaoSegundos": 120}'
```

**Abrir sessao (padrao 60 segundos):**
```bash
curl -X POST http://localhost:8081/api/v1/pautas/{id}/sessoes
```

**Votar:**
```bash
curl -X POST http://localhost:8081/api/v1/pautas/{id}/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId": "assoc-001", "voto": "SIM"}'
```

**Votar com CPF (bonus):**
```bash
curl -X POST http://localhost:8081/api/v1/pautas/{id}/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId": "assoc-001", "voto": "SIM", "cpf": "52998224725"}'
```

**Consultar resultado:**
```bash
curl http://localhost:8081/api/v1/pautas/{id}/resultado
```

Resposta (200):
```json
{
  "pautaId": "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
  "tituloPauta": "Reforma do estatuto",
  "totalSim": 15,
  "totalNao": 8,
  "totalVotos": 23,
  "resultado": "APROVADA"
}
```

## Swagger UI

Disponivel em: `http://localhost:8081/swagger-ui.html`

## Decisoes de Arquitetura

### Arquitetura em camadas
`controller -> service -> repository` com interfaces nos services (DIP). Cada camada tem responsabilidade clara.

### Design Patterns aplicados

| Pattern | Onde | Justificativa |
|---------|------|---------------|
| **Strategy** | `CpfValidationStrategy` | Desacoplar validacao de CPF da logica de voto. Permite trocar implementacao fake por HTTP real sem alterar o service |
| **Strategy** | `ResultadoCalculatorStrategy` | Isolar regra de apuracao. Permite trocar maioria simples por maioria qualificada sem modificar o PautaService |
| **Service Layer** | Todos os services | Regras de negocio isoladas da camada HTTP |
| **DTO Pattern** | Request/Response DTOs | Separar representacao da API do modelo de dominio |
| **Repository Pattern** | Spring Data JPA | Abstrai acesso a dados |

### SOLID

- **S**: Controller fino, services focados, mappers isolados
- **O**: Strategies permitem extensao sem modificacao
- **L**: Implementacoes substituiveis via interface
- **I**: Interfaces segregadas por responsabilidade
- **D**: Services sao interfaces; controller depende de abstracoes

### Identificadores publicos (UUID)

A API expoe UUIDs como identificadores publicos nos endpoints e respostas. Internamente, os relacionamentos e queries usam BIGINT auto-increment para performance. Isso evita expor IDs sequenciais (previssiveis) e melhora a seguranca da API.

### Versionamento da API (`/api/v1`)

Estrategia de versionamento via path. Quando houver breaking changes, cria-se `/api/v2` mantendo `/api/v1` em paralelo.

### Validacao de CPF (Bonus)

Implementado como `CpfValidatorClient` que implementa `CpfValidationStrategy`. Valida formato e digitos verificadores conforme regra da Receita Federal. Rejeita CPFs com todos os digitos iguais. Em producao, basta criar outra implementacao da interface e marca-la como `@Primary`.

### Protecao contra abuso

- **Rate Limiting**: 20 requisicoes por minuto por IP via Bucket4j
- **Validacao de tamanho**: `@Size` nos DTOs para prevenir payloads abusivos
- **Bean Validation**: `@NotBlank`, `@NotNull` nos campos obrigatorios
- **SQL Injection**: prevenido nativamente pelo JPA/Hibernate (queries parametrizadas)

### Performance

- **Indice em `voto.pauta_id`**: acelera contagem e verificacao de duplicidade
- **Indice em `sessao_votacao.pauta_id`**: acelera busca de sessao
- **Indice em `uuid`**: busca por identificador publico com custo O(log n)
- **UNIQUE `(pauta_id, associado_id)`**: unicidade garantida no banco
- **COUNT no banco**: resultado calculado via SQL agregado, sem carregar votos em memoria
- **EXISTS para duplicidade**: mais eficiente que SELECT + contagem

### Tratamento de erros

`@RestControllerAdvice` centralizado com respostas padronizadas incluindo `timestamp`, `status`, `error`, `message` e `path`:

| Excecao | HTTP Status |
|---------|-------------|
| `NotFoundException` | 404 |
| `InvalidCpfException` | 400 |
| `BusinessException` | 422 |
| `MethodArgumentNotValidException` | 400 |
| `DataIntegrityViolationException` | 409 |
| Rate limit excedido | 429 |

## Estrutura do Projeto

```
src/main/java/com/cooperativa/votacao/
├── VotacaoApplication.java
├── client/
│   ├── CpfValidationStrategy.java          # Interface (Strategy)
│   └── CpfValidatorClient.java             # Implementacao fake
├── config/
│   ├── RateLimitInterceptor.java           # Rate limiting por IP
│   └── WebConfig.java                      # Registro do interceptor
├── controller/
│   └── PautaController.java               # Endpoints REST
├── dto/
│   ├── PautaRequest.java / PautaResponse.java
│   ├── SessaoRequest.java / SessaoResponse.java
│   ├── VotoRequest.java / VotoResponse.java
│   ├── ResultadoResponse.java
│   ├── CpfValidationResponse.java
│   └── ErrorResponse.java
├── entity/
│   ├── Pauta.java
│   ├── SessaoVotacao.java
│   └── Voto.java
├── enums/
│   ├── VotoEnum.java                       # SIM, NAO
│   ├── StatusCpf.java                      # ABLE_TO_VOTE, UNABLE_TO_VOTE
│   └── SituacaoResultado.java              # APROVADA, REPROVADA, EMPATE
├── exception/
│   ├── GlobalExceptionHandler.java         # @ControllerAdvice
│   ├── BusinessException.java
│   ├── NotFoundException.java
│   └── InvalidCpfException.java
├── mapper/
│   ├── PautaMapper.java
│   ├── SessaoMapper.java
│   └── VotoMapper.java
├── repository/
│   ├── PautaRepository.java
│   ├── SessaoVotacaoRepository.java
│   └── VotoRepository.java
└── service/
    ├── PautaService.java                   # Interface
    ├── PautaServiceImpl.java               # Implementacao
    ├── SessaoVotacaoService.java           # Interface
    ├── SessaoVotacaoServiceImpl.java       # Implementacao
    ├── VotoService.java                    # Interface
    ├── VotoServiceImpl.java                # Implementacao
    ├── ResultadoCalculatorStrategy.java    # Interface (Strategy)
    └── MaioriaSimplesCalculatorStrategy.java
```

## Testes

32 testes cobrindo:

| Classe | Testes | Cobertura |
|--------|--------|-----------|
| PautaControllerTest | 7 | Fluxo completo via MockMvc (criar, sessao, votar, resultado, duplicidade, 404) |
| PautaServiceTest | 6 | Criar pauta, resultado aprovada/reprovada/empate/sem votos, pauta inexistente |
| SessaoVotacaoServiceTest | 3 | Duracao padrao, customizada, sessao duplicada |
| VotoServiceTest | 4 | Voto com sucesso, sessao fechada, voto duplicado, CPF inapto |
| CpfValidatorClientTest | 7 | CPF valido, mascara, nulo, digitos errados, todos iguais, formato invalido |
| MaioriaSimplesCalculatorStrategyTest | 4 | Aprovada, reprovada, empate, sem votos |
| VotacaoApplicationTests | 1 | Context load |

## Melhorias futuras

- Implementacao real do client de CPF via HTTP (substituir o fake)
- Mensageria com RabbitMQ/Kafka para notificar resultado ao encerrar sessao
- Cache com Redis para resultados ja encerrados
- Paginacao no endpoint de listagem de pautas
- Autenticacao via JWT para identificar associados
- Scheduler para fechar sessoes expiradas automaticamente
