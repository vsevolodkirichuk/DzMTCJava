package com.example.todolist.processor;

import com.example.todolist.repository.TaskRepository;
import com.example.todolist.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * {@link BeanPostProcessor} that logs the lifecycle events of {@link TaskService}
 * and {@link TaskRepository} beans during Spring context initialization.
 */
@Component
public class TaskLifecycleProcessor implements BeanPostProcessor {

  private static final Logger log = LoggerFactory.getLogger(TaskLifecycleProcessor.class);

  @Override
  public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      log.info("[BeanPostProcessor] Before initialization: bean='{}', type='{}'",
          beanName, bean.getClass().getSimpleName());
    }
    return bean;
  }

  @Override
  public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
    if (bean instanceof TaskService || bean instanceof TaskRepository) {
      log.info("[BeanPostProcessor] After initialization: bean='{}', type='{}'",
          beanName, bean.getClass().getSimpleName());
    }
    return bean;
  }
}
