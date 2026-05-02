package com.example.kosapp.controller;

import com.example.kosapp.models.Complaint;
import com.example.kosapp.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@RequiredArgsConstructor
public class ComplaintController {

    private final ComplaintRepository repository;

    @GetMapping
    public List<Complaint> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Complaint> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-tenant/{tenantId}")
    public List<Complaint> getByTenant(@PathVariable Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @PostMapping
    public Complaint create(@RequestBody Complaint complaint) {
        return repository.save(complaint);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Complaint> update(@PathVariable Long id, @RequestBody Complaint updated) {
        return repository.findById(id).map(existing -> {
            existing.setTenant(updated.getTenant());
            existing.setDescription(updated.getDescription());
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
