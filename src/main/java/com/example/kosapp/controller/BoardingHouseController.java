package com.example.kosapp.controller;

import com.example.kosapp.models.BoardingHouse;
import com.example.kosapp.repository.BoardingHouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boarding-houses")
@RequiredArgsConstructor
public class BoardingHouseController {

    private final BoardingHouseRepository repository;

    @GetMapping
    public List<BoardingHouse> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardingHouse> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public BoardingHouse create(@RequestBody BoardingHouse boardingHouse) {
        return repository.save(boardingHouse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardingHouse> update(@PathVariable Long id, @RequestBody BoardingHouse updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setAddress(updated.getAddress());
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
