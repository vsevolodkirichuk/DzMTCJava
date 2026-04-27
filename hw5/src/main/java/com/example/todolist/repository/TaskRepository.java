package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompleted(boolean completed);

    List<Task> findByPriority(Priority priority);

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.completed = false")
    List<Task> findOverdueTasks(@Param("date") LocalDate date);

    @Query("SELECT t FROM Task t JOIN t.tags tag WHERE tag = :tag")
    List<Task> findByTag(@Param("tag") String tag);
}
