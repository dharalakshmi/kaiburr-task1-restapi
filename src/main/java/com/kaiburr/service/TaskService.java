package com.kaiburr.service;

import com.kaiburr.dto.ExecuteRequest;
import com.kaiburr.exception.NotFoundException;
import com.kaiburr.model.Task;
import com.kaiburr.model.TaskExecution;
import com.kaiburr.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
public class TaskService {
    private final TaskRepository repo;

    public TaskService(TaskRepository repo) { this.repo = repo; }

    public List<Task> getAll() { return repo.findAll(); }

    public Task getById(String id) {
        return repo.findById(id).orElseThrow(() -> new NotFoundException("Task not found: " + id));
    }

    public Task save(Task task) {
        if (!StringUtils.hasText(task.getCommand())) {
            throw new IllegalArgumentException("Command must not be empty");
        }
        validateCommand(task.getCommand());
        return repo.save(task);
    }

    public void delete(String id) {
        if (!repo.existsById(id)) throw new NotFoundException("Task not found: " + id);
        repo.deleteById(id);
    }

    public List<Task> findByName(String name) {
        List<Task> found = repo.findByNameContainingIgnoreCase(name);
        if (found.isEmpty()) throw new NotFoundException("No tasks found containing: " + name);
        return found;
    }

    public Task execute(String taskId, ExecuteRequest executeRequest) throws Exception {
        Task t = getById(taskId);
        String cmd = (executeRequest != null && StringUtils.hasText(executeRequest.getCommand()))
                ? executeRequest.getCommand()
                : t.getCommand();

        validateCommand(cmd);

        Instant start = Instant.now();
        String output = runLocalCommand(cmd);
        Instant end = Instant.now();

        TaskExecution exec = new TaskExecution(start, end, output);
        t.getTaskExecutions().add(exec);
        return repo.save(t);
    }

    private String runLocalCommand(String command) throws Exception {
        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
        pb.redirectErrorStream(true);
        Process p = pb.start();

        StringBuilder out = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) out.append(line).append("\n");
        }
        int exit = p.waitFor();
        out.append("\nExitCode: ").append(exit);
        return out.toString();
    }

    private void validateCommand(String command) {
        if (!StringUtils.hasText(command)) throw new IllegalArgumentException("Command is empty");

        String cmd = command.trim();
        List<String> blacklist = Arrays.asList("rm ", "rm -", "sudo", "reboot", "shutdown", "mkfs", "dd ", ">:",
                "curl -s", "curl -fsSL", "wget ", "nc ", "netcat ", "chmod 777", ">:","ssh ", "scp ", "format");
        String lower = cmd.toLowerCase();
        for (String b : blacklist) {
            if (lower.contains(b)) {
                throw new IllegalArgumentException("Command contains forbidden operations: " + b);
            }
        }

        List<String> allowed = Arrays.asList("echo", "date", "uname", "hostname", "dir", "ls", "pwd", "whoami", "sleep", "printf", "type", "cat");
        boolean ok = false;
        for (String a : allowed) {
            if (lower.startsWith(a + " ") || lower.equals(a) || lower.startsWith(a + "-")) {
                ok = true;
                break;
            }
        }
        if (!ok) {
            throw new IllegalArgumentException("Command not allowed. Allowed commands: " + String.join(", ", allowed));
        }
    }
}
