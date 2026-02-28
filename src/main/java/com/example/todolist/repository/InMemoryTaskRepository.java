package com.example.todolist.repository;

import com.example.todolist.model.Task;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Primary in-memory implementation of {@link TaskRepository}.
 * Stores tasks in a {@link ConcurrentHashMap} and uses an atomic counter for ID generation.
 */
@Repository
@Primary
public class InMemoryTaskRepository implements TaskRepository {

  private final Map<Long, Task> storage = new ConcurrentHashMap<>();
  private final AtomicLong idCounter = new AtomicLong(1);

  @Override
  public List<Task> findAll() {
    return new ArrayList<>(storage.values());
  }

  @Override
  public Optional<Task> findById(Long id) {
    return Optional.ofNullable(storage.get(id));
  }

  @Override
  public Task save(Task task) {
    long id = idCounter.getAndIncrement();
    task.setId(id);
    storage.put(id, task);
    return task;
  }

  @Override
  public Optional<Task> update(Long id, Task task) {
    if (!storage.containsKey(id)) {
      return Optional.empty();
    }
    task.setId(id);
    storage.put(id, task);
    return Optional.of(task);
  }

  @Override
  public boolean deleteById(Long id) {
    return storage.remove(id) != null;
  }
}
