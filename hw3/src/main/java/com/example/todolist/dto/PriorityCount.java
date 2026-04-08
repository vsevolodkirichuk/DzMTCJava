package com.example.todolist.dto;

public class PriorityCount {
    private String priority;
    private long count;

    public PriorityCount(String priority, long count) {
        this.priority = priority;
        this.count = count;
    }

    public String getPriority() { return priority; }
    public long getCount() { return count; }
}
