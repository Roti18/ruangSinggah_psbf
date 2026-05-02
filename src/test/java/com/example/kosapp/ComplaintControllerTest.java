package com.example.kosapp;

import com.example.kosapp.models.Complaint;
import com.example.kosapp.models.Tenant;
import com.example.kosapp.repository.ComplaintRepository;
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

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class ComplaintControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private TenantRepository tenantRepository;

    private Tenant savedTenant;
    private Complaint savedComplaint;

    @BeforeEach
    void setup() {
        Tenant tenant = new Tenant();
        tenant.setName("Budi Santoso");
        tenant.setPhone("081234567890");
        savedTenant = tenantRepository.save(tenant);

        Complaint complaint = new Complaint();
        complaint.setDescription("Keran air rusak");
        complaint.setCreatedAt(LocalDate.of(2024, 1, 10));
        complaint.setTenant(savedTenant);
        savedComplaint = complaintRepository.save(complaint);
    }

    @Test
    void testGetAllComplaints() throws Exception {
        mockMvc.perform(get("/api/complaints"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].description").value("Keran air rusak"));
    }

    @Test
    void testGetComplaintById() throws Exception {
        mockMvc.perform(get("/api/complaints/" + savedComplaint.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedComplaint.getId()))
                .andExpect(jsonPath("$.description").value("Keran air rusak"));
    }

    @Test
    void testCreateComplaint() throws Exception {
        String json = """
                {
                  "description": "Lampu kamar mati",
                  "createdAt": "2024-01-15",
                  "tenant": {"id": %d}
                }
                """.formatted(savedTenant.getId());

        mockMvc.perform(post("/api/complaints")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Lampu kamar mati"));
    }

    @Test
    void testUpdateComplaint() throws Exception {
        String json = """
                {
                  "description": "Keran air sudah diperbaiki - closed",
                  "createdAt": "2024-01-12",
                  "tenant": {"id": %d}
                }
                """.formatted(savedTenant.getId());

        mockMvc.perform(put("/api/complaints/" + savedComplaint.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Keran air sudah diperbaiki - closed"));
    }

    @Test
    void testDeleteComplaint() throws Exception {
        mockMvc.perform(delete("/api/complaints/" + savedComplaint.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetComplaintNotFound() throws Exception {
        mockMvc.perform(get("/api/complaints/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetComplaintsByTenant() throws Exception {
        mockMvc.perform(get("/api/complaints/by-tenant/" + savedTenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
