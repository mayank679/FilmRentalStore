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
@RequestMapping("/films")
public class FilmController {

    @Autowired private ApiService apiService;

    // ── LIST ──────────────────────────────────────────────────────────────────
    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try { 
            List<Map<String, Object>> allFilms = apiService.getAllFilms();
            if (allFilms == null) allFilms = new ArrayList<>();
            int pageSize = 10;
            int totalFilms = allFilms.size();
            int totalPages = (int) Math.ceil((double) totalFilms / pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page < 0) page = 0;
            if (page >= totalPages) page = totalPages - 1;

            int start = page * pageSize;
            int end = Math.min(start + pageSize, totalFilms);
            List<Map<String, Object>> pagedFilms = allFilms.subList(start, end);

            model.addAttribute("films", pagedFilms);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
        }
        catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "film/list";
    }

    // ── STATS ─────────────────────────────────────────────────────────────────
    @GetMapping("/stats")
    public String stats(Model model) {
        try {
            model.addAttribute("countByCategory",      apiService.getFilmCountByCategory());
            model.addAttribute("countByRating",        apiService.getFilmCountByRating());
            model.addAttribute("avgRentalRate",        apiService.getFilmAvgRentalRate());
            model.addAttribute("avgLength",            apiService.getFilmAvgLength());
            model.addAttribute("totalReplacementCost", apiService.getFilmTotalReplacementCost());
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "film/stats";
    }

    // ── CATEGORIES FOR A FILM ─────────────────────────────────────────────────
    @GetMapping("/{id}/categories")
    public String filmCategories(@PathVariable Integer id, Model model) {
        try {
            model.addAttribute("filmWithCategories", apiService.getFilmWithCategories(id));
            model.addAttribute("filmId", id);
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); return "film/list"; }
        return "film/categories";
    }

    // ── CREATE ────────────────────────────────────────────────────────────────
    @GetMapping("/create")
    public String createForm(Model model) {
        try { model.addAttribute("languages", apiService.getAllLanguages()); } catch (Exception ignored) {}
        return "film/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String title,
                       @RequestParam(required=false) String description,
                       @RequestParam(required=false) Integer releaseYear,
                       @RequestParam Integer languageId,
                       @RequestParam(required=false) Integer rentalDuration,
                       @RequestParam(required=false) String rentalRate,
                       @RequestParam(required=false) Integer length,
                       @RequestParam(required=false) String replacementCost,
                       @RequestParam(required=false) String rating,
                       @RequestParam(required=false) String specialFeatures,
                       @RequestParam(required=false) Integer categoryIds,
                       RedirectAttributes ra,
                       Model model) {
        Map<String, Object> req = new HashMap<>();
        req.put("title", title);
        req.put("languageId", languageId);
        if (description    != null && !description.isBlank())     req.put("description", description);
        if (releaseYear    != null)                               req.put("releaseYear", releaseYear);
        if (rentalDuration != null)                               req.put("rentalDuration", rentalDuration);
        if (rentalRate     != null && !rentalRate.isBlank())      req.put("rentalRate", rentalRate);
        if (length         != null)                               req.put("length", length);
        if (replacementCost!= null && !replacementCost.isBlank()) req.put("replacementCost", replacementCost);
        if (rating         != null && !rating.isBlank())          req.put("rating", rating);
        if (specialFeatures!= null && !specialFeatures.isBlank()) req.put("specialFeatures", specialFeatures);
        if (categoryIds    != null)                               req.put("categoryIds", List.of(categoryIds));
        try {
            apiService.createFilm(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/films";
    }

    // ── READ BY ID ────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> f = apiService.getFilmById(id);
            if (f == null || f.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Film #" + id + " does not exist.");
                return "redirect:/films";
            }
            f.put("filmId", id);
            model.addAttribute("film", f);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Film #" + id + " does not exist.");
            return "redirect:/films";
        }
        return "film/detail";
    }

    // ── PUT ───────────────────────────────────────────────────────────────────
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> f = apiService.getFilmById(id);
            if (f == null || f.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Film #" + id + " does not exist.");
                return "redirect:/films";
            }
            f.put("filmId", id);
            model.addAttribute("film", f);
            model.addAttribute("filmId", id);
            model.addAttribute("languages", apiService.getAllLanguages());
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Film #" + id + " does not exist.");
            return "redirect:/films";
        }
        return "film/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam String title,
                         @RequestParam(required=false) String description,
                         @RequestParam(required=false) Integer releaseYear,
                         @RequestParam Integer languageId,
                         @RequestParam(required=false) Integer rentalDuration,
                         @RequestParam(required=false) String rentalRate,
                         @RequestParam(required=false) Integer length,
                         @RequestParam(required=false) String replacementCost,
                         @RequestParam(required=false) String rating,
                         @RequestParam(required=false) String specialFeatures,
                         @RequestParam(required=false) Integer categoryIds,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("title", title);
        req.put("languageId", languageId);
        if (description    != null && !description.isBlank())     req.put("description", description);
        if (releaseYear    != null)                               req.put("releaseYear", releaseYear);
        if (rentalDuration != null)                               req.put("rentalDuration", rentalDuration);
        if (rentalRate     != null && !rentalRate.isBlank())      req.put("rentalRate", rentalRate);
        if (length         != null)                               req.put("length", length);
        if (replacementCost!= null && !replacementCost.isBlank()) req.put("replacementCost", replacementCost);
        if (rating         != null && !rating.isBlank())          req.put("rating", rating);
        if (specialFeatures!= null && !specialFeatures.isBlank()) req.put("specialFeatures", specialFeatures);
        if (categoryIds    != null)                               req.put("categoryIds", List.of(categoryIds));
        try {
            apiService.updateFilm(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/films";
    }

    // ── FIRST 10 ──────────────────────────────────────────────────────────────
    @GetMapping("/first10")
    public String getFirst10(Model model) {
        try { model.addAttribute("films", apiService.getFirst10Films()); }
        catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "film/list";
    }

    // ── UNIFIED SEARCH ────────────────────────────────────────────────────────
    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String  mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) String  title,
            @RequestParam(required=false) String  rating,
            @RequestParam(required=false) Integer releaseYear,
            @RequestParam(required=false) Integer languageId,
            @RequestParam(required=false) String  languageName,
            @RequestParam(required=false) Integer rentalDuration,
            @RequestParam(required=false) String  rentalRate,
            @RequestParam(required=false) Integer filmId,
            @RequestParam(required=false) Integer length,
            @RequestParam(required=false) String  minRate,
            @RequestParam(required=false) String  maxRate,
            @RequestParam(required=false) String  minCost,
            @RequestParam(required=false) String  maxCost,
            Model model) {

        model.addAttribute("mode",                mode);
        model.addAttribute("searchId",            id);
        model.addAttribute("searchTitle",         title);
        model.addAttribute("searchRating",        rating);
        model.addAttribute("searchReleaseYear",   releaseYear);
        model.addAttribute("searchLanguageId",    languageId);
        model.addAttribute("searchLanguageName",  languageName);
        model.addAttribute("searchRentalDuration",rentalDuration);
        model.addAttribute("searchRentalRate",    rentalRate);
        model.addAttribute("searchFilmId",        filmId);
        model.addAttribute("searchLength",        length);
        model.addAttribute("searchMinRate",       minRate);
        model.addAttribute("searchMaxRate",       maxRate);
        model.addAttribute("searchMinCost",       minCost);
        model.addAttribute("searchMaxCost",       maxCost);

        if (mode == null) return "film/search";

        Object result = null;
        try {
            switch (mode) {
                case "id"             -> { if (id != null)
                    result = List.of(apiService.getFilmById(id)); }
                case "title"          -> { if (title != null && !title.isBlank())
                    result = apiService.getFilmsByTitle(title); }
                case "rating"         -> { if (rating != null && !rating.isBlank())
                    result = apiService.getFilmsByRating(rating); }
                case "releaseYear"    -> { if (releaseYear != null)
                    result = apiService.getFilmsByReleaseYear(releaseYear); }
                case "languageId"     -> { if (languageId != null)
                    result = apiService.getFilmsByLanguageId(languageId); }
                case "languageName"   -> { if (languageName != null && !languageName.isBlank())
                    result = apiService.getFilmsByLanguageName(languageName); }
                case "rentalDuration" -> { if (rentalDuration != null)
                    result = apiService.getFilmsByRentalDuration(rentalDuration); }
                case "rentalRate"     -> { if (rentalRate != null && !rentalRate.isBlank())
                    result = apiService.getFilmsByRentalRate(rentalRate); }
                case "idRating"       -> { if (filmId != null && rating != null && !rating.isBlank())
                    result = List.of(apiService.getFilmByIdAndRating(filmId, rating)); }
                case "ratingRate"     -> { if (rating != null && rentalRate != null)
                    result = apiService.getFilmsByRatingAndRate(rating, rentalRate); }
                case "lengthYear"     -> { if (length != null && releaseYear != null)
                    result = apiService.getFilmsByLengthAndYear(length, releaseYear); }
                case "rentalRange"    -> { if (minRate != null && maxRate != null)
                    result = apiService.getFilmsByRentalRateRange(minRate, maxRate); }
                case "replaceRange"   -> { if (minCost != null && maxCost != null)
                    result = apiService.getFilmsByReplacementCostRange(minCost, maxCost); }
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
            List<?> pagedResults = allResults.subList(start, end);
            model.addAttribute("films", pagedResults);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (pagedResults.isEmpty()) {
                model.addAttribute("errorMessage", "Record not found");
            }
        } else {
            model.addAttribute("films", result);
            if (result == null) {
                model.addAttribute("errorMessage", "Record not found");
            }
        }
        
        return "film/search";
    }
}