package com.example.kosapp;

import com.example.kosapp.models.BoardingHouse;
import com.example.kosapp.models.Room;
import com.example.kosapp.models.RoomType;
import com.example.kosapp.repository.BoardingHouseRepository;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BoardingHouseRepository boardingHouseRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private BoardingHouse savedBoardingHouse;
    private RoomType savedRoomType;
    private Room savedRoom;

    @BeforeEach
    void setup() {
        BoardingHouse bh = new BoardingHouse();
        bh.setName("Kos Test");
        bh.setAddress("Jl. Test No. 1");
        savedBoardingHouse = boardingHouseRepository.save(bh);

        RoomType rt = new RoomType();
        rt.setName("Standard");
        savedRoomType = roomTypeRepository.save(rt);

        Room room = new Room();
        room.setName("Kamar 101");
        room.setPrice(500000.0);
        room.setStatus("AVAILABLE");
        room.setBoardingHouse(savedBoardingHouse);
        room.setRoomType(savedRoomType);
        savedRoom = roomRepository.save(room);
    }

    @Test
    void testGetAllRooms() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Kamar 101"));
    }

    @Test
    void testGetRoomById() throws Exception {
        mockMvc.perform(get("/api/rooms/" + savedRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRoom.getId()))
                .andExpect(jsonPath("$.name").value("Kamar 101"))
                .andExpect(jsonPath("$.price").value(500000.0))
                .andExpect(jsonPath("$.status").value("AVAILABLE"));
    }

    @Test
    void testCreateRoom() throws Exception {
        String json = """
                {
                  "name": "Kamar 202",
                  "price": 750000,
                  "status": "AVAILABLE",
                  "boardingHouse": {"id": %d},
                  "roomType": {"id": %d}
                }
                """.formatted(savedBoardingHouse.getId(), savedRoomType.getId());

        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Kamar 202"))
                .andExpect(jsonPath("$.price").value(750000.0));
    }

    @Test
    void testUpdateRoom() throws Exception {
        String json = """
                {
                  "name": "Kamar 101 Updated",
                  "price": 600000,
                  "status": "OCCUPIED",
                  "boardingHouse": {"id": %d},
                  "roomType": {"id": %d}
                }
                """.formatted(savedBoardingHouse.getId(), savedRoomType.getId());

        mockMvc.perform(put("/api/rooms/" + savedRoom.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kamar 101 Updated"))
                .andExpect(jsonPath("$.status").value("OCCUPIED"))
                .andExpect(jsonPath("$.price").value(600000.0));
    }

    @Test
    void testDeleteRoom() throws Exception {
        mockMvc.perform(delete("/api/rooms/" + savedRoom.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRoomNotFound() throws Exception {
        mockMvc.perform(get("/api/rooms/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRoomsByStatus() throws Exception {
        mockMvc.perform(get("/api/rooms/by-status/AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].status").value("AVAILABLE"));
    }

    @Test
    void testGetRoomsByBoardingHouse() throws Exception {
        mockMvc.perform(get("/api/rooms/by-boarding-house/" + savedBoardingHouse.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
