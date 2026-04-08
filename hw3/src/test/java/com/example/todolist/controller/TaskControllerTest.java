package com.example.todolist.controller;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class TaskControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TaskCreateDto validDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Controller Task");
        dto.setPriority(Priority.HIGH);
        dto.setDueDate(LocalDate.now().plusDays(2));
        return dto;
    }

    private TaskResponseDto createTask() {
        return restTemplate.postForEntity("/api/tasks", validDto(), TaskResponseDto.class).getBody();
    }

    @Test
    void createTask_positive_returnsCreated() {
        ResponseEntity<TaskResponseDto> response = restTemplate.postForEntity("/api/tasks", validDto(), TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getId()).isNotNull();
    }

    @Test
    void createTask_negative_blankTitle_returnsBadRequest() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setPriority(Priority.LOW);
        ResponseEntity<String> response = restTemplate.postForEntity("/api/tasks", dto, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getAllTasks_positive_returnsOkWithXTotalCount() {
        createTask();
        ResponseEntity<TaskResponseDto[]> response = restTemplate.getForEntity("/api/tasks", TaskResponseDto[].class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().getFirst("X-Total-Count")).isNotNull();
    }

    @Test
    void getAllTasks_negative_wrongPath_returnsNotFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/wrongpath", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getTaskById_positive_returnsTask() {
        TaskResponseDto created = createTask();
        ResponseEntity<TaskResponseDto> response = restTemplate.getForEntity("/api/tasks/" + created.getId(), TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getTaskById_negative_notFound() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/tasks/99999", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateTask_positive_returnsUpdated() {
        TaskResponseDto created = createTask();
        TaskUpdateDto update = new TaskUpdateDto();
        update.setTitle("Updated Title");
        HttpEntity<TaskUpdateDto> request = new HttpEntity<>(update);
        ResponseEntity<TaskResponseDto> response = restTemplate.exchange(
                "/api/tasks/" + created.getId(), HttpMethod.PUT, request, TaskResponseDto.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getTitle()).isEqualTo("Updated Title");
    }

    @Test
    void updateTask_negative_notFound() {
        TaskUpdateDto update = new TaskUpdateDto();
        update.setTitle("Title");
        HttpEntity<TaskUpdateDto> request = new HttpEntity<>(update);
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/99999", HttpMethod.PUT, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteTask_positive_returnsNoContent() {
        TaskResponseDto created = createTask();
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/tasks/" + created.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void deleteTask_negative_notFound() {
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/99999", HttpMethod.DELETE, null, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void bulkComplete_negative_rollsBackOnMissingId() {
        TaskResponseDto created = createTask();
        HttpEntity<List<Long>> request = new HttpEntity<>(List.of(created.getId(), 999999L));
        ResponseEntity<String> response = restTemplate.exchange("/api/tasks/bulk-complete", HttpMethod.POST, request, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ResponseEntity<TaskResponseDto> check = restTemplate.getForEntity("/api/tasks/" + created.getId(), TaskResponseDto.class);
        assertThat(check.getBody().isCompleted()).isFalse();
    }
}
