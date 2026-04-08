package com.example.todolist.service;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.model.Priority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
class TaskServiceIntegrationTest {

    @Autowired
    private TaskService taskService;

    private TaskCreateDto validDto() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Integration Task");
        dto.setPriority(Priority.MEDIUM);
        dto.setDueDate(LocalDate.now().plusDays(5));
        return dto;
    }

    @Test
    void create_and_getById_works() {
        TaskResponseDto created = taskService.create(validDto());
        assertThat(created.getId()).isNotNull();
        assertThat(taskService.getById(created.getId()).getTitle()).isEqualTo("Integration Task");
    }

    @Test
    void bulkComplete_rollsBack_whenOneIdMissing() {
        TaskResponseDto t1 = taskService.create(validDto());
        TaskResponseDto t2 = taskService.create(validDto());

        assertThatThrownBy(() -> taskService.bulkCompleteTasks(List.of(t1.getId(), 999999L, t2.getId())))
                .isInstanceOf(BulkOperationException.class);

        assertThat(taskService.getById(t1.getId()).isCompleted()).isFalse();
        assertThat(taskService.getById(t2.getId()).isCompleted()).isFalse();
    }

    @Test
    void bulkComplete_completesAll_whenAllIdsExist() {
        TaskResponseDto t1 = taskService.create(validDto());
        TaskResponseDto t2 = taskService.create(validDto());

        taskService.bulkCompleteTasks(List.of(t1.getId(), t2.getId()));

        assertThat(taskService.getById(t1.getId()).isCompleted()).isTrue();
        assertThat(taskService.getById(t2.getId()).isCompleted()).isTrue();
    }

    @Test
    void getDueSoon_returnsTasksDueWithin7Days() {
        taskService.create(validDto());
        List<TaskResponseDto> dueSoon = taskService.getDueSoon();
        assertThat(dueSoon).isNotEmpty();
    }
}
