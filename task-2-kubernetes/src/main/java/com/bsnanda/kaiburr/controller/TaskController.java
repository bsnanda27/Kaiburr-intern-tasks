package com.bsnanda.kaiburr.controller;

import com.bsnanda.kaiburr.model.Task;
import com.bsnanda.kaiburr.model.TaskExecution;
import com.bsnanda.kaiburr.repository.TaskRepository;
import com.bsnanda.kaiburr.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskRepository taskRepository;

    @Autowired
    private TaskService taskService;

    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAllTasks(@RequestParam(required = false) String id) {
        if (id != null) {
            return taskService.getTaskById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        }
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/{id}/executions")
    public ResponseEntity<List<TaskExecution>> getTaskExecutions(@PathVariable String id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            return ResponseEntity.ok(taskOptional.get().getTaskExecutions());
        }
        return ResponseEntity.notFound().build(); // 404 if task doesn't exist
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchTasks(@RequestParam String name) {
        List<Task> tasks = taskService.searchTasksByName(name);
        return tasks.isEmpty() ? ResponseEntity.notFound().build() : ResponseEntity.ok(tasks);
    }

    @PutMapping("")
    public ResponseEntity<Task> createOrUpdateTask(@RequestBody Task task) {
        return ResponseEntity.ok(taskService.createOrUpdateTask(task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable String id) {
        taskService.deleteTask(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable String id, @RequestBody Task updatedTask) {
        Optional<Task> taskOptional = taskRepository.findById(id);

        if (taskOptional.isPresent()) {
            Task existingTask = taskOptional.get();

            // Update only non-null fields
            if (updatedTask.getName() != null) {
                existingTask.setName(updatedTask.getName());
            }
            if (updatedTask.getOwner() != null) {
                existingTask.setOwner(updatedTask.getOwner());
            }
            if (updatedTask.getCommand() != null) {
                existingTask.setCommand(updatedTask.getCommand());
            }

            taskRepository.save(existingTask); // Save updated task
            return ResponseEntity.ok(existingTask);
        }

        return ResponseEntity.notFound().build(); // Return 404 if task not found
    }


    @PutMapping("/{id}/execute")
    public ResponseEntity<?> executeTask(@PathVariable String id) {
        try {
            TaskExecution execution = taskService.executeTask(id);
            return ResponseEntity.ok(execution);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @PutMapping("/{id}/executekub")
    public ResponseEntity<?> executeTaskInBusyBox(@PathVariable String id) {
        try {
            TaskExecution execution = taskService.executeTaskInBusyBox(id);
            return ResponseEntity.ok(execution);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error executing task: " + e.getMessage());
        }
    }

}
