package com.example.kosapp.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class PageController {

    private static final List<String> ADMIN_ENTITIES = List.of(
            "boarding-houses", "room-types", "rooms", "facilities",
            "tenants", "rentals", "payments", "invoices",
            "complaints", "maintenance-requests", "users", "roles",
            "activity-logs", "notifications", "expenses",
            "inventory-items", "reviews", "vouchers"
    );

        private static final List<String> STAFF_ENTITIES = List.of(
            "rooms", "complaints", "maintenance-requests", "inventory-items",
            "tenants", "rentals", "payments", "invoices"
        );

    @GetMapping("/{entity:[a-zA-Z\\-]+}")
    public String list(@PathVariable String entity, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        String role = (String) session.getAttribute("role");
        if (role == null || !(role.equals("ADMIN") || role.equals("STAFF"))) return "redirect:/";
        List<String> allowed = role.equals("ADMIN") ? ADMIN_ENTITIES : STAFF_ENTITIES;
        if (!allowed.contains(entity)) return "redirect:/";
        model.addAttribute("entity", entity);
        model.addAttribute("role", role);
        return "entity/list";
    }

    @GetMapping("/{entity:[a-zA-Z\\-]+}/create")
    public String create(@PathVariable String entity, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        String role = (String) session.getAttribute("role");
        if (role == null || !(role.equals("ADMIN") || role.equals("STAFF"))) return "redirect:/";
        List<String> allowed = role.equals("ADMIN") ? ADMIN_ENTITIES : STAFF_ENTITIES;
        if (!allowed.contains(entity)) return "redirect:/";
        model.addAttribute("entity", entity);
        model.addAttribute("mode", "create");
        return "entity/form";
    }

    @GetMapping("/{entity:[a-zA-Z\\-]+}/edit/{id}")
    public String edit(@PathVariable String entity, @PathVariable Long id, HttpSession session, Model model) {
        if (session.getAttribute("user") == null) return "redirect:/login";
        String role = (String) session.getAttribute("role");
        if (role == null || !(role.equals("ADMIN") || role.equals("STAFF"))) return "redirect:/";
        List<String> allowed = role.equals("ADMIN") ? ADMIN_ENTITIES : STAFF_ENTITIES;
        if (!allowed.contains(entity)) return "redirect:/";
        model.addAttribute("entity", entity);
        model.addAttribute("entityId", id);
        model.addAttribute("mode", "edit");
        return "entity/form";
    }
}
