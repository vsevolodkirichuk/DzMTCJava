package com.example.gateway.client;

import com.example.gateway.dto.TaskDto;
import com.example.gateway.exception.ExternalApiException;
import com.example.gateway.exception.TaskNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.net.URI;
import java.util.List;

@Component
public class ExternalTasksClient {

    private static final Logger log = LoggerFactory.getLogger(ExternalTasksClient.class);

    private final RestClient restClient;

    public ExternalTasksClient(RestClient externalRestClient) {
        this.restClient = externalRestClient;
    }

    public URI createTask(TaskDto task) {
        ResponseEntity<Void> response = restClient.post()
                .uri("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(task)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), (req, resp) -> {
                    throw new ExternalApiException("External API error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value());
                })
                .toBodilessEntity();

        return response.getHeaders().getLocation();
    }

    public TaskDto getTask(Long id) {
        try {
            return restClient.get()
                    .uri("/tasks/{id}", id)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(status -> status.value() == 404, (req, resp) -> {
                        throw new TaskNotFoundException("Task not found with id: " + id);
                    })
                    .onStatus(status -> status.is5xxServerError(), (req, resp) -> {
                        throw new ExternalApiException("External API error: " + resp.getStatusCode().value(),
                                resp.getStatusCode().value());
                    })
                    .onStatus(status -> !status.is2xxSuccessful(), (req, resp) -> {
                        String contentType = resp.getHeaders().getContentType() != null
                                ? resp.getHeaders().getContentType().toString() : "unknown";
                        if (!contentType.contains("json")) {
                            log.warn("Unexpected content type from external API: {}", contentType);
                        }
                        throw new ExternalApiException("Unexpected response: " + resp.getStatusCode().value(),
                                resp.getStatusCode().value());
                    })
                    .body(TaskDto.class);
        } catch (RestClientResponseException e) {
            throw new ExternalApiException("External API call failed: " + e.getMessage(), e.getStatusCode().value());
        }
    }

    public List<TaskDto> getTasks(Boolean completed, Integer limit) {
        return restClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path("/tasks");
                    if (completed != null) uriBuilder.queryParam("completed", completed);
                    if (limit != null) uriBuilder.queryParam("limit", limit);
                    return uriBuilder.build();
                })
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(status -> status.is5xxServerError(), (req, resp) -> {
                    throw new ExternalApiException("External API error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value());
                })
                .body(new ParameterizedTypeReference<List<TaskDto>>() {});
    }

    public void deleteTask(Long id) {
        restClient.delete()
                .uri("/tasks/{id}", id)
                .retrieve()
                .onStatus(status -> status.value() == 404, (req, resp) -> {
                    throw new TaskNotFoundException("Task not found with id: " + id);
                })
                .onStatus(status -> status.is5xxServerError(), (req, resp) -> {
                    throw new ExternalApiException("External API error: " + resp.getStatusCode().value(),
                            resp.getStatusCode().value());
                })
                .toBodilessEntity();
    }
}
