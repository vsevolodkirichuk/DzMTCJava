package com.example.todolist.service;

import com.example.todolist.dto.TaskCreateDto;
import com.example.todolist.dto.TaskResponseDto;
import com.example.todolist.dto.TaskUpdateDto;
import com.example.todolist.exception.BulkOperationException;
import com.example.todolist.exception.TaskNotFoundException;
import com.example.todolist.mapper.TaskMapper;
import com.example.todolist.model.Task;
import com.example.todolist.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public List<TaskResponseDto> getAll() {
        return taskRepository.findAll().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public TaskResponseDto getById(Long id) {
        return taskMapper.toResponseDto(findEntityById(id));
    }

    public List<TaskResponseDto> getDueSoon() {
        return taskRepository.findDueSoon(LocalDate.now(), LocalDate.now().plusDays(7)).stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public List<TaskResponseDto> getAllWithAttachments() {
        return taskRepository.findAllWithAttachments().stream()
                .map(taskMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponseDto create(TaskCreateDto dto) {
        Task task = taskMapper.toEntity(dto);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    public TaskResponseDto update(Long id, TaskUpdateDto dto) {
        Task task = findEntityById(id);
        dto.setCreatedAt(task.getCreatedAt());
        taskMapper.updateEntity(dto, task);
        return taskMapper.toResponseDto(taskRepository.save(task));
    }

    @Transactional
    public void delete(Long id) {
        findEntityById(id);
        taskRepository.deleteById(id);
    }

    @Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.READ_COMMITTED,
        rollbackFor = BulkOperationException.class
    )
    public void bulkCompleteTasks(List<Long> ids) {
        List<Task> tasks = ids.stream()
                .map(id -> taskRepository.findById(id)
                        .orElseThrow(() -> new BulkOperationException("Task not found with id: " + id)))
                .collect(Collectors.toList());

        tasks.forEach(task -> task.setCompleted(true));
        taskRepository.saveAll(tasks);
    }

    public long count() {
        return taskRepository.count();
    }

    public Task findEntityById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }
}
