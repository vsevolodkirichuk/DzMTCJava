package com.example.todolist.config;

import com.example.todolist.repository.StubTaskRepository;
import com.example.todolist.repository.TaskRepository;
import com.example.todolist.scope.PrototypeScopedBean;
import com.example.todolist.scope.RequestScopedBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.web.context.WebApplicationContext;

/**
 * Main application configuration class.
 * Registers {@link StubTaskRepository}, {@link RequestScopedBean}, and {@link PrototypeScopedBean} as Spring beans.
 */
@Configuration
public class AppConfig {

  /**
   * Registers the stub repository implementation as a Spring bean.
   *
   * @return a new {@link StubTaskRepository} instance
   */
  @Bean("stubTaskRepository")
  public TaskRepository stubTaskRepository() {
    return new StubTaskRepository();
  }

  /**
   * Registers a request-scoped bean that tracks per-request metadata.
   *
   * @return a new {@link RequestScopedBean} for each HTTP request
   */
  @Bean
  @Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
  public RequestScopedBean requestScopedBean() {
    return new RequestScopedBean();
  }

  /**
   * Registers a prototype-scoped bean that generates unique task IDs.
   *
   * @return a new {@link PrototypeScopedBean} on every injection
   */
  @Bean
  @Scope(BeanDefinition.SCOPE_PROTOTYPE)
  public PrototypeScopedBean prototypeScopedBean() {
    return new PrototypeScopedBean();
  }
}
