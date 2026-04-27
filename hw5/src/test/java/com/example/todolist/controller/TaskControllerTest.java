package com.example.todolist.controller;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.model.Priority;
import com.example.todolist.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    void createTask_validRequest_returns201WithBody() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Write tests");
        request.setPriority(Priority.HIGH);

        TaskResponse response = new TaskResponse();
        response.setId(1L);
        response.setTitle("Write tests");
        response.setPriority(Priority.HIGH);
        response.setCompleted(false);
        response.setCreatedAt(LocalDateTime.now());

        when(taskService.createTask(any())).thenReturn(response);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.title").value("Write tests"))
            .andExpect(jsonPath("$.priority").value("HIGH"))
            .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getTaskById_existingTask_returns200WithBody() throws Exception {
        TaskResponse response = new TaskResponse();
        response.setId(42L);
        response.setTitle("Fix bug");
        response.setPriority(Priority.MEDIUM);
        response.setCompleted(false);
        response.setCreatedAt(LocalDateTime.now());

        when(taskService.getTask(42L)).thenReturn(response);

        mockMvc.perform(get("/api/tasks/42"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(42))
            .andExpect(jsonPath("$.title").value("Fix bug"))
            .andExpect(jsonPath("$.priority").value("MEDIUM"));
    }
}
