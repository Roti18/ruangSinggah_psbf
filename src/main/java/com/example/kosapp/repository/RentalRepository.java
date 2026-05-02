package com.example.kosapp.repository;

import com.example.kosapp.models.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
    List<Rental> findByTenantId(Long tenantId);
    List<Rental> findByRoomId(Long roomId);
}
