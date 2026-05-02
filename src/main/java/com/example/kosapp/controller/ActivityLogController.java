package com.example.kosapp.controller;

import com.example.kosapp.models.ActivityLog;
import com.example.kosapp.repository.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository repository;

    @GetMapping
    public List<ActivityLog> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityLog> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ActivityLog create(@RequestBody ActivityLog log) {
        return repository.save(log);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityLog> update(@PathVariable Long id, @RequestBody ActivityLog updated) {
        return repository.findById(id).map(existing -> {
            existing.setAction(updated.getAction());
            existing.setCreatedAt(updated.getCreatedAt());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
