package com.example.todolist.repository;

import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TaskRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.flyway.locations", () -> "classpath:db/migration");
    }

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void findOverdueTasks_returnsOnlyIncompleteTasksBeforeDate() {
        Task overdueTask = new Task();
        overdueTask.setTitle("Overdue task");
        overdueTask.setPriority(Priority.HIGH);
        overdueTask.setCompleted(false);
        overdueTask.setDueDate(LocalDate.now().minusDays(3));
        taskRepository.save(overdueTask);

        Task completedOld = new Task();
        completedOld.setTitle("Completed old task");
        completedOld.setPriority(Priority.LOW);
        completedOld.setCompleted(true);
        completedOld.setDueDate(LocalDate.now().minusDays(5));
        taskRepository.save(completedOld);

        Task futureTask = new Task();
        futureTask.setTitle("Future task");
        futureTask.setPriority(Priority.MEDIUM);
        futureTask.setCompleted(false);
        futureTask.setDueDate(LocalDate.now().plusDays(7));
        taskRepository.save(futureTask);

        List<Task> overdue = taskRepository.findOverdueTasks(LocalDate.now());

        assertThat(overdue).hasSize(1);
        assertThat(overdue.get(0).getTitle()).isEqualTo("Overdue task");
    }
}
