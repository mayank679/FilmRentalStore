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
@RequestMapping("/customers")
public class CustomerController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer storeId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (storeId != null) {
                data = apiService.getCustomersByStore(storeId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getCustomersPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("customers", data);
        model.addAttribute("filterStoreId", storeId);
        return "customer/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer storeId,
            Model model) {

        model.addAttribute("mode",          mode);
        model.addAttribute("searchId",      id);
        model.addAttribute("searchStoreId", storeId);

        if (mode == null) return "customer/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getCustomerById(id)); }
                case "storeId" -> { if (storeId != null)
                    result = apiService.getCustomersByStore(storeId); }
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
            model.addAttribute("customers", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("customers", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "customer/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> c = apiService.getCustomerById(id);
            if (c == null || c.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Customer #" + id + " does not exist.");
                return "redirect:/customers";
            }
            c.put("customerId", id);
            model.addAttribute("customer", c);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Customer #" + id + " does not exist.");
            return "redirect:/customers";
        }
        return "customer/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "customer/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> c = apiService.getCustomerById(id);
            if (c == null || c.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Customer #" + id + " does not exist.");
                return "redirect:/customers";
            }
            c.put("customerId", id);
            model.addAttribute("customer", c);
            model.addAttribute("customerId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Customer #" + id + " does not exist.");
            return "redirect:/customers";
        }
        return "customer/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer storeId, @RequestParam String firstName,
                       @RequestParam String lastName, @RequestParam String email,
                       @RequestParam Integer addressId, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("storeId", storeId); req.put("firstName", firstName);
        req.put("lastName", lastName); req.put("email", email);
        req.put("addressId", addressId);
        try {
            apiService.createCustomer(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/customers";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer storeId, @RequestParam String firstName,
                         @RequestParam String lastName, @RequestParam String email,
                         @RequestParam Integer addressId, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("storeId", storeId); req.put("firstName", firstName);
        req.put("lastName", lastName); req.put("email", email);
        req.put("addressId", addressId);
        try {
            apiService.updateCustomer(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/customers";
    }
}
