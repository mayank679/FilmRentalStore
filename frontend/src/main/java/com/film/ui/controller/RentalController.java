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
@RequestMapping("/rentals")
public class RentalController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer customerId,
                       @RequestParam(required=false) Integer staffId,
                       @RequestParam(required=false) Integer inventoryId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (customerId != null) { data = apiService.getRentalsByCustomer(customerId); filtered = true; }
            else if (staffId != null) { data = apiService.getRentalsByStaff(staffId); filtered = true; }
            else if (inventoryId != null) { data = apiService.getRentalsByInventory(inventoryId); filtered = true; }
            else {
                Map<String,Object> resp = apiService.getRentalsPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        if (filtered && data.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        model.addAttribute("rentals", data);
        return "rental/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer customerId,
            @RequestParam(required=false) Integer staffId,
            @RequestParam(required=false) Integer inventoryId,
            @RequestParam(required=false) String startDate,
            @RequestParam(required=false) String endDate,
            @RequestParam(required=false) String returnStartDate,
            @RequestParam(required=false) String returnEndDate,
            Model model) {

        model.addAttribute("mode",             mode);
        model.addAttribute("searchId",         id);
        model.addAttribute("searchCustomerId", customerId);
        model.addAttribute("searchStaffId",    staffId);
        model.addAttribute("searchInventoryId",inventoryId);
        model.addAttribute("startDate",        startDate);
        model.addAttribute("endDate",          endDate);
        model.addAttribute("returnStartDate",  returnStartDate);
        model.addAttribute("returnEndDate",    returnEndDate);

        if (mode == null) return "rental/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getRentalById(id)); }
                case "customerId" -> { if (customerId != null)
                    result = apiService.getRentalsByCustomer(customerId); }
                case "staffId" -> { if (staffId != null)
                    result = apiService.getRentalsByStaff(staffId); }
                case "inventoryId" -> { if (inventoryId != null)
                    result = apiService.getRentalsByInventory(inventoryId); }
                case "dateRange" -> { if (startDate != null && endDate != null)
                    result = apiService.getRentalsByDateRange(startDate, endDate); }
                case "returnDateRange" -> { if (returnStartDate != null && returnEndDate != null)
                    result = apiService.getRentalsByReturnDateRange(returnStartDate, returnEndDate); }
                case "lastUpdateRange" -> { if (startDate != null && endDate != null)
                    result = apiService.getRentalsByLastUpdateRange(startDate, endDate); }
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
            model.addAttribute("rentals", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("rentals", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "rental/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> r = apiService.getRentalById(id);
            if (r == null || r.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Rental #" + id + " does not exist.");
                return "redirect:/rentals";
            }
            r.put("rentalId", id);
            model.addAttribute("rental", r);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Rental #" + id + " does not exist.");
            return "redirect:/rentals";
        }
        return "rental/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "rental/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> r = apiService.getRentalById(id);
            if (r == null || r.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Rental #" + id + " does not exist.");
                return "redirect:/rentals";
            }
            r.put("rentalId", id);
            model.addAttribute("rental", r);
            model.addAttribute("rentalId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Rental #" + id + " does not exist.");
            return "redirect:/rentals";
        }
        return "rental/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer inventoryId, @RequestParam Integer customerId,
                       @RequestParam Integer staffId,
                       @RequestParam(required=false) String rentalDate,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("inventoryId", inventoryId); req.put("customerId", customerId); req.put("staffId", staffId);
        if (rentalDate != null && !rentalDate.isBlank()) req.put("rentalDate", rentalDate);
        try {
            apiService.createRental(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/rentals";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer inventoryId, @RequestParam Integer customerId,
                         @RequestParam Integer staffId,
                         @RequestParam(required=false) String returnDate,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("inventoryId", inventoryId); req.put("customerId", customerId); req.put("staffId", staffId);
        if (returnDate != null && !returnDate.isBlank()) req.put("returnDate", returnDate);
        try {
            apiService.updateRental(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/rentals";
    }
}