package com.example.todolist.controller;

import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link TaskController} using {@link TestRestTemplate}.
 * Each endpoint is covered by at least one positive and one negative test scenario.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerTest {

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TaskRepository taskRepository;

  private Task savedTask;

  @BeforeEach
  void setUp() {
    Task task = new Task();
    task.setTitle("Test Task");
    task.setDescription("Test Description");
    task.setCompleted(false);
    savedTask = taskRepository.save(task);
  }

  // --- GET /api/tasks ---

  @Test
  void getAllTasks_positive_returnsOkWithList() {
    ResponseEntity<Task[]> response = restTemplate.getForEntity("/api/tasks", Task[].class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().length).isGreaterThanOrEqualTo(1);
  }

  @Test
  void getAllTasks_negative_returnsNotFoundForWrongPath() {
    ResponseEntity<String> response = restTemplate.getForEntity("/api/wrongpath", String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // --- GET /api/tasks/{id} ---

  @Test
  void getTaskById_positive_returnsTask() {
    ResponseEntity<Task> response = restTemplate.getForEntity(
        "/api/tasks/" + savedTask.getId(), Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(savedTask.getId());
  }

  @Test
  void getTaskById_negative_returnsNotFound() {
    ResponseEntity<Task> response = restTemplate.getForEntity("/api/tasks/99999", Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // --- POST /api/tasks ---

  @Test
  void createTask_positive_returnsCreatedTask() {
    Task newTask = new Task();
    newTask.setTitle("New Task");
    newTask.setDescription("New Description");
    newTask.setCompleted(false);

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", newTask, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("New Task");
  }

  @Test
  void createTask_negative_returnsBadRequestWhenTitleIsBlank() {
    Task invalidTask = new Task();
    invalidTask.setTitle("");
    invalidTask.setDescription("No title");

    ResponseEntity<Task> response = restTemplate.postForEntity("/api/tasks", invalidTask, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
  }

  // --- PUT /api/tasks/{id} ---

  @Test
  void updateTask_positive_returnsUpdatedTask() {
    Task update = new Task();
    update.setTitle("Updated Title");
    update.setDescription("Updated Description");
    update.setCompleted(true);

    HttpEntity<Task> request = new HttpEntity<>(update);
    ResponseEntity<Task> response = restTemplate.exchange(
        "/api/tasks/" + savedTask.getId(), HttpMethod.PUT, request, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
    assertThat(response.getBody().isCompleted()).isTrue();
  }

  @Test
  void updateTask_negative_returnsNotFoundForMissingId() {
    Task update = new Task();
    update.setTitle("Ghost Task");

    HttpEntity<Task> request = new HttpEntity<>(update);
    ResponseEntity<Task> response = restTemplate.exchange(
        "/api/tasks/99999", HttpMethod.PUT, request, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }

  // --- DELETE /api/tasks/{id} ---

  @Test
  void deleteTask_positive_returnsNoContent() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tasks/" + savedTask.getId(), HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
  }

  @Test
  void deleteTask_negative_returnsNotFoundForMissingId() {
    ResponseEntity<Void> response = restTemplate.exchange(
        "/api/tasks/99999", HttpMethod.DELETE, null, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
  }
}