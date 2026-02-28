package com.example.todolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Entry point for the To-Do List Spring Boot application.
 *
 * <p>{@code @EnableAspectJAutoProxy} is declared explicitly to demonstrate understanding
 * of the AspectJ auto-proxy mechanism. Spring Boot enables it automatically via
 * {@code AopAutoConfiguration}, but the explicit declaration makes the intent visible.</p>
 */
@SpringBootApplication
@EnableAspectJAutoProxy
public class TodoListApplication {

  public static void main(String[] args) {
    SpringApplication.run(TodoListApplication.class, args);
  }
}
