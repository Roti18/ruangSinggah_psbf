package com.example.kosapp.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "boarding_houses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardingHouse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String address;
}
