# Task Manager API

API REST para gestión de tareas construida con **Java 17**, **Spring Boot 3**, **PostgreSQL** y **Docker**.

> Proyecto de portfolio — CRUD completo con tests, validación, manejo de errores y despliegue con Docker Compose.

---

## Arquitectura

```
Cliente (curl / Swagger UI)
        |
        v
+------------------+
|  TaskController   |  <-- @RestController, validación con @Valid
+------------------+
        |
        v
+------------------+
|   TaskService     |  <-- Lógica de negocio, filtrado
+------------------+
        |
        v
+------------------+
|  TaskRepository   |  <-- Spring Data JPA (queries derivadas)
+------------------+
        |
        v
+------------------+
|   PostgreSQL 16   |  <-- Docker / H2 en tests
+------------------+
```

## Tecnologías

| Categoría       | Tecnología                            |
|-----------------|---------------------------------------|
| Lenguaje        | Java 17                               |
| Framework       | Spring Boot 3.2                       |
| Persistencia    | Spring Data JPA + PostgreSQL 16       |
| Validación      | Bean Validation (jakarta.validation)  |
| Documentación   | SpringDoc OpenAPI (Swagger UI)        |
| Testing         | JUnit 5 + Mockito + AssertJ + H2     |
| Infraestructura | Docker + Docker Compose               |

## Endpoints

| Método | Ruta              | Descripción                               | Status |
|--------|-------------------|-------------------------------------------|--------|
| GET    | `/api/tasks`        | Listar tareas (filtros: status, priority) | 200    |
| GET    | `/api/tasks/{id}`   | Obtener tarea por ID                      | 200    |
| POST   | `/api/tasks`        | Crear nueva tarea                         | 201    |
| PUT    | `/api/tasks/{id}`   | Actualizar tarea existente                | 200    |
| DELETE | `/api/tasks/{id}`   | Eliminar tarea                            | 204    |

### Ejemplo de respuesta

```json
{
  "id": 1,
  "title": "Revisar pull request",
  "description": "Revisar PR #42 del equipo de backend",
  "status": "PENDING",
  "priority": "HIGH",
  "createdAt": "2026-02-12T10:30:00",
  "updatedAt": "2026-02-12T10:30:00"
}
```

**Estados posibles:** `PENDING` | `IN_PROGRESS` | `DONE`
**Prioridades:** `LOW` | `MEDIUM` | `HIGH`

## Requisitos previos

- Java 17+
- Maven 3.8+
- Docker y Docker Compose

## Ejecución

### Con Docker Compose (recomendado)

```bash
docker compose up --build
```

La API estará disponible en http://localhost:8080

Swagger UI en http://localhost:8080/swagger-ui.html

### Sin Docker

1. Tener PostgreSQL corriendo en `localhost:5432` con base de datos `taskmanager`.
2. Ejecutar:

```bash
mvn spring-boot:run
```

## Tests

```bash
mvn test
```

| Tipo        | Clase                              | Tests | Herramientas       |
|-------------|-------------------------------------|-------|--------------------|
| Unitarios   | `TaskServiceTest`                    | 10    | Mockito + AssertJ  |
| Integración | `TaskControllerIntegrationTest`      | 12    | MockMvc + H2       |

## Estructura del proyecto

```
src/main/java/com/cayetanobp/taskmanager/
├── controller/     # Endpoints REST
├── dto/            # Records de entrada/salida
├── exception/      # Manejo global de errores (@RestControllerAdvice)
├── model/          # Entidad JPA + enums (TaskStatus, Priority)
├── repository/     # Spring Data JPA
└── service/        # Lógica de negocio
```

## Qué aprendí con este proyecto

- Diseñar una API REST siguiendo convenciones HTTP (códigos de estado, verbos, rutas)
- Separar responsabilidades en capas (Controller → Service → Repository)
- Usar **Java Records** como DTOs inmutables
- Validar entrada con Bean Validation y devolver errores estructurados
- Escribir tests unitarios con Mockito (aislando dependencias) y tests de integración con MockMvc
- Configurar **perfiles de Spring** (default, docker, test) para distintos entornos
- Contenerizar una aplicación con Docker multi-stage build y orquestarla con Compose

