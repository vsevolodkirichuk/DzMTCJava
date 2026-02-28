package com.example.todolist.repository;

import com.example.todolist.model.Task;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface defining CRUD operations for {@link Task} entities.
 */
public interface TaskRepository {

  List<Task> findAll();

  Optional<Task> findById(Long id);

  Task save(Task task);

  Optional<Task> update(Long id, Task task);

  boolean deleteById(Long id);
}
