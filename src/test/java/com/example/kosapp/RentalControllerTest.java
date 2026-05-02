package com.example.kosapp;

import com.example.kosapp.models.*;
import com.example.kosapp.repository.*;
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
class RentalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RentalRepository rentalRepository;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private BoardingHouseRepository boardingHouseRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Tenant savedTenant;
    private Room savedRoom;
    private Rental savedRental;

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

        Tenant tenant = new Tenant();
        tenant.setName("Budi Santoso");
        tenant.setPhone("081234567890");
        savedTenant = tenantRepository.save(tenant);

        Rental rental = new Rental();
        rental.setTenant(savedTenant);
        rental.setRoom(savedRoom);
        rental.setStartDate(LocalDate.of(2024, 1, 1));
        rental.setEndDate(LocalDate.of(2024, 12, 31));
        savedRental = rentalRepository.save(rental);
    }

    @Test
    void testGetAllRentals() throws Exception {
        mockMvc.perform(get("/api/rentals"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetRentalById() throws Exception {
        mockMvc.perform(get("/api/rentals/" + savedRental.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedRental.getId()))
                .andExpect(jsonPath("$.startDate").value("2024-01-01"))
                .andExpect(jsonPath("$.endDate").value("2024-12-31"));
    }

    @Test
    void testCreateRental() throws Exception {
        String json = """
                {
                  "startDate": "2025-01-01",
                  "endDate": "2025-12-31",
                  "tenant": {"id": %d},
                  "room": {"id": %d}
                }
                """.formatted(savedTenant.getId(), savedRoom.getId());

        mockMvc.perform(post("/api/rentals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.startDate").value("2025-01-01"))
                .andExpect(jsonPath("$.endDate").value("2025-12-31"));
    }

    @Test
    void testUpdateRental() throws Exception {
        String json = """
                {
                  "startDate": "2024-03-01",
                  "endDate": "2024-09-30",
                  "tenant": {"id": %d},
                  "room": {"id": %d}
                }
                """.formatted(savedTenant.getId(), savedRoom.getId());

        mockMvc.perform(put("/api/rentals/" + savedRental.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.startDate").value("2024-03-01"))
                .andExpect(jsonPath("$.endDate").value("2024-09-30"));
    }

    @Test
    void testDeleteRental() throws Exception {
        mockMvc.perform(delete("/api/rentals/" + savedRental.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetRentalNotFound() throws Exception {
        mockMvc.perform(get("/api/rentals/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetRentalsByTenant() throws Exception {
        mockMvc.perform(get("/api/rentals/by-tenant/" + savedTenant.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    void testGetRentalsByRoom() throws Exception {
        mockMvc.perform(get("/api/rentals/by-room/" + savedRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
