package com.kaiburr.controller;

import com.kaiburr.dto.ExecuteRequest;
import com.kaiburr.model.Task;
import com.kaiburr.service.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService svc;

    public TaskController(TaskService svc) { this.svc = svc; }

    @GetMapping
    public ResponseEntity<List<Task>> getAll(@RequestParam(value = "id", required = false) String id) {
        if (id != null && !id.isEmpty()) {
            Task t = svc.getById(id);
            return ResponseEntity.ok(List.of(t));
        }
        return ResponseEntity.ok(svc.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Task> getById(@PathVariable String id) {
        return ResponseEntity.ok(svc.getById(id));
    }

    @PutMapping
    public ResponseEntity<Task> createOrUpdate(@RequestBody Task task) {
        Task saved = svc.save(task);
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        svc.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<Task>> searchByName(@RequestParam("name") String name) {
        return ResponseEntity.ok(svc.findByName(name));
    }

    @PutMapping("/{id}/execute")
    public ResponseEntity<Task> execute(@PathVariable String id, @RequestBody(required = false) ExecuteRequest body) throws Exception {
        Task t = svc.execute(id, body);
        return ResponseEntity.ok(t);
    }
}
