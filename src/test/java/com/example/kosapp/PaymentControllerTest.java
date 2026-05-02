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
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PaymentRepository paymentRepository;

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

    private Rental savedRental;
    private Payment savedPayment;

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
        Room savedRoom = roomRepository.save(room);

        Tenant tenant = new Tenant();
        tenant.setName("Budi Santoso");
        tenant.setPhone("081234567890");
        Tenant savedTenant = tenantRepository.save(tenant);

        Rental rental = new Rental();
        rental.setTenant(savedTenant);
        rental.setRoom(savedRoom);
        rental.setStartDate(LocalDate.of(2024, 1, 1));
        rental.setEndDate(LocalDate.of(2024, 12, 31));
        savedRental = rentalRepository.save(rental);

        Payment payment = new Payment();
        payment.setRental(savedRental);
        payment.setAmount(500000.0);
        payment.setDate(LocalDate.of(2024, 1, 5));
        savedPayment = paymentRepository.save(payment);
    }

    @Test
    void testGetAllPayments() throws Exception {
        mockMvc.perform(get("/api/payments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].amount").value(500000.0));
    }

    @Test
    void testGetPaymentById() throws Exception {
        mockMvc.perform(get("/api/payments/" + savedPayment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedPayment.getId()))
                .andExpect(jsonPath("$.amount").value(500000.0))
                .andExpect(jsonPath("$.date").value("2024-01-05"));
    }

    @Test
    void testCreatePayment() throws Exception {
        String json = """
                {
                  "amount": 750000,
                  "date": "2024-02-05",
                  "rental": {"id": %d}
                }
                """.formatted(savedRental.getId());

        mockMvc.perform(post("/api/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.amount").value(750000.0))
                .andExpect(jsonPath("$.date").value("2024-02-05"));
    }

    @Test
    void testUpdatePayment() throws Exception {
        String json = """
                {
                  "amount": 600000,
                  "date": "2024-01-10",
                  "rental": {"id": %d}
                }
                """.formatted(savedRental.getId());

        mockMvc.perform(put("/api/payments/" + savedPayment.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(600000.0))
                .andExpect(jsonPath("$.date").value("2024-01-10"));
    }

    @Test
    void testDeletePayment() throws Exception {
        mockMvc.perform(delete("/api/payments/" + savedPayment.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetPaymentNotFound() throws Exception {
        mockMvc.perform(get("/api/payments/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetPaymentsByRental() throws Exception {
        mockMvc.perform(get("/api/payments/by-rental/" + savedRental.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
