package com.example.kosapp;

import com.example.kosapp.models.Facility;
import com.example.kosapp.repository.FacilityRepository;
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
class FacilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FacilityRepository facilityRepository;

    private Facility saved;

    @BeforeEach
    void setup() {
        Facility facility = new Facility();
        facility.setName("WiFi");
        saved = facilityRepository.save(facility);
    }

    @Test
    void testGetAllFacilities() throws Exception {
        mockMvc.perform(get("/api/facilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("WiFi"));
    }

    @Test
    void testGetFacilityById() throws Exception {
        mockMvc.perform(get("/api/facilities/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("WiFi"));
    }

    @Test
    void testCreateFacility() throws Exception {
        String json = """
                {"name":"AC"}
                """;
        mockMvc.perform(post("/api/facilities")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("AC"));
    }

    @Test
    void testUpdateFacility() throws Exception {
        String json = """
                {"name":"Kamar Mandi Dalam"}
                """;
        mockMvc.perform(put("/api/facilities/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Kamar Mandi Dalam"));
    }

    @Test
    void testDeleteFacility() throws Exception {
        mockMvc.perform(delete("/api/facilities/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetFacilityNotFound() throws Exception {
        mockMvc.perform(get("/api/facilities/99999"))
                .andExpect(status().isNotFound());
    }
}
