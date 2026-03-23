package com.example.todolist.dto;

import com.example.todolist.dto.validation.OnCreate;
import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;

@Schema(description = "DTO for creating a new task")
public class TaskCreateDto {

    @NotBlank(groups = OnCreate.class)
    @Size(min = 3, max = 100, groups = OnCreate.class)
    @Schema(description = "Task title", example = "Buy groceries")
    private String title;

    @Size(max = 500)
    @Schema(description = "Task description")
    private String description;

    @FutureOrPresent
    @Schema(description = "Due date")
    private LocalDate dueDate;

    @NotNull(groups = OnCreate.class)
    @Schema(description = "Task priority")
    private Priority priority;

    @Size(max = 5)
    @Schema(description = "Tags")
    private Set<String> tags;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
