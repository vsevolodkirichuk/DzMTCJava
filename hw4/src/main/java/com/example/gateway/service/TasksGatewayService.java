package com.example.gateway.service;

import com.example.gateway.client.ExternalTasksClient;
import com.example.gateway.dto.TaskDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class TasksGatewayService {

    private static final Logger log = LoggerFactory.getLogger(TasksGatewayService.class);

    private final ExternalTasksClient client;

    public TasksGatewayService(ExternalTasksClient client) {
        this.client = client;
    }

    @RateLimiter(name = "externalApi", fallbackMethod = "createTaskFallback")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "createTaskFallback")
    public URI createTask(TaskDto task) {
        return client.createTask(task);
    }

    @RateLimiter(name = "externalApi", fallbackMethod = "getTaskFallback")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTaskFallback")
    public TaskDto getTask(Long id) {
        return client.getTask(id);
    }

    @RateLimiter(name = "externalApi", fallbackMethod = "getTasksFallback")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "getTasksFallback")
    public List<TaskDto> getTasks(Boolean completed, Integer limit) {
        return client.getTasks(completed, limit);
    }

    @RateLimiter(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    @CircuitBreaker(name = "externalApi", fallbackMethod = "deleteTaskFallback")
    public void deleteTask(Long id) {
        client.deleteTask(id);
    }

    public URI createTaskFallback(TaskDto task, Throwable t) {
        log.warn("Circuit breaker / rate limiter fallback for createTask: {}", t.getMessage());
        return URI.create("/fallback/tasks/0");
    }

    public TaskDto getTaskFallback(Long id, Throwable t) {
        log.warn("Circuit breaker / rate limiter fallback for getTask({}): {}", id, t.getMessage());
        TaskDto stub = new TaskDto();
        stub.setId(id);
        stub.setTitle("Service temporarily unavailable");
        stub.setCompleted(false);
        return stub;
    }

    public List<TaskDto> getTasksFallback(Boolean completed, Integer limit, Throwable t) {
        log.warn("Circuit breaker / rate limiter fallback for getTasks: {}", t.getMessage());
        return List.of();
    }

    public void deleteTaskFallback(Long id, Throwable t) {
        log.warn("Circuit breaker / rate limiter fallback for deleteTask({}): {}", id, t.getMessage());
        throw new RuntimeException("Service temporarily unavailable, please try again later");
    }
}
