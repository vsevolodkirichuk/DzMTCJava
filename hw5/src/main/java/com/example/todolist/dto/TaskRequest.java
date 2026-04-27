package com.example.todolist.dto;

import com.example.todolist.dto.validation.OnCreate;
import com.example.todolist.dto.validation.OnUpdate;
import com.example.todolist.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.Set;

public class TaskRequest {

    @NotBlank(groups = OnCreate.class)
    private String title;

    private String description;

    @NotNull(groups = {OnCreate.class, OnUpdate.class})
    private Priority priority;

    private LocalDate dueDate;

    private Set<String> tags;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Set<String> getTags() { return tags; }
    public void setTags(Set<String> tags) { this.tags = tags; }
}
