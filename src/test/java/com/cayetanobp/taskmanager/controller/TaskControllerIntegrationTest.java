package com.cayetanobp.taskmanager.controller;

import com.cayetanobp.taskmanager.model.Priority;
import com.cayetanobp.taskmanager.model.Task;
import com.cayetanobp.taskmanager.model.TaskStatus;
import com.cayetanobp.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository repository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    private Task createSampleTask(String title, TaskStatus status, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription("Descripcion de " + title);
        task.setStatus(status);
        task.setPriority(priority);
        return repository.save(task);
    }

    @Nested
    @DisplayName("POST /api/tasks")
    class CreateTask {

        @Test
        @DisplayName("crea tarea con datos validos - 201")
        void createsTaskSuccessfully() throws Exception {
            String body = objectMapper.writeValueAsString(
                    Map.of("title", "Comprar leche", "description", "En el super", "priority", "LOW"));

            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNumber())
                    .andExpect(jsonPath("$.title").value("Comprar leche"))
                    .andExpect(jsonPath("$.status").value("PENDING"))
                    .andExpect(jsonPath("$.priority").value("LOW"));
        }

        @Test
        @DisplayName("rechaza titulo vacio - 400")
        void rejectsBlankTitle() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("title", ""));

            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.messages").isArray());
        }

        @Test
        @DisplayName("asigna prioridad MEDIUM por defecto")
        void assignsDefaultPriority() throws Exception {
            String body = objectMapper.writeValueAsString(Map.of("title", "Sin prioridad"));

            mockMvc.perform(post("/api/tasks")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.priority").value("MEDIUM"));
        }
    }

    @Nested
    @DisplayName("GET /api/tasks")
    class ListTasks {

        @Test
        @DisplayName("devuelve lista vacia")
        void returnsEmptyList() throws Exception {
            mockMvc.perform(get("/api/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("devuelve todas las tareas")
        void returnsAllTasks() throws Exception {
            createSampleTask("Tarea 1", TaskStatus.PENDING, Priority.LOW);
            createSampleTask("Tarea 2", TaskStatus.DONE, Priority.HIGH);

            mockMvc.perform(get("/api/tasks"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)));
        }

        @Test
        @DisplayName("filtra por estado")
        void filtersByStatus() throws Exception {
            createSampleTask("Pendiente", TaskStatus.PENDING, Priority.LOW);
            createSampleTask("Hecha", TaskStatus.DONE, Priority.LOW);

            mockMvc.perform(get("/api/tasks").param("status", "PENDING"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].title").value("Pendiente"));
        }
    }

    @Nested
    @DisplayName("GET /api/tasks/{id}")
    class GetById {

        @Test
        @DisplayName("devuelve tarea existente")
        void returnsExistingTask() throws Exception {
            Task task = createSampleTask("Existente", TaskStatus.PENDING, Priority.MEDIUM);

            mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Existente"));
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void returns404() throws Exception {
            mockMvc.perform(get("/api/tasks/{id}", 9999))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message").value(containsString("9999")));
        }
    }

    @Nested
    @DisplayName("PUT /api/tasks/{id}")
    class UpdateTask {

        @Test
        @DisplayName("actualiza campos de tarea existente")
        void updatesExistingTask() throws Exception {
            Task task = createSampleTask("Original", TaskStatus.PENDING, Priority.LOW);
            String body = objectMapper.writeValueAsString(
                    Map.of("title", "Actualizada", "status", "IN_PROGRESS"));

            mockMvc.perform(put("/api/tasks/{id}", task.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(body))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.title").value("Actualizada"))
                    .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/tasks/{id}")
    class DeleteTask {

        @Test
        @DisplayName("elimina tarea existente - 204")
        void deletesExistingTask() throws Exception {
            Task task = createSampleTask("A borrar", TaskStatus.PENDING, Priority.LOW);

            mockMvc.perform(delete("/api/tasks/{id}", task.getId()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/tasks/{id}", task.getId()))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("devuelve 404 si no existe")
        void returns404WhenNotExists() throws Exception {
            mockMvc.perform(delete("/api/tasks/{id}", 9999))
                    .andExpect(status().isNotFound());
        }
    }
}
