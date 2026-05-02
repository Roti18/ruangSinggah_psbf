package com.example.kosapp.repository;

import com.example.kosapp.models.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByRoomId(Long roomId);
}
