package com.cayetanobp.taskmanager.service;

import com.cayetanobp.taskmanager.dto.CreateTaskRequest;
import com.cayetanobp.taskmanager.dto.TaskResponse;
import com.cayetanobp.taskmanager.dto.UpdateTaskRequest;
import com.cayetanobp.taskmanager.exception.TaskNotFoundException;
import com.cayetanobp.taskmanager.model.Priority;
import com.cayetanobp.taskmanager.model.Task;
import com.cayetanobp.taskmanager.model.TaskStatus;
import com.cayetanobp.taskmanager.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository repository;

    @InjectMocks
    private TaskService service;

    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleTask = new Task();
        sampleTask.setId(1L);
        sampleTask.setTitle("Tarea de prueba");
        sampleTask.setDescription("Descripcion de prueba");
        sampleTask.setStatus(TaskStatus.PENDING);
        sampleTask.setPriority(Priority.MEDIUM);
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        @DisplayName("devuelve todas las tareas sin filtros")
        void returnsAllTasksWithoutFilters() {
            when(repository.findAll()).thenReturn(List.of(sampleTask));

            List<TaskResponse> result = service.findAll(null, null);

            assertThat(result).hasSize(1);
            assertThat(result.get(0).title()).isEqualTo("Tarea de prueba");
            verify(repository).findAll();
        }

        @Test
        @DisplayName("filtra por estado")
        void filtersByStatus() {
            when(repository.findByStatus(TaskStatus.PENDING)).thenReturn(List.of(sampleTask));

            List<TaskResponse> result = service.findAll(TaskStatus.PENDING, null);

            assertThat(result).hasSize(1);
            verify(repository).findByStatus(TaskStatus.PENDING);
        }

        @Test
        @DisplayName("filtra por prioridad")
        void filtersByPriority() {
            when(repository.findByPriority(Priority.HIGH)).thenReturn(List.of());

            List<TaskResponse> result = service.findAll(null, Priority.HIGH);

            assertThat(result).isEmpty();
            verify(repository).findByPriority(Priority.HIGH);
        }

        @Test
        @DisplayName("filtra por estado y prioridad")
        void filtersByStatusAndPriority() {
            when(repository.findByStatusAndPriority(TaskStatus.PENDING, Priority.MEDIUM))
                    .thenReturn(List.of(sampleTask));

            List<TaskResponse> result = service.findAll(TaskStatus.PENDING, Priority.MEDIUM);

            assertThat(result).hasSize(1);
            verify(repository).findByStatusAndPriority(TaskStatus.PENDING, Priority.MEDIUM);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        @DisplayName("devuelve tarea cuando existe")
        void returnsTaskWhenExists() {
            when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));

            TaskResponse result = service.findById(1L);

            assertThat(result.id()).isEqualTo(1L);
            assertThat(result.title()).isEqualTo("Tarea de prueba");
        }

        @Test
        @DisplayName("lanza excepcion cuando no existe")
        void throwsWhenNotFound() {
            when(repository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.findById(99L))
                    .isInstanceOf(TaskNotFoundException.class)
                    .hasMessageContaining("99");
        }
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        @DisplayName("crea tarea con prioridad por defecto")
        void createsWithDefaultPriority() {
            CreateTaskRequest request = new CreateTaskRequest("Nueva tarea", "Desc", null);
            when(repository.save(any(Task.class))).thenReturn(sampleTask);

            TaskResponse result = service.create(request);

            assertThat(result).isNotNull();
            verify(repository).save(any(Task.class));
        }

        @Test
        @DisplayName("crea tarea con prioridad especificada")
        void createsWithGivenPriority() {
            CreateTaskRequest request = new CreateTaskRequest("Urgente", "Desc", Priority.HIGH);
            when(repository.save(any(Task.class))).thenReturn(sampleTask);

            service.create(request);

            verify(repository).save(argThat(task -> task.getPriority() == Priority.HIGH));
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        @DisplayName("actualiza solo campos no nulos")
        void updatesOnlyNonNullFields() {
            when(repository.findById(1L)).thenReturn(Optional.of(sampleTask));
            when(repository.save(any(Task.class))).thenReturn(sampleTask);

            UpdateTaskRequest request = new UpdateTaskRequest("Nuevo titulo", null, null, null);
            service.update(1L, request);

            verify(repository).save(argThat(task ->
                    task.getTitle().equals("Nuevo titulo") &&
                    task.getDescription().equals("Descripcion de prueba")
            ));
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("elimina tarea existente")
        void deletesExistingTask() {
            when(repository.existsById(1L)).thenReturn(true);

            service.delete(1L);

            verify(repository).deleteById(1L);
        }

        @Test
        @DisplayName("lanza excepcion si tarea no existe")
        void throwsWhenNotExists() {
            when(repository.existsById(99L)).thenReturn(false);

            assertThatThrownBy(() -> service.delete(99L))
                    .isInstanceOf(TaskNotFoundException.class);
        }
    }
}
