package com.example.kosapp.controller;

import com.example.kosapp.models.MaintenanceRequest;
import com.example.kosapp.repository.MaintenanceRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/maintenance-requests")
@RequiredArgsConstructor
public class MaintenanceRequestController {

    private final MaintenanceRequestRepository repository;

    @GetMapping
    public List<MaintenanceRequest> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceRequest> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-room/{roomId}")
    public List<MaintenanceRequest> getByRoom(@PathVariable Long roomId) {
        return repository.findByRoomId(roomId);
    }

    @GetMapping("/by-status/{status}")
    public List<MaintenanceRequest> getByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }

    @PostMapping
    public MaintenanceRequest create(@RequestBody MaintenanceRequest request) {
        return repository.save(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MaintenanceRequest> update(@PathVariable Long id, @RequestBody MaintenanceRequest updated) {
        return repository.findById(id).map(existing -> {
            existing.setRoom(updated.getRoom());
            existing.setDescription(updated.getDescription());
            existing.setStatus(updated.getStatus());
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
