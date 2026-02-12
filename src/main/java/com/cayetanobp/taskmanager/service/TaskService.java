package com.cayetanobp.taskmanager.service;

import com.cayetanobp.taskmanager.dto.CreateTaskRequest;
import com.cayetanobp.taskmanager.dto.TaskResponse;
import com.cayetanobp.taskmanager.dto.UpdateTaskRequest;
import com.cayetanobp.taskmanager.exception.TaskNotFoundException;
import com.cayetanobp.taskmanager.model.Priority;
import com.cayetanobp.taskmanager.model.Task;
import com.cayetanobp.taskmanager.model.TaskStatus;
import com.cayetanobp.taskmanager.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    public List<TaskResponse> findAll(TaskStatus status, Priority priority) {
        List<Task> tasks;

        if (status != null && priority != null) {
            tasks = repository.findByStatusAndPriority(status, priority);
        } else if (status != null) {
            tasks = repository.findByStatus(status);
        } else if (priority != null) {
            tasks = repository.findByPriority(priority);
        } else {
            tasks = repository.findAll();
        }

        return tasks.stream().map(TaskResponse::from).toList();
    }

    public TaskResponse findById(Long id) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }

    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setPriority(request.priority() != null ? request.priority() : Priority.MEDIUM);
        task.setStatus(TaskStatus.PENDING);

        Task saved = repository.save(task);
        return TaskResponse.from(saved);
    }

    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = repository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));

        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.status() != null) {
            task.setStatus(request.status());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }

        Task updated = repository.save(task);
        return TaskResponse.from(updated);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
