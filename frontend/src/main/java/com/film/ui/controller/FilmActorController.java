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
@RequestMapping("/filmactors")
public class FilmActorController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer filmId,
                       @RequestParam(required=false) Integer actorId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (filmId != null) {
                data = apiService.getFilmActorsByFilm(filmId);
                filtered = true;
            } else if (actorId != null) {
                data = apiService.getFilmActorsByActor(actorId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getFilmActorsPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) {
            if (filmId != null || actorId != null) filtered = true;
            else model.addAttribute("error", e.getMessage());
        }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("filmactors", data);
        model.addAttribute("filterFilmId", filmId);
        model.addAttribute("filterActorId", actorId);
        return "filmactor/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer filmId,
            @RequestParam(required=false) Integer actorId,
            Model model) {

        model.addAttribute("mode",          mode);
        model.addAttribute("searchFilmId",  filmId);
        model.addAttribute("searchActorId", actorId);

        if (mode == null) return "filmactor/search";

        Object result = null;
        try {
            switch (mode) {
                case "filmId" -> { if (filmId != null)
                    result = apiService.getFilmActorsByFilm(filmId); }
                case "actorId" -> { if (actorId != null)
                    result = apiService.getFilmActorsByActor(actorId); }
                case "actorFilm" -> { if (actorId != null && filmId != null)
                    result = List.of(apiService.getFilmActorById(actorId, filmId)); }
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
            model.addAttribute("filmactors", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("filmactors", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "filmactor/search";
    }

    @GetMapping("/{actorId}/{filmId}")
    public String getById(@PathVariable Integer actorId, @PathVariable Integer filmId,
                          Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> fa = apiService.getFilmActorById(actorId, filmId);
            if (fa == null || fa.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found.");
                return "redirect:/filmactors";
            }
            fa.put("actorId", actorId);
            fa.put("filmId", filmId);
            model.addAttribute("filmactor", fa);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmactors";
        }
        return "filmactor/detail";
    }

    @GetMapping("/{actorId}/{filmId}/edit")
    public String editForm(@PathVariable Integer actorId, @PathVariable Integer filmId,
                           Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> fa = apiService.getFilmActorById(actorId, filmId);
            if (fa == null || fa.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found.");
                return "redirect:/filmactors";
            }
            fa.put("actorId", actorId);
            fa.put("filmId", filmId);
            model.addAttribute("filmactor", fa);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmactors";
        }
        return "filmactor/form";
    }

    @GetMapping("/create")
    public String createForm() { return "filmactor/form"; }

    @PostMapping("/save")
    public String save(@RequestParam Integer actorId, @RequestParam Integer filmId,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("actorId", actorId); req.put("filmId", filmId);
        try {
            apiService.createFilmActor(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/filmactors";
    }

    @PostMapping("/{actorId}/{filmId}/update")
    public String update(@PathVariable Integer actorId, @PathVariable Integer filmId,
                         @RequestParam Integer newActorId, @RequestParam Integer newFilmId,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("actorId", newActorId); req.put("filmId", newFilmId);
        try {
            apiService.updateFilmActor(actorId, filmId, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/filmactors";
    }
}