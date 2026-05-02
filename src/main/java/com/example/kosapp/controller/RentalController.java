package com.example.kosapp.controller;

import com.example.kosapp.models.Rental;
import com.example.kosapp.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rentals")
@RequiredArgsConstructor
public class RentalController {

    private final RentalRepository repository;

    @GetMapping
    public List<Rental> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Rental> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-tenant/{tenantId}")
    public List<Rental> getByTenant(@PathVariable Long tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @GetMapping("/by-room/{roomId}")
    public List<Rental> getByRoom(@PathVariable Long roomId) {
        return repository.findByRoomId(roomId);
    }

    @PostMapping
    public Rental create(@RequestBody Rental rental) {
        return repository.save(rental);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Rental> update(@PathVariable Long id, @RequestBody Rental updated) {
        return repository.findById(id).map(existing -> {
            existing.setTenant(updated.getTenant());
            existing.setRoom(updated.getRoom());
            existing.setStartDate(updated.getStartDate());
            existing.setEndDate(updated.getEndDate());
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
