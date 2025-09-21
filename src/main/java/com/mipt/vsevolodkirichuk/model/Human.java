package com.mipt.ivanivanov.model;

public class Human {
    private String firstName;
    private String lastName;
    private int age;
    private boolean isEmployed;

    // Геттер для firstName
    public String getFirstName() {
        return firstName;
    }

    // Сеттер для firstName
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Геттер для lastName
    public String getLastName() {
        return lastName;
    }

    // Сеттер для lastName
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Геттер для age
    public int getAge() {
        return age;
    }

    // Сеттер для age
    public void setAge(int age) {
        this.age = age;
    }

    // Геттер для isEmployed (для boolean полей часто используют is вместо get)
    public boolean isEmployed() {
        return isEmployed;
    }

    // Сеттер для isEmployed
    public void setEmployed(boolean employed) {
        isEmployed = employed;
    }
}
