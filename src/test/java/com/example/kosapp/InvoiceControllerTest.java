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
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private InvoiceRepository invoiceRepository;

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
    private Invoice savedInvoice;

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

        Invoice invoice = new Invoice();
        invoice.setRental(savedRental);
        invoice.setTotal(6000000.0);
        savedInvoice = invoiceRepository.save(invoice);
    }

    @Test
    void testGetAllInvoices() throws Exception {
        mockMvc.perform(get("/api/invoices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].total").value(6000000.0));
    }

    @Test
    void testGetInvoiceById() throws Exception {
        mockMvc.perform(get("/api/invoices/" + savedInvoice.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedInvoice.getId()))
                .andExpect(jsonPath("$.total").value(6000000.0));
    }

    @Test
    void testCreateInvoice() throws Exception {
        String json = """
                {
                  "total": 9000000,
                  "rental": {"id": %d}
                }
                """.formatted(savedRental.getId());

        mockMvc.perform(post("/api/invoices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.total").value(9000000.0));
    }

    @Test
    void testUpdateInvoice() throws Exception {
        String json = """
                {
                  "total": 7500000,
                  "rental": {"id": %d}
                }
                """.formatted(savedRental.getId());

        mockMvc.perform(put("/api/invoices/" + savedInvoice.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(7500000.0));
    }

    @Test
    void testDeleteInvoice() throws Exception {
        mockMvc.perform(delete("/api/invoices/" + savedInvoice.getId()))
                .andExpect(status().isNoContent());
    }

    @Test
    void testGetInvoiceNotFound() throws Exception {
        mockMvc.perform(get("/api/invoices/99999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetInvoicesByRental() throws Exception {
        mockMvc.perform(get("/api/invoices/by-rental/" + savedRental.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }
}
