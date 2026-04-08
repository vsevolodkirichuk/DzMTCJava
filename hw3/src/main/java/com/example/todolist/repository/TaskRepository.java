package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByCompletedAndPriority(boolean completed, Priority priority);

    @Query("SELECT t FROM Task t WHERE t.dueDate BETWEEN :today AND :in7days")
    List<Task> findDueSoon(LocalDate today, LocalDate in7days);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("SELECT t FROM Task t")
    List<Task> findAllWithAttachments();
}
