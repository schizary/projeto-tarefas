# projeto-tarefas

Aplicação para o gerenciamento de tarefas do dia a dia.

API REST em **Java 21 + Spring Boot 3.3** com persistência em **PostgreSQL**.
Sem login e sem conceito de usuário, conforme as regras de negócio do projeto.

---

## Padrão de projeto

Arquitetura **em camadas** com inversão de dependência entre elas:

```
TarefaController  ->  TarefaService (interface)  ->  TarefaRepository
                           |                              |
                      TarefaServiceImpl              Spring Data JPA
                           |
                      TarefaMapper  (DTO <-> entidade)
```

| Padrão | Onde aparece |
|---|---|
| Layered Architecture | pastas `controllers`, `services`, `repositories`, `models` |
| Service Layer + Dependency Inversion | `TarefaService` (contrato) e `TarefaServiceImpl` (implementação) |
| Repository | `TarefaRepository` sobre o Spring Data JPA |
| DTO | `TarefaRequest`, `StatusRequest`, `TarefaResponse` — a entidade nunca é exposta na API |
| Mapper | `TarefaMapper` concentra a conversão entre DTO e entidade |
| Rich Domain Model | `Tarefa` controla o próprio estado (`alterarDados`, `alterarStatus`, datas) |

Nomes de métodos, variáveis e mensagens em português; nomenclatura clássica de
framework (pastas, sufixos de classe, anotações) em inglês.

---

## Estrutura

```
projeto-tarefas/
├── database/
│   └── script_banco.sql              script de criação do banco
├── docker-compose.yml                PostgreSQL pronto para uso (opcional)
├── pom.xml
└── src/
    ├── main/java/com/projeto/tarefas/
    │   ├── config/OpenApiConfig.java
    │   ├── controllers/TarefaController.java
    │   ├── dtos/{TarefaRequest, StatusRequest, TarefaResponse}.java
    │   ├── exceptions/{RecursoNaoEncontradoException, ErroResponse, RestExceptionHandler}.java
    │   ├── mappers/TarefaMapper.java
    │   ├── models/{Tarefa, StatusTarefa}.java
    │   ├── repositories/TarefaRepository.java
    │   └── services/{TarefaService, impl/TarefaServiceImpl}.java
    ├── main/resources/application.yml
    └── test/java/com/projeto/tarefas/
        ├── controllers/TarefaControllerIT.java   testes de integração
        └── services/TarefaServiceImplTest.java   testes unitários
```

---

## Entidade

| Campo | Coluna | Tipo | Observação |
|---|---|---|---|
| id | `id` | BIGSERIAL | chave primária |
| nome | `nome` | VARCHAR(120) | obrigatório |
| descricao | `descricao` | VARCHAR(500) | |
| status | `status` | VARCHAR(20) | `PENDENTE`, `EM_ANDAMENTO`, `CONCLUIDA`, `CANCELADA` |
| observacoes | `observacoes` | VARCHAR(500) | |
| dataCriacao | `data_criacao` | TIMESTAMP | preenchida na criação |
| dataAtualizacao | `data_atualizacao` | TIMESTAMP | atualizada a cada alteração |

---

## Banco de dados

### Opção 1 — PostgreSQL já instalado

```bash
psql -U postgres -c "CREATE DATABASE projeto_tarefas;"
psql -U postgres -d projeto_tarefas -f database/script_banco.sql
```

### Opção 2 — Docker

```bash
docker compose up -d
```

O `docker-compose.yml` executa o script automaticamente na primeira subida.

### Conexão

Configurada em `src/main/resources/application.yml`, com variáveis de ambiente
e valores padrão:

| Variável | Padrão |
|---|---|
| `DB_HOST` | `localhost` |
| `DB_PORT` | `5432` |
| `DB_NAME` | `projeto_tarefas` |
| `DB_USER` | `postgres` |
| `DB_PASSWORD` | `postgres` |
| `SERVER_PORT` | `8080` |

O `ddl-auto` está em `validate`: o schema é responsabilidade do script, e a
aplicação apenas confere se ele bate com as entidades.

---

## Executando

Pré-requisitos: **JDK 21** e **Maven 3.9+** (ou o Maven embutido na IDE).

```bash
mvn spring-boot:run
```

Documentação interativa: <http://localhost:8080/swagger-ui.html>

---

## Testes

```bash
mvn test      # somente os unitarios (Surefire)
mvn verify    # unitarios + integracao (Surefire + Failsafe)
```

Os testes rodam sobre **H2 em memória** (perfil `test`), portanto não precisam
de PostgreSQL nem de Docker.

| Classe | Tipo | Testes |
|---|---|---|
| `TarefaServiceImplTest` | unitário, com Mockito | 10 |
| `TarefaControllerIT` | integração da API inteira (controller, serviço, repositório e banco) com MockMvc | 10 |
| `TarefasApplicationTests` | subida do contexto | 1 |

Classes terminadas em `IT` são executadas pelo **Failsafe**, no `verify`; as
terminadas em `Test` pelo **Surefire**, no `test`.

---

## Endpoints

| Método | Rota | Descrição | Resposta |
|---|---|---|---|
| `POST` | `/api/tarefas` | cria uma tarefa | `201 Created` |
| `PUT` | `/api/tarefas/{id}` | altera todos os dados | `200 OK` |
| `PATCH` | `/api/tarefas/{id}/status` | altera somente o status | `200 OK` |
| `DELETE` | `/api/tarefas/{id}` | deleta a tarefa | `204 No Content` |
| `GET` | `/api/tarefas/{id}` | busca por id | `200 OK` |
| `GET` | `/api/tarefas?status=PENDENTE` | lista, com filtro opcional | `200 OK` |

Erros saem padronizados: `404` para tarefa inexistente e `400` com a lista de
campos inválidos.

### Exemplo

```bash
curl -X POST http://localhost:8080/api/tarefas \
  -H "Content-Type: application/json" \
  -d '{
        "nome": "Comprar pão",
        "descricao": "Padaria da esquina",
        "status": "PENDENTE",
        "observacoes": "pela manhã"
      }'
```

```json
{
  "id": 1,
  "nome": "Comprar pão",
  "descricao": "Padaria da esquina",
  "status": "PENDENTE",
  "descricaoStatus": "Pendente",
  "observacoes": "pela manhã",
  "dataCriacao": "2026-10-03T17:20:11.482",
  "dataAtualizacao": "2026-10-03T17:20:11.482"
}
```
