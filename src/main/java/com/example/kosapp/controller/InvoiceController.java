package com.example.kosapp.controller;

import com.example.kosapp.models.Invoice;
import com.example.kosapp.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceRepository repository;

    @GetMapping
    public List<Invoice> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Invoice> getById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-rental/{rentalId}")
    public List<Invoice> getByRental(@PathVariable Long rentalId) {
        return repository.findByRentalId(rentalId);
    }

    @PostMapping
    public Invoice create(@RequestBody Invoice invoice) {
        return repository.save(invoice);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Invoice> update(@PathVariable Long id, @RequestBody Invoice updated) {
        return repository.findById(id).map(existing -> {
            existing.setRental(updated.getRental());
            existing.setTotal(updated.getTotal());
            return ResponseEntity.ok(repository.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) return ResponseEntity.notFound().build();
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
