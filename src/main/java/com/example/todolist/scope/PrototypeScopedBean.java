package com.example.todolist.scope;

import java.util.UUID;

/**
 * Prototype-scoped bean that generates a unique task identifier on each instantiation.
 * A new instance is created every time this bean is requested from the application context.
 */
public class PrototypeScopedBean {

  private final String generatedId;

  public PrototypeScopedBean() {
    this.generatedId = UUID.randomUUID().toString();
  }

  public String getGeneratedId() {
    return generatedId;
  }

  @Override
  public String toString() {
    return "PrototypeScopedBean{generatedId='" + generatedId + "'}";
  }
}
