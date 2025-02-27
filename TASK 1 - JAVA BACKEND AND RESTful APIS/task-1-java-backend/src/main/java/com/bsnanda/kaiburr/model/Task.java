package com.bsnanda.kaiburr.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@Document(collection = "tasks")
public class Task {
    @Id
    private String id;
    private String name;
    private String owner;
    private String command;
    private List<TaskExecution> taskExecutions = new ArrayList<>(); // Ensure it's always initialized

    public Task() {
        this.taskExecutions = new ArrayList<>(); // Initialize in the constructor
    }

    // Getters and Setters
    public List<TaskExecution> getTaskExecutions() {
        if (taskExecutions == null) {
            taskExecutions = new ArrayList<>(); // Fallback initialization
        }
        return taskExecutions;
    }

    public void setTaskExecutions(List<TaskExecution> taskExecutions) {
        this.taskExecutions = taskExecutions;
    }
}

