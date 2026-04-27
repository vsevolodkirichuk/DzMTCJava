package com.example.todolist.mapper;

import com.example.todolist.dto.TaskRequest;
import com.example.todolist.dto.TaskResponse;
import com.example.todolist.model.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    TaskResponse toResponse(Task task);

    Task toEntity(TaskRequest request);

    void updateEntity(TaskRequest request, @MappingTarget Task task);
}
