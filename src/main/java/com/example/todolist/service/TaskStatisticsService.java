package com.example.todolist.service;

import com.example.todolist.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service that demonstrates the use of {@code @Primary} and {@code @Qualifier} to inject
 * two different {@link TaskRepository} implementations simultaneously.
 */
@Service
public class TaskStatisticsService {

  private static final Logger log = LoggerFactory.getLogger(TaskStatisticsService.class);

  private final TaskRepository primaryRepository;
  private final TaskRepository stubRepository;

  public TaskStatisticsService(
      TaskRepository primaryRepository,
      @Qualifier("stubTaskRepository") TaskRepository stubRepository) {
    this.primaryRepository = primaryRepository;
    this.stubRepository = stubRepository;
  }

  /**
   * Compares the number of tasks in the primary repository versus the stub repository
   * and logs the result.
   */
  public void compareRepositories() {
    int primaryCount = primaryRepository.findAll().size();
    int stubCount = stubRepository.findAll().size();
    log.info("[Statistics] Primary repository task count: {}", primaryCount);
    log.info("[Statistics] Stub repository task count: {}", stubCount);
    log.info("[Statistics] Primary repo class: {}", primaryRepository.getClass().getSimpleName());
    log.info("[Statistics] Stub repo class: {}", stubRepository.getClass().getSimpleName());
  }

  public int getPrimaryTaskCount() {
    return primaryRepository.findAll().size();
  }

  public int getStubTaskCount() {
    return stubRepository.findAll().size();
  }
}
