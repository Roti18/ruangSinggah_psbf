package com.example.kosapp.controller;

import com.example.kosapp.models.InventoryItem;
import com.example.kosapp.repository.InventoryItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/inventory-items")
public class InventoryItemController {
    @Autowired
    private InventoryItemRepository inventoryItemRepository;

    @GetMapping
    public List<InventoryItem> getAll() {
        return inventoryItemRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryItem> getById(@PathVariable Long id) {
        return inventoryItemRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public InventoryItem create(@RequestBody InventoryItem item) {
        return inventoryItemRepository.save(item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryItem> update(@PathVariable Long id, @RequestBody InventoryItem details) {
        return inventoryItemRepository.findById(id).map(item -> {
            item.setName(details.getName());
            item.setCondition(details.getCondition());
            item.setRoom(details.getRoom());
            return ResponseEntity.ok(inventoryItemRepository.save(item));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return inventoryItemRepository.findById(id).map(item -> {
            inventoryItemRepository.delete(item);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-room/{id}")
    public List<InventoryItem> getByRoom(@PathVariable Long id) {
        return inventoryItemRepository.findByRoomId(id);
    }
}
