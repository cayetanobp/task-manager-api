INSERT INTO tasks (title, description, status, priority, created_at, updated_at) VALUES
('Revisar pull request', 'Revisar PR #42 del equipo de backend', 'PENDING', 'HIGH', NOW(), NOW()),
('Escribir tests unitarios', 'Cubrir el servicio de tareas con Mockito', 'IN_PROGRESS', 'HIGH', NOW(), NOW()),
('Configurar Docker Compose', 'Orquestar PostgreSQL y la API en contenedores', 'DONE', 'MEDIUM', NOW(), NOW()),
('Documentar endpoints', 'Escribir ejemplos de uso con curl en el README', 'DONE', 'MEDIUM', NOW(), NOW()),
('Implementar filtrado', 'Filtrar tareas por estado y prioridad via query params', 'DONE', 'LOW', NOW(), NOW()),
('Añadir Swagger UI', 'Integrar SpringDoc OpenAPI para documentacion interactiva', 'PENDING', 'MEDIUM', NOW(), NOW());
