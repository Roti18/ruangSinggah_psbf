package com.example.kosapp;

import com.example.kosapp.models.Tenant;
import com.example.kosapp.repository.TenantRepository;
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
class TenantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant saved;

    @BeforeEach
    void setup() {
        Tenant tenant = new Tenant();
        tenant.setName("Budi Santoso");
        tenant.setPhone("081234567890");
        saved = tenantRepository.save(tenant);
    }

    @Test
    void testGetAllTenants() throws Exception {
        mockMvc.perform(get("/api/tenants"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("Budi Santoso"));
    }

    @Test
    void testGetTenantById() throws Exception {
        mockMvc.perform(get("/api/tenants/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("Budi Santoso"))
                .andExpect(jsonPath("$.phone").value("081234567890"));
    }

    @Test
    void testCreateTenant() throws Exception {
        String json = """
                {"name":"Siti Rahayu","phone":"089876543210"}
                """;
        mockMvc.perform(post("/api/tenants")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Siti Rahayu"))
                .andExpect(jsonPath("$.phone").value("089876543210"));
    }

    @Test
    void testUpdateTenant() throws Exception {
        String json = """
                {"name":"Budi Santoso Updated","phone":"081111111111"}
                """;
        mockMvc.perform(put("/api/tenants/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Budi Santoso Updated"))
                .andExpect(jsonPath("$.phone").value("081111111111"));
    }

    @Test
    void testDeleteTenant() throws Exception {
        mockMvc.perform(delete("/api/tenants/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetTenantNotFound() throws Exception {
        mockMvc.perform(get("/api/tenants/99999"))
                .andExpect(status().isNotFound());
    }
}
