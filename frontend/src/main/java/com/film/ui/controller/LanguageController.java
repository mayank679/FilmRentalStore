package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/languages")
public class LanguageController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Map<String,Object> resp = apiService.getLanguagesPaged(page);
            model.addAttribute("languages",   resp.get("content"));
            model.addAttribute("currentPage", resp.get("number"));
            model.addAttribute("totalPages",  resp.get("totalPages"));
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "language/list";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> l = apiService.getLanguageById(id);
            if (l == null || l.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Language #" + id + " does not exist.");
                return "redirect:/languages";
            }
            l.put("languageId", id);
            model.addAttribute("language", l);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Language #" + id + " does not exist.");
            return "redirect:/languages";
        }
        return "language/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "language/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> l = apiService.getLanguageById(id);
            if (l == null || l.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Language #" + id + " does not exist.");
                return "redirect:/languages";
            }
            l.put("languageId", id);
            model.addAttribute("language", l);
            model.addAttribute("languageId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Language #" + id + " does not exist.");
            return "redirect:/languages";
        }
        return "language/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String name, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>(); req.put("name", name);
        try {
            apiService.createLanguage(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/languages";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>(); req.put("name", name);
        try {
            apiService.updateLanguage(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/languages";
    }
}
