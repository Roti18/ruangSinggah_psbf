package com.example.kosapp;

import com.example.kosapp.models.BoardingHouse;
import com.example.kosapp.models.MaintenanceRequest;
import com.example.kosapp.models.Room;
import com.example.kosapp.models.RoomType;
import com.example.kosapp.repository.BoardingHouseRepository;
import com.example.kosapp.repository.MaintenanceRequestRepository;
import com.example.kosapp.repository.RoomRepository;
import com.example.kosapp.repository.RoomTypeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class MaintenanceRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MaintenanceRequestRepository maintenanceRequestRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BoardingHouseRepository boardingHouseRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Room savedRoom;
    private MaintenanceRequest savedRequest;

    @BeforeEach
    void setup() {
        BoardingHouse bh = new BoardingHouse();
        bh.setName("Kos Test");
        bh.setAddress("Jl. Test");
        BoardingHouse savedBH = boardingHouseRepository.save(bh);

        RoomType rt = new RoomType();
        rt.setName("Standard");
        RoomType savedRT = roomTypeRepository.save(rt);

        Room room = new Room();
        room.setName("Kamar 101");
        room.setPrice(500000.0);
        room.setStatus("AVAILABLE");
        room.setBoardingHouse(savedBH);
        room.setRoomType(savedRT);
        savedRoom = roomRepository.save(room);

        MaintenanceRequest req = new MaintenanceRequest();
        req.setDescription("AC tidak dingin");
        req.setStatus("PENDING");
        req.setCreatedAt(LocalDate.of(2024, 1, 5));
        req.setRoom(savedRoom);
        savedRequest = maintenanceRequestRepository.save(req);
    }

    @Test
    void testGetAllMaintenanceRequests() throws Exception {
        mockMvc.perform(get("/api/maintenance-requests"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value("AC tidak dingin"));
    }

    @Test
    void testGetMaintenanceRequestById() throws Exception {
        mockMvc.perform(get("/api/maintenance-requests/" + savedRequest.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRequest.getId()))
                .andExpect(jsonPath("$.description").value("AC tidak dingin"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testCreateMaintenanceRequest() throws Exception {
        String json = """
                {
                  "description": "Pintu kamar mandi rusak",
                  "status": "PENDING",
                  "createdAt": "2024-01-10",
                  "room": {"id": %d}
                }
                """.formatted(savedRoom.getId());

        mockMvc.perform(post("/api/maintenance-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Pintu kamar mandi rusak"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void testUpdateMaintenanceRequest() throws Exception {
        String json = """
                {
                  "description": "AC tidak dingin - SELESAI",
                  "status": "DONE",
                  "createdAt": "2024-01-06",
                  "room": {"id": %d}
                }
                """.formatted(savedRoom.getId());

        mockMvc.perform(put("/api/maintenance-requests/" + savedRequest.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("DONE"))
                .andExpect(jsonPath("$.description").value("AC tidak dingin - SELESAI"));
    }

    @Test
    void testDeleteMaintenanceRequest() throws Exception {
        mockMvc.perform(delete("/api/maintenance-requests/" + savedRequest.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetMaintenanceRequestNotFound() throws Exception {
        mockMvc.perform(get("/api/maintenance-requests/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetByRoom() throws Exception {
        mockMvc.perform(get("/api/maintenance-requests/by-room/" + savedRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetByStatus() throws Exception {
        mockMvc.perform(get("/api/maintenance-requests/by-status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
