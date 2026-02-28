package com.example.todolist.scope;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Request-scoped bean that captures metadata for each incoming HTTP request.
 * A new instance is created for every HTTP request.
 */
public class RequestScopedBean {

  private final String requestId;
  private final LocalDateTime requestStartTime;

  public RequestScopedBean() {
    this.requestId = UUID.randomUUID().toString();
    this.requestStartTime = LocalDateTime.now();
  }

  public String getRequestId() {
    return requestId;
  }

  public LocalDateTime getRequestStartTime() {
    return requestStartTime;
  }

  @Override
  public String toString() {
    return "RequestScopedBean{requestId='" + requestId + "', requestStartTime=" + requestStartTime + "}";
  }
}
