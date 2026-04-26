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
@RequestMapping("/inventory")
public class InventoryController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer filmId,
                       @RequestParam(required=false) Integer storeId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (filmId != null) {
                data = apiService.getInventoryByFilm(filmId);
                filtered = true;
            } else if (storeId != null) {
                data = apiService.getInventoryByStore(storeId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getInventoryPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("inventory", data);
        model.addAttribute("filterFilmId", filmId);
        model.addAttribute("filterStoreId", storeId);
        return "inventory/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer filmId,
            @RequestParam(required=false) Integer storeId,
            Model model) {

        model.addAttribute("mode",          mode);
        model.addAttribute("searchId",      id);
        model.addAttribute("searchFilmId",  filmId);
        model.addAttribute("searchStoreId", storeId);

        if (mode == null) return "inventory/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getInventoryById(id)); }
                case "filmId" -> { if (filmId != null)
                    result = apiService.getInventoryByFilm(filmId); }
                case "storeId" -> { if (storeId != null)
                    result = apiService.getInventoryByStore(storeId); }
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
            model.addAttribute("inventory", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("inventory", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "inventory/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> inv = apiService.getInventoryById(id);
            if (inv == null || inv.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Inventory #" + id + " does not exist.");
                return "redirect:/inventory";
            }
            inv.put("inventoryId", id);
            model.addAttribute("item", inv);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Inventory #" + id + " does not exist.");
            return "redirect:/inventory";
        }
        return "inventory/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "inventory/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> inv = apiService.getInventoryById(id);
            if (inv == null || inv.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Inventory #" + id + " does not exist.");
                return "redirect:/inventory";
            }
            inv.put("inventoryId", id);
            model.addAttribute("item", inv);
            model.addAttribute("inventoryId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Inventory #" + id + " does not exist.");
            return "redirect:/inventory";
        }
        return "inventory/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer filmId, @RequestParam Integer storeId,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("filmId", filmId); req.put("storeId", storeId);
        try {
            apiService.createInventory(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/inventory";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam Integer filmId, @RequestParam Integer storeId,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("filmId", filmId); req.put("storeId", storeId);
        try {
            apiService.updateInventory(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/inventory";
    }
}
