package com.example.kosapp.controller;

import com.example.kosapp.models.Voucher;
import com.example.kosapp.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/vouchers")
public class VoucherController {
    @Autowired
    private VoucherRepository voucherRepository;

    @GetMapping
    public List<Voucher> getAll() {
        return voucherRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Voucher> getById(@PathVariable Long id) {
        return voucherRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Voucher create(@RequestBody Voucher voucher) {
        return voucherRepository.save(voucher);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Voucher> update(@PathVariable Long id, @RequestBody Voucher details) {
        return voucherRepository.findById(id).map(voucher -> {
            voucher.setCode(details.getCode());
            voucher.setDiscount(details.getDiscount());
            voucher.setValidUntil(details.getValidUntil());
            return ResponseEntity.ok(voucherRepository.save(voucher));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return voucherRepository.findById(id).map(voucher -> {
            voucherRepository.delete(voucher);
            return ResponseEntity.noContent().<Void>build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-code/{code}")
    public ResponseEntity<Voucher> getByCode(@PathVariable String code) {
        return voucherRepository.findByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
