package com.example.kosapp.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "inventory_items")
@Data
public class InventoryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    @Column(name = "item_condition")
    private String condition;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
}
