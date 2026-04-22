package com.example.gateway.api;

import com.example.gateway.dto.TaskDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExternalApiControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private TaskDto createTask(String title) {
        TaskDto task = new TaskDto();
        task.setTitle(title);
        task.setCompleted(false);
        ResponseEntity<Void> resp = restTemplate.postForEntity("/external/v1/tasks", task, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getHeaders().getLocation()).isNotNull();

        String location = resp.getHeaders().getLocation().toString();
        String id = location.substring(location.lastIndexOf('/') + 1);
        return restTemplate.getForObject("/external/v1/tasks/" + id, TaskDto.class);
    }

    @Test
    void createTask_positive_returns201WithLocation() {
        TaskDto task = new TaskDto();
        task.setTitle("Test Task");
        ResponseEntity<Void> resp = restTemplate.postForEntity("/external/v1/tasks", task, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(resp.getHeaders().getLocation()).isNotNull();
    }

    @Test
    void getTask_positive_returnsTask() {
        TaskDto created = createTask("Get Test");
        ResponseEntity<TaskDto> resp = restTemplate.getForEntity(
                "/external/v1/tasks/" + created.getId(), TaskDto.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody().getTitle()).isEqualTo("Get Test");
    }

    @Test
    void getTask_negative_returns404WithProblemDetails() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/external/v1/tasks/99999", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody()).contains("not found");
    }

    @Test
    void deleteTask_positive_returns204() {
        TaskDto created = createTask("Delete Test");
        ResponseEntity<Void> resp = restTemplate.exchange(
                "/external/v1/tasks/" + created.getId(), HttpMethod.DELETE, null, Void.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void getTasks_withQueryParams_returnsFilteredList() {
        createTask("Completed Task");
        ResponseEntity<TaskDto[]> resp = restTemplate.getForEntity(
                "/external/v1/tasks?completed=false&limit=5", TaskDto[].class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void unstable_500_returnsServerError() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/external/v1/unstable?mode=500", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void unstable_429_returnsTooManyRequests() {
        ResponseEntity<String> resp = restTemplate.getForEntity("/external/v1/unstable?mode=429", String.class);
        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
        assertThat(resp.getHeaders().getFirst("Retry-After")).isEqualTo("5");
    }
}
