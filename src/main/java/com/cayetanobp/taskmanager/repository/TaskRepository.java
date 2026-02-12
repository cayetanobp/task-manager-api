package com.cayetanobp.taskmanager.repository;

import com.cayetanobp.taskmanager.model.Priority;
import com.cayetanobp.taskmanager.model.Task;
import com.cayetanobp.taskmanager.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByStatus(TaskStatus status);

    List<Task> findByPriority(Priority priority);

    List<Task> findByStatusAndPriority(TaskStatus status, Priority priority);
}
