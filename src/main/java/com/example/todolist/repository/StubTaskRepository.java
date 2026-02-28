package com.example.todolist.repository;

import com.example.todolist.model.Task;

import java.util.*;

/**
 * Stub implementation of {@link TaskRepository} with fixed predefined data.
 * Registered as a Spring bean via {@code @Bean} in the application configuration.
 */
public class StubTaskRepository implements TaskRepository {

  private final List<Task> stubTasks = List.of(
      new Task(1L, "Stub Task 1", "First stub task", false),
      new Task(2L, "Stub Task 2", "Second stub task", true),
      new Task(3L, "Stub Task 3", "Third stub task", false)
  );

  @Override
  public List<Task> findAll() {
    return stubTasks;
  }

  @Override
  public Optional<Task> findById(Long id) {
    return stubTasks.stream()
        .filter(t -> t.getId().equals(id))
        .findFirst();
  }

  @Override
  public Task save(Task task) {
    return task;
  }

  @Override
  public Optional<Task> update(Long id, Task task) {
    return Optional.of(task);
  }

  @Override
  public boolean deleteById(Long id) {
    return true;
  }
}
