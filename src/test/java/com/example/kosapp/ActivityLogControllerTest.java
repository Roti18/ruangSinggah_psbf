package com.example.kosapp;

import com.example.kosapp.models.ActivityLog;
import com.example.kosapp.repository.ActivityLogRepository;
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
class ActivityLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActivityLogRepository activityLogRepository;

    private ActivityLog saved;

    @BeforeEach
    void setup() {
        ActivityLog log = new ActivityLog();
        log.setAction("USER_LOGIN");
        log.setCreatedAt(LocalDate.of(2024, 1, 1));
        saved = activityLogRepository.save(log);
    }

    @Test
    void testGetAllActivityLogs() throws Exception {
        mockMvc.perform(get("/api/activity-logs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].action").value("USER_LOGIN"));
    }

    @Test
    void testGetActivityLogById() throws Exception {
        mockMvc.perform(get("/api/activity-logs/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.action").value("USER_LOGIN"));
    }

    @Test
    void testCreateActivityLog() throws Exception {
        String json = """
                {"action":"ROOM_BOOKED","createdAt":"2024-02-01"}
                """;
        mockMvc.perform(post("/api/activity-logs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.action").value("ROOM_BOOKED"));
    }

    @Test
    void testUpdateActivityLog() throws Exception {
        String json = """
                {"action":"USER_LOGOUT","createdAt":"2024-01-02"}
                """;
        mockMvc.perform(put("/api/activity-logs/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.action").value("USER_LOGOUT"));
    }

    @Test
    void testDeleteActivityLog() throws Exception {
        mockMvc.perform(delete("/api/activity-logs/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetActivityLogNotFound() throws Exception {
        mockMvc.perform(get("/api/activity-logs/99999"))
                .andExpect(status().isNotFound());
    }
}
