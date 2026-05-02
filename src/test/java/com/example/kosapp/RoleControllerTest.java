package com.example.kosapp;

import com.example.kosapp.models.Role;
import com.example.kosapp.repository.RoleRepository;
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
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    private Role saved;

    @BeforeEach
    void setup() {
        Role role = new Role();
        role.setName("ADMIN");
        saved = roleRepository.save(role);
    }

    @Test
    void testGetAllRoles() throws Exception {
        mockMvc.perform(get("/api/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].name").value("ADMIN"));
    }

    @Test
    void testGetRoleById() throws Exception {
        mockMvc.perform(get("/api/roles/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.name").value("ADMIN"));
    }

    @Test
    void testCreateRole() throws Exception {
        String json = """
                {"name":"TENANT"}
                """;
        mockMvc.perform(post("/api/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("TENANT"));
    }

    @Test
    void testUpdateRole() throws Exception {
        String json = """
                {"name":"STAFF"}
                """;
        mockMvc.perform(put("/api/roles/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("STAFF"));
    }

    @Test
    void testDeleteRole() throws Exception {
        mockMvc.perform(delete("/api/roles/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRoleNotFound() throws Exception {
        mockMvc.perform(get("/api/roles/99999"))
                .andExpect(status().isNotFound());
    }
}
