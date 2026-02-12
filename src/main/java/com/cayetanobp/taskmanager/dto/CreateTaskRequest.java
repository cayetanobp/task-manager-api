package com.cayetanobp.taskmanager.dto;

import com.cayetanobp.taskmanager.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTaskRequest(

        @NotBlank(message = "El titulo es obligatorio")
        @Size(max = 120, message = "El titulo no puede superar 120 caracteres")
        String title,

        @Size(max = 500, message = "La descripcion no puede superar 500 caracteres")
        String description,

        Priority priority
) {
}
