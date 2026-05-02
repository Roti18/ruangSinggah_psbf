package com.example.kosapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "boarding_house_id")
    private BoardingHouse boardingHouse;

    @ManyToOne
    @JoinColumn(name = "room_type_id")
    private RoomType roomType;

    private String name;

    private Double price;

    private String status;

    @ManyToMany
    @JoinTable(
        name = "room_facilities",
        joinColumns = @JoinColumn(name = "room_id"),
        inverseJoinColumns = @JoinColumn(name = "facility_id")
    )
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<Facility> facilities = new HashSet<>();
}
