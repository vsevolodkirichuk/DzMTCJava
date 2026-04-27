package com.example.todolist.service;

import com.example.todolist.dto.TaskResponse;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskServiceTest {

    @Autowired
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void updateStatus_existingTask_savesWithUpdatedCompletedFlag() {
        Task existing = new Task();
        existing.setId(1L);
        existing.setTitle("Buy milk");
        existing.setPriority(Priority.LOW);
        existing.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(taskRepository.save(any(Task.class))).thenAnswer(inv -> inv.getArgument(0));

        TaskResponse response = taskService.updateStatus(1L, true);

        ArgumentCaptor<Task> captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());
        assertThat(captor.getValue().isCompleted()).isTrue();
        assertThat(response.isCompleted()).isTrue();
    }
}
