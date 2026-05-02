package com.example.kosapp.controller;

import com.example.kosapp.models.Room;
import com.example.kosapp.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomRepository repository;

    @GetMapping
    public List<Room> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Room> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-status/{status}")
    public List<Room> getByStatus(@PathVariable String status) {
        return repository.findByStatus(status);
    }

    @GetMapping("/by-boarding-house/{boardingHouseId}")
    public List<Room> getByBoardingHouse(@PathVariable Long boardingHouseId) {
        return repository.findByBoardingHouseId(boardingHouseId);
    }

    @PostMapping
    public Room create(@RequestBody Room room) {
        return repository.save(room);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Room> update(@PathVariable Long id, @RequestBody Room updated) {
        return repository.findById(id).map(existing -> {
            existing.setName(updated.getName());
            existing.setPrice(updated.getPrice());
            existing.setStatus(updated.getStatus());
            existing.setBoardingHouse(updated.getBoardingHouse());
            existing.setRoomType(updated.getRoomType());
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
