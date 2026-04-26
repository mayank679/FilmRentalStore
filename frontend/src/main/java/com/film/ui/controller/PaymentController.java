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
@RequestMapping("/payments")
public class PaymentController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer customerId,
                       @RequestParam(required=false) Integer staffId,
                       @RequestParam(required=false) Integer rentalId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (customerId != null) {
                data = apiService.getPaymentsByCustomer(customerId);
                filtered = true;
            } else if (staffId != null) {
                data = apiService.getPaymentsByStaff(staffId);
                filtered = true;
            } else if (rentalId != null) {
                data = apiService.getPaymentsByRental(rentalId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getPaymentsPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("payments", data);
        model.addAttribute("filterCustomerId", customerId);
        model.addAttribute("filterStaffId", staffId);
        model.addAttribute("filterRentalId", rentalId);
        return "payment/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer customerId,
            @RequestParam(required=false) Integer staffId,
            @RequestParam(required=false) Integer rentalId,
            @RequestParam(required=false) String date,
            Model model) {

        model.addAttribute("mode",             mode);
        model.addAttribute("searchId",         id);
        model.addAttribute("searchCustomerId", customerId);
        model.addAttribute("searchStaffId",    staffId);
        model.addAttribute("searchRentalId",   rentalId);
        model.addAttribute("searchDate",       date);

        if (mode == null) return "payment/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getPaymentById(id)); }
                case "customerId" -> { if (customerId != null)
                    result = apiService.getPaymentsByCustomer(customerId); }
                case "staffId" -> { if (staffId != null)
                    result = apiService.getPaymentsByStaff(staffId); }
                case "rentalId" -> { if (rentalId != null)
                    result = apiService.getPaymentsByRental(rentalId); }
                case "date" -> { if (date != null && !date.isBlank())
                    result = apiService.getPaymentsByDate(date); }
                case "customerView" -> { result = apiService.getPaymentsWithCustomer(); }
                case "staffView" -> { result = apiService.getPaymentsWithStaff(); }
                case "rentalView" -> { result = apiService.getPaymentsWithRental(); }
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
            model.addAttribute("payments", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("payments", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "payment/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> p = apiService.getPaymentById(id);
            if (p == null || p.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Payment #" + id + " does not exist.");
                return "redirect:/payments";
            }
            p.put("paymentId", id);
            model.addAttribute("payment", p);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Payment #" + id + " does not exist.");
            return "redirect:/payments";
        }
        return "payment/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "payment/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> p = apiService.getPaymentById(id);
            if (p == null || p.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Payment #" + id + " does not exist.");
                return "redirect:/payments";
            }
            p.put("paymentId", id);
            model.addAttribute("payment", p);
            model.addAttribute("paymentId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Payment #" + id + " does not exist.");
            return "redirect:/payments";
        }
        return "payment/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer customerId, @RequestParam Integer staffId,
                       @RequestParam Integer rentalId, @RequestParam String amount,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("customerId", customerId); req.put("staffId", staffId);
        req.put("rentalId", rentalId); req.put("amount", amount);
        try {
            apiService.createPayment(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/payments";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer customerId, @RequestParam Integer staffId,
                         @RequestParam Integer rentalId, @RequestParam String amount,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("customerId", customerId); req.put("staffId", staffId);
        req.put("rentalId", rentalId); req.put("amount", amount);
        try {
            apiService.updatePayment(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/payments";
    }
}
