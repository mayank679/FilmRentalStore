package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/cities")
public class CityController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Map<String, Object> cityPage = apiService.getCities(page);
        model.addAttribute("cities",      cityPage.get("content"));
        model.addAttribute("currentPage", (Integer) cityPage.get("number"));
        model.addAttribute("totalPages",  (Integer) cityPage.get("totalPages"));
        return "city/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) String name,
            @RequestParam(required=false) Integer countryId,
            Model model) {

        model.addAttribute("mode",            mode);
        model.addAttribute("searchId",        id);
        model.addAttribute("searchName",      name);
        model.addAttribute("searchCountryId", countryId);

        if (mode == null) return "city/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getCityById(id)); }
                case "name" -> { if (name != null && !name.isBlank())
                    result = apiService.getCitiesByName(name); }
                case "countryId" -> { if (countryId != null)
                    result = apiService.getCitiesByCountryId(countryId); }
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
            model.addAttribute("cities", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("cities", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "city/search";
    }

    @GetMapping("/create")
    public String form() { return "city/form"; }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> city = apiService.getCityById(id);
            if (city == null || city.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: City #" + id + " does not exist.");
                return "redirect:/cities";
            }
            model.addAttribute("city", city);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: City #" + id + " does not exist.");
            return "redirect:/cities";
        }
        return "city/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> city = apiService.getCityById(id);
            if (city == null || city.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: City #" + id + " does not exist.");
                return "redirect:/cities";
            }
            model.addAttribute("city", city);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: City #" + id + " does not exist.");
            return "redirect:/cities";
        }
        return "city/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String city, @RequestParam Integer countryId, RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("city", city); req.put("countryId", countryId);
        boolean ok = apiService.createCity(req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record added successfully." : "Failed to add record. Please check your input.");
        return "redirect:/cities";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @RequestParam String city, @RequestParam Integer countryId, RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("city", city); req.put("countryId", countryId);
        boolean ok = apiService.updateCity(id, req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record updated successfully." : "Failed to update record. Please check your input.");
        return "redirect:/cities";
    }
}
