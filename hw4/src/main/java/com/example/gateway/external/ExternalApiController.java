package com.example.gateway.external;

import com.example.gateway.dto.TaskDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/external/v1")
public class ExternalApiController {

    private final Map<Long, TaskDto> storage = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    @PostMapping("/tasks")
    public ResponseEntity<Void> createTask(@RequestBody TaskDto task) {
        task.setId(idSequence.getAndIncrement());
        if (task.getCompleted() == null) task.setCompleted(false);
        storage.put(task.getId(), task);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(task.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/tasks/{id}")
    public ResponseEntity<?> getTask(@PathVariable Long id) {
        TaskDto task = storage.get(id);
        if (task == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                    .body(Map.of(
                            "type", "https://example.com/problems/not-found",
                            "title", "Not Found",
                            "status", 404,
                            "detail", "Task with id " + id + " not found"
                    ));
        }
        return ResponseEntity.ok(task);
    }

    @GetMapping("/tasks")
    public ResponseEntity<List<TaskDto>> getTasks(
            @RequestParam(required = false) Boolean completed,
            @RequestParam(required = false) Integer limit) {
        List<TaskDto> tasks = new ArrayList<>(storage.values());

        if (completed != null) {
            tasks = tasks.stream()
                    .filter(t -> t.getCompleted().equals(completed))
                    .collect(Collectors.toList());
        }

        if (limit != null && limit < tasks.size()) {
            tasks = tasks.subList(0, limit);
        }

        return ResponseEntity.ok(tasks);
    }

    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!storage.containsKey(id)) {
            return ResponseEntity.notFound().build();
        }
        storage.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unstable")
    public ResponseEntity<?> unstable(@RequestParam String mode) throws InterruptedException {
        return switch (mode) {
            case "timeout" -> {
                Thread.sleep(10000);
                yield ResponseEntity.ok("ok");
            }
            case "500" -> ResponseEntity.status(500)
                    .body(Map.of("error", "Internal Server Error"));
            case "429" -> ResponseEntity.status(429)
                    .header("Retry-After", "5")
                    .body(Map.of("error", "Too Many Requests"));
            case "html" -> ResponseEntity.status(502)
                    .contentType(MediaType.TEXT_HTML)
                    .body("<html><body>Bad Gateway</body></html>");
            default -> ResponseEntity.badRequest()
                    .body(Map.of("error", "Unknown mode: " + mode));
        };
    }
}
