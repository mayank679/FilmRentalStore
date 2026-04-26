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
@RequestMapping("/filmtexts")
public class FilmTextController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Map<String,Object> resp = apiService.getFilmTextsPaged(page);
            model.addAttribute("filmtexts",   resp.get("content"));
            model.addAttribute("currentPage", resp.get("number"));
            model.addAttribute("totalPages",  resp.get("totalPages"));
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "filmtext/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) String title,
            @RequestParam(required=false) String description,
            @RequestParam(required=false) String keyword,
            Model model) {

        model.addAttribute("mode",              mode);
        model.addAttribute("searchId",          id);
        model.addAttribute("searchTitle",       title);
        model.addAttribute("searchDescription", description);
        model.addAttribute("searchKeyword",     keyword);

        if (mode == null) return "filmtext/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getFilmTextById(id)); }
                case "title" -> { if (title != null && !title.isBlank())
                    result = apiService.searchFilmTextsByTitle(title); }
                case "description" -> { if (description != null && !description.isBlank())
                    result = apiService.searchFilmTextsByDescription(description); }
                case "keyword" -> { if (keyword != null && !keyword.isBlank())
                    result = apiService.searchFilmTextsByKeyword(keyword); }
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
            model.addAttribute("filmtexts", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("filmtexts", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "filmtext/search";
    }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> ft = apiService.getFilmTextById(id);
            if (ft == null || ft.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Film Text #" + id + " does not exist.");
                return "redirect:/filmtexts";
            }
            ft.put("filmId", id);
            model.addAttribute("filmtext", ft);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Film Text #" + id + " does not exist.");
            return "redirect:/filmtexts";
        }
        return "filmtext/detail";
    }

    @GetMapping("/create")
    public String createForm() { return "filmtext/form"; }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> ft = apiService.getFilmTextById(id);
            if (ft == null || ft.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Film Text #" + id + " does not exist.");
                return "redirect:/filmtexts";
            }
            ft.put("filmId", id);
            model.addAttribute("filmtext", ft);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Film Text #" + id + " does not exist.");
            return "redirect:/filmtexts";
        }
        return "filmtext/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam Integer filmId, @RequestParam String title,
                       @RequestParam String description, RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("filmId", filmId); req.put("title", title); req.put("description", description);
        try {
            apiService.createFilmText(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/filmtexts";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam String title, @RequestParam String description,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("title", title); req.put("description", description);
        try {
            apiService.updateFilmText(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/filmtexts";
    }
}
