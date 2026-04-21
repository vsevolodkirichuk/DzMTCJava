package com.example.gateway.api;

import com.example.gateway.dto.TaskDto;
import com.example.gateway.service.TasksGatewayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TasksGatewayController {

    private final TasksGatewayService gatewayService;

    public TasksGatewayController(TasksGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }

    @PostMapping
    public ResponseEntity<Void> createTask(@RequestBody TaskDto task) {
        URI location = gatewayService.createTask(task);
        if (location != null) {
            return ResponseEntity.created(location).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDto> getTask(@PathVariable Long id) {
        return ResponseEntity.ok(gatewayService.getTask(id));
    }

    @GetMapping
    public ResponseEntity<List<TaskDto>> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        return ResponseEntity.ok(gatewayService.getTasks(completed, limit));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        gatewayService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }
}
