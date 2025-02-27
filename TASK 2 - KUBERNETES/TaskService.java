package com.bsnanda.kaiburr.service;

import com.bsnanda.kaiburr.model.Task;
import com.bsnanda.kaiburr.model.TaskExecution;
import com.bsnanda.kaiburr.repository.TaskRepository;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

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
        String command = task.getCommand();

        if (!isValidCommand(command)) {
            throw new Exception("Invalid or potentially unsafe command detected.");
        }

        long startTime = System.currentTimeMillis();
        String output = "";

        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command("sh", "-c", command);
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


    public TaskExecution executeTaskInBusyBox(String taskId) throws Exception {
        Optional<Task> optionalTask = taskRepository.findById(taskId);
        if (optionalTask.isEmpty()) {
            throw new Exception("Task not found");
        }

        Task task = optionalTask.get();
        String command = task.getCommand();

        if (!isValidCommand(command)) {
            throw new Exception("Invalid or potentially unsafe command detected.");
        }

        String podName = "task-exec-" + UUID.randomUUID().toString().substring(0, 8);

        ApiClient client = Config.defaultClient();
        CoreV1Api api = new CoreV1Api(client);

        // Create a new pod with the BusyBox image
        V1Pod pod = new V1Pod()
                .apiVersion("v1")
                .kind("Pod")
                .metadata(new V1ObjectMeta().name(podName))
                .spec(new V1PodSpec()
                        .containers(Collections.singletonList(new V1Container()
                                .name("executor")
                                .image("busybox")
                                .command(Arrays.asList("sh", "-c", command))
                        ))
                        .restartPolicy("Never")
                );

        api.createNamespacedPod("default", pod, null, null, null, null);
        long startTime = System.currentTimeMillis();

        // Wait for pod execution
        while (true) {
            V1Pod podStatus = api.readNamespacedPod(podName, "default", null);
            if ("Succeeded".equals(podStatus.getStatus().getPhase()) || "Failed".equals(podStatus.getStatus().getPhase())) {
                break;
            }
            Thread.sleep(1000);
        }

        long endTime = System.currentTimeMillis();

        // Capture command output from pod logs
        String output = api.readNamespacedPodLog(
                podName,               // Pod name
                "default",             // Namespace
                "executor",            // Container name
                null,                  // Follow logs (Boolean)
                null,                  // Pretty output (String)
                null,                  // Previous log (Boolean)
                null,                  // Since seconds (Integer)
                null,                  // Since time (String)
                null,                  // Tail lines (Integer)
                null,                  // Timestamps (Boolean)
                null                   // Limit bytes (Integer)
        );

        // Save execution result
        TaskExecution execution = new TaskExecution(new Date(startTime), new Date(endTime), output);
        task.getTaskExecutions().add(execution);
        taskRepository.save(task);

        // Delete the pod after execution
        api.deleteNamespacedPod(podName, "default", null, null, null, null, null, null);

        return execution;
    }
    private boolean isValidCommand(String command) {
        // Blacklist of potentially dangerous commands
        List<String> blacklistedCommands = Arrays.asList(
                "rm", "shutdown", "reboot", "kill", "pkill", "mkfs", "dd", "wget", "curl",
                "chmod 777", "chown root", ">", ">>", "2>", "&&", ";", "|", "`", "$("
        );

        for (String unsafe : blacklistedCommands) {
            if (command.contains(unsafe)) {
                return false;
            }
        }

        List<String> allowedCommands = Arrays.asList("echo", "ls", "pwd", "date", "whoami");
        if (allowedCommands.stream().noneMatch(command::startsWith)) {
            return false;
        }

        return true;
    }
}

