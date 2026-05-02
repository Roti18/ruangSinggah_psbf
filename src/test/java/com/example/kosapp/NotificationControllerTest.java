package com.example.kosapp;

import com.example.kosapp.models.Notification;
import com.example.kosapp.repository.NotificationRepository;
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
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationRepository notificationRepository;

    private Notification saved;

    @BeforeEach
    void setup() {
        Notification notification = new Notification();
        notification.setMessage("Tagihan bulan ini sudah jatuh tempo");
        notification.setCreatedAt(LocalDate.of(2024, 1, 1));
        saved = notificationRepository.save(notification);
    }

    @Test
    void testGetAllNotifications() throws Exception {
        mockMvc.perform(get("/api/notifications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].message").value("Tagihan bulan ini sudah jatuh tempo"));
    }

    @Test
    void testGetNotificationById() throws Exception {
        mockMvc.perform(get("/api/notifications/" + saved.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(saved.getId()))
                .andExpect(jsonPath("$.message").value("Tagihan bulan ini sudah jatuh tempo"));
    }

    @Test
    void testCreateNotification() throws Exception {
        String json = """
                {"message":"Kamar Anda akan habis masa sewanya","createdAt":"2024-02-01"}
                """;
        mockMvc.perform(post("/api/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.message").value("Kamar Anda akan habis masa sewanya"));
    }

    @Test
    void testUpdateNotification() throws Exception {
        String json = """
                {"message":"Pesan Diperbarui","createdAt":"2024-01-15"}
                """;
        mockMvc.perform(put("/api/notifications/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Pesan Diperbarui"));
    }

    @Test
    void testDeleteNotification() throws Exception {
        mockMvc.perform(delete("/api/notifications/" + saved.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetNotificationNotFound() throws Exception {
        mockMvc.perform(get("/api/notifications/99999"))
                .andExpect(status().isNotFound());
    }
}
