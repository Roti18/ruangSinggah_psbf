package com.example.kosapp;

import com.example.kosapp.models.RoomType;
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
class RoomTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private RoomType saved;

    @BeforeEach
    void setup() {
        RoomType rt = new RoomType();
        rt.setName("Standard");
        saved = roomTypeRepository.save(rt);
    }

    @Test
    void testGetAllRoomTypes() throws Exception {
        mockMvc.perform(get("/api/room-types"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Standard"));
    }

    @Test
    void testGetRoomTypeById() throws Exception {
        mockMvc.perform(get("/api/room-types/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Standard"));
    }

    @Test
    void testCreateRoomType() throws Exception {
        String json = """
                {"name":"Deluxe"}
                """;
        mockMvc.perform(post("/api/room-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Deluxe"));
    }

    @Test
    void testUpdateRoomType() throws Exception {
        String json = """
                {"name":"VIP"}
                """;
        mockMvc.perform(put("/api/room-types/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("VIP"));
    }

    @Test
    void testDeleteRoomType() throws Exception {
        mockMvc.perform(delete("/api/room-types/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRoomTypeNotFound() throws Exception {
        mockMvc.perform(get("/api/room-types/99999"))
                .andExpect(status().isNotFound());
    }
}
