package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.scope.RequestScopedBean;
import com.example.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller exposing CRUD endpoints for {@link Task} management.
 * All endpoints are available under the {@code /api/tasks} base path.
 */
@RestController
@RequestMapping("/api/tasks")
public class TaskController {

  private static final Logger log = LoggerFactory.getLogger(TaskController.class);

  private final TaskService taskService;
  private final RequestScopedBean requestScopedBean;

  public TaskController(TaskService taskService, RequestScopedBean requestScopedBean) {
    this.taskService = taskService;
    this.requestScopedBean = requestScopedBean;
  }

  /**
   * Returns all tasks.
   *
   * @return HTTP 200 with a list of all tasks
   */
  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    log.info("Request [{}] GET /api/tasks", requestScopedBean.getRequestId());
    List<Task> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  /**
   * Returns a single task by ID.
   *
   * @param id the task identifier
   * @return HTTP 200 with the task, or HTTP 404 if not found
   */
  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    log.info("Request [{}] GET /api/tasks/{}", requestScopedBean.getRequestId(), id);
    Optional<Task> task = taskService.getTaskById(id);
    return task.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Creates a new task.
   *
   * @param task the task payload from the request body
   * @return HTTP 201 with the created task, or HTTP 400 if the title is missing
   */
  @PostMapping
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    log.info("Request [{}] POST /api/tasks", requestScopedBean.getRequestId());
    if (task.getTitle() == null || task.getTitle().isBlank()) {
      return ResponseEntity.badRequest().build();
    }
    Task created = taskService.createTask(task);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  /**
   * Updates an existing task.
   *
   * @param id   the task identifier
   * @param task the updated task payload
   * @return HTTP 200 with the updated task, or HTTP 404 if not found
   */
  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task task) {
    log.info("Request [{}] PUT /api/tasks/{}", requestScopedBean.getRequestId(), id);
    Optional<Task> updated = taskService.updateTask(id, task);
    return updated.map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  /**
   * Deletes a task by ID.
   *
   * @param id the task identifier
   * @return HTTP 204 if deleted, or HTTP 404 if not found
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    log.info("Request [{}] DELETE /api/tasks/{}", requestScopedBean.getRequestId(), id);
    boolean deleted = taskService.deleteTask(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}
