package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stores")
public class StoreController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Map<String,Object> resp = apiService.getStoresPaged(page);
            model.addAttribute("stores",      resp.get("content"));
            model.addAttribute("currentPage", resp.get("number"));
            model.addAttribute("totalPages",  resp.get("totalPages"));
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "store/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            Model model) {

        model.addAttribute("mode",     mode);
        model.addAttribute("searchId", id);

        if (mode == null) return "store/search";

        Object result = null;
        try {
            if ("id".equals(mode) && id != null) {
                result = List.of(apiService.getStoreById(id));
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        if (result instanceof List) {
            List<?> allResults = (List<?>) result;
            model.addAttribute("stores", allResults);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("stores", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "store/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> s = apiService.getStoreById(id);
            if (s == null || s.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Store #" + id + " does not exist.");
                return "redirect:/stores";
            }
            s.put("storeId", id);
            model.addAttribute("store", s);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Store #" + id + " does not exist.");
            return "redirect:/stores";
        }
        return "store/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "store/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> s = apiService.getStoreById(id);
            if (s == null || s.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Store #" + id + " does not exist.");
                return "redirect:/stores";
            }
            s.put("storeId", id);
            model.addAttribute("store", s);
            model.addAttribute("storeId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Store #" + id + " does not exist.");
            return "redirect:/stores";
        }
        return "store/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer managerStaffId, @RequestParam Integer addressId,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("managerStaffId", managerStaffId); req.put("addressId", addressId);
        try {
            apiService.createStore(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/stores";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer managerStaffId, @RequestParam Integer addressId,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("managerStaffId", managerStaffId); req.put("addressId", addressId);
        try {
            apiService.updateStore(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/stores";
    }
}
