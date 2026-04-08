package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.dto.validation.OnCreate;
import com.example.todolist.dto.validation.OnUpdate;
import com.example.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Task management API")
public class TaskController {
    private final TaskService taskService;

    @Value("${app.api-version}")
    private String apiVersion;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Get all tasks")
    @ApiResponse(responseCode = "200", description = "List of tasks")
    public ResponseEntity<List<TaskResponseDto>> getAll() {
        List<TaskResponseDto> tasks = taskService.getAll();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Total-Count", String.valueOf(tasks.size()));
        headers.add("X-API-Version", apiVersion);
        return ResponseEntity.ok().headers(headers).body(tasks);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskService.getById(id));
    }

    @PostMapping
    @Operation(summary = "Create a task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Invalid input")
    public ResponseEntity<TaskResponseDto> create(@Validated(OnCreate.class) @RequestBody TaskCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .header("X-API-Version", apiVersion)
                .body(taskService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a task")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<TaskResponseDto> update(@PathVariable Long id,
                                                   @Validated(OnUpdate.class) @RequestBody TaskUpdateDto dto) {
        return ResponseEntity.ok()
                .header("X-API-Version", apiVersion)
                .body(taskService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a task")
    @ApiResponse(responseCode = "204", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent()
                .header("X-API-Version", apiVersion)
                .build();
    }
}
