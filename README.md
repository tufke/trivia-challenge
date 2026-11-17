# Architecture

This service follows a pragmatic Hexagonal Architecture approach.

The architecture is divided into three layers:

```text
Application
    ↓
Domain
    ↓
Infrastructure
```

---

## Application

The Application layer contains all incoming triggers.

Examples:

- REST Controllers
- Kafka Listeners
- Schedulers
- Batch Jobs

Responsibilities:

- Receive external input
- Perform transport-level validation
- Map DTOs to domain models
- Invoke use cases

Must not contain:

- Business logic
- Persistence logic
- Integration logic

Application owns its own models:

```text
StartQuizRequest
SubmitQuizRequest
QuizResponse
```

---

## Domain

The Domain layer contains all business logic.

Responsibilities:

- Implement use cases
- Execute business rules
- Define ports
- Define domain models
- Coordinate business processes

Contains:

```text
StartQuizService
SubmitQuizService
MaintainQuestionsService

Quiz
Question
Answer
QuizResult

StartQuizUseCase
SubmitQuizUseCase

QuizStoragePort
QuestionsStoragePort
```

Rules:

- All business logic belongs here.
- If business behaviour changes, the change should be made here.
- Domain must not depend on Application or Infrastructure.

---

## Infrastructure

The Infrastructure layer contains all technical implementations.

Examples:

```text
QuizStorageAdapter
QuestionsStorageAdapter
OpenTriviaDbAdapter
```

Responsibilities:

- Implement outgoing ports
- Integrate with databases
- Integrate with external APIs
- Implement caching
- Perform model mapping

Must not contain:

- Business rules
- Business decisions

Infrastructure owns its own models:

```text
QuizEntity
QuizQuestionEntity
QuestionEntity
```

---

## Ports

Ports are defined in Domain.

Incoming ports describe available use cases.

Examples:

```text
StartQuizUseCase
SubmitQuizUseCase
```

Outgoing ports describe required dependencies.

Examples:

```text
QuizStoragePort
QuestionsStoragePort
OpenTriviaDbPort
```

Infrastructure provides the implementations.

---

## Model Ownership

Each layer owns its own model.

```text
Application
    ↓
Request/Response DTOs

Domain
    ↓
Business Models

Infrastructure
    ↓
Entities / Integration Models
```

Models are mapped at layer boundaries.

Examples:

```text
SubmitQuizRequest
        ↓
Quiz
        ↓
QuizEntity
```

Direct sharing of Application or Infrastructure models across layers is not allowed.

---

## Adapter Boundary

Adapters form the boundary between Domain and Infrastructure.

Responsibilities:

- Convert Domain models to Infrastructure models
- Convert Infrastructure models to Domain models
- Hide technical implementation details

Example:

```text
Quiz
    ↕
QuizStorageAdapter
    ↕
QuizEntity
```

Infrastructure components should only work with Infrastructure models.

Domain components should only work with Domain models.

---

## Dependency Rule

Dependencies always point towards Domain.

Allowed:

```text
Application → Domain
Infrastructure → Domain
```

Not allowed:

```text
Domain → Application
Domain → Infrastructure
```

---

## Guiding Principle

When looking for business behaviour:

```text
Look in Domain Services.
```

When looking for input handling:

```text
Look in Application.
```

When looking for technical integrations:

```text
Look in Infrastructure.
```
