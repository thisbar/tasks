# Tasks API

Small REST API to manage **Tasks**. It uses in-memory storage, simple validation, and HTTP Basic Auth. I kept the code intentionally straightforward but clean.

## What’s inside

- **Java 21 / Spring Boot 3** + **Maven**
- **Hexagonal architecture** (ports & adapters), **SOLID** principles, and **basic DDD**
- Value Objects for input (`TaskTitle`, `TaskDescription`, `TaskId`) with an error-bag (`Validated`, `ValidationError`)
- Single-action controllers per endpoint
- Search with filtering by status and with pagination (criteria/specification pattern)
- Global error handler with consistent JSON errors
- Basic Auth (see “Auth” below)

---

## Run it

### Using Docker (recommended)

This repo includes a **multi-stage Dockerfile** and a **simple Makefile** that builds and runs everything inside Docker (no local JDK needed).

```bash
# build image and run container on :8080
make start

# follow logs
make logs

# stop & remove container
make stop
```

If you need just the image:

```bash
make image
```

### Run locally (optional)

If you prefer to run it without Docker:

```bash
./mvnw spring-boot:run
# or
./mvnw -DskipTests clean package
java -jar target/*.jar
```

---

## Test & lint

All of this runs **inside a Maven+JDK Docker image** so you don’t need a local JDK.

```bash
# run unit + integration tests
make test

# style checks
make lint
```

**Cleanup:**

```bash
make clean
```

---

## Auth

The API is protected with **HTTP Basic Auth**. Default credentials (change them if you modified `SecurityConfiguration` or `application.properties`):

```
admin / secret
```

Example with curl:

```bash
curl -i -u admin:secret http://localhost:8080/tasks
```

---

## Endpoints

- `POST /tasks` – create a task
- `GET /tasks` – list tasks
- `GET /tasks/{id}` – get a task
- `PUT /tasks/{id}` – update a task
- `DELETE /tasks/{id}` – delete a task

### Model

```json
{
  "id": "uuid",
  "title": "string (required, max 100)",
  "description": "string | null",
  "status": "PENDING | IN_PROGRESS | DONE",
  "createdAt": "ISO-8601",
  "updatedAt": "ISO-8601"
}
```

### Validation & business rules

- Title: **required**, not blank, **≤ 100** chars → **400 Bad Request** with an array of `ValidationError`
- Business rule: **cannot mark DONE if still IN_PROGRESS** → **400 Bad Request**
- Not found → **404**
- Delete success → **204 No Content**
- Invalid UUID in `{id}` → **400 Bad Request**

### Sample calls

Create:
```bash
curl -i -u admin:secret   -H "Content-Type: application/json"   -d '{"title":"My task","description":"optional"}'   http://localhost:8080/tasks
```

List:
```bash
curl -s -u admin:secret http://localhost:8080/tasks?status=PENDING&limit=25&offset=0
```

Get by id:
```bash
curl -s -u admin:secret http://localhost:8080/tasks/<id>
```

Update:
```bash
curl -i -u admin:secret -X PUT   -H "Content-Type: application/json"   -d '{"title":"Renamed","description":"","status":"IN_PROGRESS"}'   http://localhost:8080/tasks/<id>
```

Delete:
```bash
curl -i -u admin:secret -X DELETE http://localhost:8080/tasks/<id>
```

---

## Error format

Validation errors (400):
```json
[
  { "code": "title.blank", "message": "Title is required", "field": "title" },
  { "code": "title.too_long", "message": "Max 100 characters", "field": "title" }
]
```

Domain rule / not found:
```json
{ "code": "task.not_found", "message": "Task not found" }
```

---

## Architecture

I used **Hexagonal architecture** to keep the domain independent of the framework:

- **Domain**: Entities/aggregates (`Task`), value objects (`TaskTitle`, `TaskDescription`, `TaskId`), domain rules (e.g. `InvalidTransition`).
- **Application**: Use cases (`TaskCreator`, `TaskCriteriaSearcher`, `TaskFinder`, `TaskUpdater`, `TaskDeleter`). They depend on ports (interfaces) like `TaskRepository`.
- **Infrastructure**: Adapters (HTTP controllers, DTOs, mappers), in-memory repository, security config, exception handler.

**SOLID**:  
- Single Responsibility: VOs only validate themselves; controllers only map HTTP ↔ domain; use cases orchestrate.  
- Open/Closed: new errors/domain rules via `DomainError` without touching adapters.  
- Dependency Inversion: application depends on `TaskRepository` port; infra provides the in-memory adapter.

**Basic DDD**:  
- Aggregate root `Task`, value objects for inputs, domain invariants (status transitions), and a thin ubiquitous language in names.

---

## Project structure

```
src/
 ├─ main/java/com/kiosite/tasks
 │   ├─ application/         # use cases
 │   ├─ domain/              # Task, VOs, domain errors
 │   └─ infrastructure/
 │       ├─ http/            # controllers + DTOs + mappers
 │       ├─ security/        # Basic Auth
 │       └─ persistence/     # in-memory repo
 └─ test/java/...            # unit + integration tests
```

---

## Makefile

- `make image` – builds the Docker image (the code compiles **inside** Docker)
- `make start` – runs the container on `localhost:8080`
- `make logs` – follows container logs
- `make stop` – stops & removes the container
- `make test` – runs `mvn test` in a Maven+JDK Docker image (with cached `.m2`)
- `make lint` – runs Spotless / Checkstyle in Docker
- `make clean` – cleans Maven build (in Docker) and removes any running container

---

## Notes
- If you’re using an IDE packaged as Flatpak and it can’t see `docker`, run `make ... DOCKER="flatpak-spawn --host docker"` or use a regular system terminal.
