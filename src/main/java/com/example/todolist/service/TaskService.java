package com.example.todolist.service;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer for managing {@link Task} entities.
 * Delegates persistence to a {@link TaskRepository} and maintains an in-memory task cache.
 */
@Service
public class TaskService {

  private static final Logger log = LoggerFactory.getLogger(TaskService.class);

  private final TaskRepository taskRepository;

  @Value("${app.name:todo-list}")
  private String appName;

  @Value("${app.version:0.0.1}")
  private String appVersion;

  private Map<String, Task> taskCache;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @PostConstruct
  public void initCache() {
    log.info("[PostConstruct] Initializing task cache for app '{}' v{}", appName, appVersion);
    taskCache = new HashMap<>();
    List<Task> tasks = taskRepository.findAll();
    for (Task task : tasks) {
      taskCache.put(String.valueOf(task.getId()), task);
    }
    log.info("[PostConstruct] Task cache initialized with {} entries", taskCache.size());
  }

  @PreDestroy
  public void cleanUp() {
    log.info("[PreDestroy] Cleaning up TaskService. Cache contains {} task(s)", taskCache.size());
    try (FileWriter writer = new FileWriter("task-statistics.txt", true)) {
      writer.write("Application shutdown. Total cached tasks: " + taskCache.size() + System.lineSeparator());
    } catch (IOException e) {
      log.warn("[PreDestroy] Could not write statistics file: {}", e.getMessage());
    }
    taskCache.clear();
    log.info("[PreDestroy] Task cache cleared");
  }

  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  public Task createTask(Task task) {
    Task saved = taskRepository.save(task);
    taskCache.put(String.valueOf(saved.getId()), saved);
    return saved;
  }

  public Optional<Task> updateTask(Long id, Task task) {
    Optional<Task> updated = taskRepository.update(id, task);
    updated.ifPresent(t -> taskCache.put(String.valueOf(t.getId()), t));
    return updated;
  }

  public boolean deleteTask(Long id) {
    boolean deleted = taskRepository.deleteById(id);
    if (deleted) {
      taskCache.remove(String.valueOf(id));
    }
    return deleted;
  }
}
