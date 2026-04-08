package com.example.todolist.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotBeforeCreation {
    String message() default "dueDate must not be before createdAt";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
