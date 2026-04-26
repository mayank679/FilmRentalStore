package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/countries")
public class CountryController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Map<String, Object> countryPage = apiService.getCountries(page);
        model.addAttribute("countries",   countryPage.get("content"));
        model.addAttribute("currentPage", countryPage.get("number"));
        model.addAttribute("totalPages",  countryPage.get("totalPages"));
        return "country/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) String name,
            Model model) {

        model.addAttribute("mode",       mode);
        model.addAttribute("searchId",   id);
        model.addAttribute("searchName", name);

        if (mode == null) return "country/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getCountryById(id)); }
                case "name" -> { if (name != null && !name.isBlank())
                    result = apiService.getCountriesByName(name); }
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
            model.addAttribute("countries", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("countries", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "country/search";
    }

    @GetMapping("/create")
    public String form() { return "country/form"; }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> country = apiService.getCountryById(id);
            if (country == null || country.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Country #" + id + " does not exist.");
                return "redirect:/countries";
            }
            model.addAttribute("country", country);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Country #" + id + " does not exist.");
            return "redirect:/countries";
        }
        return "country/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> country = apiService.getCountryById(id);
            if (country == null || country.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Country #" + id + " does not exist.");
                return "redirect:/countries";
            }
            model.addAttribute("country", country);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Country #" + id + " does not exist.");
            return "redirect:/countries";
        }
        return "country/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String country, RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("country", country);
        boolean ok = apiService.createCountry(req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record added successfully." : "Failed to add record. Please check your input.");
        return "redirect:/countries";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @RequestParam String country, RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("country", country);
        boolean ok = apiService.updateCountry(id, req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record updated successfully." : "Failed to update record. Please check your input.");
        return "redirect:/countries";
    }
}
