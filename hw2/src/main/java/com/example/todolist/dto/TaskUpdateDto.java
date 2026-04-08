package com.example.todolist.dto;

import com.example.todolist.dto.validation.DueDateNotBeforeCreation;
import com.example.todolist.dto.validation.OnUpdate;
import com.example.todolist.model.Priority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@DueDateNotBeforeCreation(groups = OnUpdate.class)
@Schema(description = "DTO for updating a task")
public class TaskUpdateDto {

    @Size(min = 3, groups = OnUpdate.class)
    @Schema(description = "Task title")
    private String title;

    @Size(max = 500)
    @Schema(description = "Task description")
    private String description;

    @Schema(description = "Completion status")
    private Boolean completed;

    @Schema(description = "Due date")
    private LocalDate dueDate;

    @Schema(description = "Task priority")
    private Priority priority;

    @Size(max = 5)
    @Schema(description = "Tags")
    private Set<String> tags;

    @Schema(hidden = true)
    private LocalDateTime createdAt;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getCompleted() { return completed; }
    public void setCompleted(Boolean completed) { this.completed = completed; }
    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
