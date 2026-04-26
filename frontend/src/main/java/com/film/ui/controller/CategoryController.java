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
@RequestMapping("/categories")
public class CategoryController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Map<String,Object> resp = apiService.getCategoriesPaged(page);
            model.addAttribute("categories",  resp.get("content"));
            model.addAttribute("currentPage", resp.get("number"));
            model.addAttribute("totalPages",  resp.get("totalPages"));
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "category/list";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required=false) Integer categoryId, Model model) {
        if (categoryId != null) {
            try {
                Map<String,Object> c = apiService.getCategoryById(categoryId);
                if (c == null || c.isEmpty()) {
                    model.addAttribute("errorMessage", "Record not found");
                } else {
                    c.put("categoryId", categoryId);
                    model.addAttribute("category", c);
                }
            } catch (Exception e) { model.addAttribute("errorMessage", "Record not found"); }
        }
        model.addAttribute("searchId", categoryId);
        return "category/search";
    }

    @GetMapping("/create")
    public String createForm() { return "category/form"; }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> c = apiService.getCategoryById(id);
            if (c == null || c.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Category #" + id + " does not exist.");
                return "redirect:/categories";
            }
            c.put("categoryId", id);
            model.addAttribute("category", c);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Category #" + id + " does not exist.");
            return "redirect:/categories";
        }
        return "category/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> c = apiService.getCategoryById(id);
            if (c == null || c.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Category #" + id + " does not exist.");
                return "redirect:/categories";
            }
            c.put("categoryId", id);
            model.addAttribute("category", c);
            model.addAttribute("categoryId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Category #" + id + " does not exist.");
            return "redirect:/categories";
        }
        return "category/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String name, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>(); req.put("name", name);
        try {
            apiService.createCategory(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/categories";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id, @RequestParam String name, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>(); req.put("name", name);
        try {
            apiService.updateCategory(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/categories";
    }
}
