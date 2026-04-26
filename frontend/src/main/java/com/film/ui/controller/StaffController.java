package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/staff")
public class StaffController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer storeId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (storeId != null) {
                data = apiService.getStaffByStore(storeId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getStaffPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("staffList", data);
        model.addAttribute("filterStoreId", storeId);
        return "staff/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer storeId,
            @RequestParam(required=false) String username,
            @RequestParam(required=false) String active,
            Model model) {

        model.addAttribute("mode",           mode);
        model.addAttribute("searchId",       id);
        model.addAttribute("searchStoreId",  storeId);
        model.addAttribute("searchUsername", username);
        model.addAttribute("searchActive",   active);

        if (mode == null) return "staff/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getStaffById(id)); }
                case "storeId" -> { if (storeId != null)
                    result = apiService.getStaffByStore(storeId); }
                case "username" -> { if (username != null && !username.isBlank())
                    result = List.of(apiService.getStaffByUsername(username)); }
                case "active" -> {
                    Boolean activeVal = (active != null && !active.isBlank()) ? Boolean.valueOf(active) : null;
                    result = apiService.getStaffByFilter(activeVal); }
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        if (result instanceof List) {
            List<?> allResults = (List<?>) result;
            int pageSize = 10;
            int totalResults = allResults.size();
            int totalPages = (int) Math.ceil((double) totalResults / pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page < 0) page = 0;
            if (page >= totalPages) page = totalPages - 1;
            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalResults);
            model.addAttribute("staffList", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("staffList", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "staff/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> s = apiService.getStaffById(id);
            if (s == null || s.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Staff #" + id + " does not exist.");
                return "redirect:/staff";
            }
            s.put("staffId", id);
            model.addAttribute("staff", s);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Staff #" + id + " does not exist.");
            return "redirect:/staff";
        }
        return "staff/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "staff/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> s = apiService.getStaffById(id);
            if (s == null || s.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Staff #" + id + " does not exist.");
                return "redirect:/staff";
            }
            s.put("staffId", id);
            model.addAttribute("staff", s);
            model.addAttribute("staffId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Staff #" + id + " does not exist.");
            return "redirect:/staff";
        }
        return "staff/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String firstName, @RequestParam String lastName,
                       @RequestParam Integer addressId, @RequestParam Integer storeId,
                       @RequestParam String username,
                       @RequestParam(required=false) String email,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("firstName", firstName); req.put("lastName", lastName);
        req.put("addressId", addressId); req.put("storeId", storeId);
        req.put("username", username);
        if (email != null && !email.isBlank()) req.put("email", email);
        try {
            apiService.createStaff(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/staff";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam String firstName, @RequestParam String lastName,
                         @RequestParam Integer addressId, @RequestParam Integer storeId,
                         @RequestParam String username,
                         @RequestParam(required=false) String email,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("firstName", firstName); req.put("lastName", lastName);
        req.put("addressId", addressId); req.put("storeId", storeId);
        req.put("username", username);
        if (email != null && !email.isBlank()) req.put("email", email);
        try {
            apiService.updateStaff(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/staff";
    }
}
