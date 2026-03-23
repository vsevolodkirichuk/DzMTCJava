package com.example.todolist.mapper;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.model.Priority;
import com.example.todolist.model.Task;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    void toEntity_mapsCorrectly() {
        TaskCreateDto dto = new TaskCreateDto();
        dto.setTitle("Title");
        dto.setDescription("Desc");
        dto.setPriority(Priority.HIGH);
        dto.setDueDate(LocalDate.now().plusDays(3));
        dto.setTags(Set.of("tag1"));

        Task task = taskMapper.toEntity(dto);

        assertThat(task.getTitle()).isEqualTo("Title");
        assertThat(task.getPriority()).isEqualTo(Priority.HIGH);
        assertThat(task.getTags()).contains("tag1");
    }

    @Test
    void toResponseDto_mapsCorrectly() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Title");
        task.setPriority(Priority.LOW);

        TaskResponseDto dto = taskMapper.toResponseDto(task);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getTitle()).isEqualTo("Title");
        assertThat(dto.getPriority()).isEqualTo(Priority.LOW);
    }

    @Test
    void updateEntity_updatesOnlyProvidedFields() {
        Task task = new Task();
        task.setTitle("Old Title");
        task.setPriority(Priority.LOW);

        TaskUpdateDto dto = new TaskUpdateDto();
        dto.setTitle("New Title");

        taskMapper.updateEntity(dto, task);

        assertThat(task.getTitle()).isEqualTo("New Title");
        assertThat(task.getPriority()).isNull();
    }
}
