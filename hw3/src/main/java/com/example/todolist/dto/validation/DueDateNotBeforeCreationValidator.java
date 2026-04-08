package com.example.todolist.dto.validation;

import com.example.todolist.dto.TaskUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DueDateNotBeforeCreationValidator implements ConstraintValidator<DueDateNotBeforeCreation, TaskUpdateDto> {

    @Override
    public boolean isValid(TaskUpdateDto dto, ConstraintValidatorContext context) {
        if (dto.getDueDate() == null || dto.getCreatedAt() == null) {
            return true;
        }
        return !dto.getDueDate().isBefore(dto.getCreatedAt().toLocalDate());
    }
}
