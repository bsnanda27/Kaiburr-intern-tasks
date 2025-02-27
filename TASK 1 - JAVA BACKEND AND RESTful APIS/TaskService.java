package com.bsnanda.kaiburr.service;

import com.bsnanda.kaiburr.model.Task;
import com.bsnanda.kaiburr.model.TaskExecution;
import com.bsnanda.kaiburr.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {
    @Autowired
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(String id) {
        return taskRepository.findById(id);
    }

    public List<Task> searchTasksByName(String name) {
        return taskRepository.findByNameContaining(name);
    }

    public Task createOrUpdateTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(String id) {
        taskRepository.deleteById(id);
    }

    public TaskExecution executeTask(String taskId) throws Exception {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new Exception("Task not found");
        }

        Task task = optionalTask.get();
        long startTime = System.currentTimeMillis();
        String output = "";

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("sh", "-c", task.getCommand());
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder outputBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                outputBuilder.append(line).append("\n");
            }

            process.waitFor();
            output = outputBuilder.toString().trim();
        } catch (Exception e) {
            output = "Error executing command: " + e.getMessage();
        }

        long endTime = System.currentTimeMillis();
        TaskExecution execution = new TaskExecution(new Date(startTime), new Date(endTime), output);

        // Ensure taskExecutions is initialized
        if (task.getTaskExecutions() == null) {
            task.setTaskExecutions(new ArrayList<>());
        }

        task.getTaskExecutions().add(execution);
        taskRepository.save(task);
        return execution;
    }

}
