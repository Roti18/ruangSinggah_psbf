package com.example.kosapp.repository;

import com.example.kosapp.models.MaintenanceRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {
    List<MaintenanceRequest> findByRoomId(Long roomId);
    List<MaintenanceRequest> findByStatus(String status);
}
