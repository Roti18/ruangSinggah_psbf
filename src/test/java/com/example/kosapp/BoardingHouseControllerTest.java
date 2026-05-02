package com.example.kosapp;

import com.example.kosapp.models.BoardingHouse;
import com.example.kosapp.repository.BoardingHouseRepository;
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
class BoardingHouseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BoardingHouseRepository boardingHouseRepository;

    private BoardingHouse saved;

    @BeforeEach
    void setup() {
        BoardingHouse bh = new BoardingHouse();
        bh.setName("Kos Melati");
        bh.setAddress("Jl. Melati No. 1");
        saved = boardingHouseRepository.save(bh);
    }

    @Test
    void testGetAllBoardingHouses() throws Exception {
        mockMvc.perform(get("/api/boarding-houses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Kos Melati"));
    }

    @Test
    void testGetBoardingHouseById() throws Exception {
        mockMvc.perform(get("/api/boarding-houses/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Kos Melati"))
                .andExpect(jsonPath("$.address").value("Jl. Melati No. 1"));
    }

    @Test
    void testCreateBoardingHouse() throws Exception {
        String json = """
                {"name":"Kos Mawar","address":"Jl. Mawar No. 2"}
                """;
        mockMvc.perform(post("/api/boarding-houses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Kos Mawar"))
                .andExpect(jsonPath("$.address").value("Jl. Mawar No. 2"));
    }

    @Test
    void testUpdateBoardingHouse() throws Exception {
        String json = """
                {"name":"Kos Melati Updated","address":"Jl. Melati No. 99"}
                """;
        mockMvc.perform(put("/api/boarding-houses/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kos Melati Updated"))
                .andExpect(jsonPath("$.address").value("Jl. Melati No. 99"));
    }

    @Test
    void testDeleteBoardingHouse() throws Exception {
        mockMvc.perform(delete("/api/boarding-houses/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetBoardingHouseNotFound() throws Exception {
        mockMvc.perform(get("/api/boarding-houses/99999"))
                .andExpect(status().isNotFound());
    }
}
