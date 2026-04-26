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
@RequestMapping("/filmcategories")
public class FilmCategoryController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(required=false) Integer filmId,
                       @RequestParam(required=false) Integer categoryId,
                       @RequestParam(defaultValue = "0") int page,
                       Model model) {
        List<Map<String,Object>> data = new ArrayList<>();
        boolean filtered = false;
        try {
            if (filmId != null) {
                data = apiService.getFilmCategoriesByFilm(filmId);
                filtered = true;
            } else if (categoryId != null) {
                data = apiService.getFilmCategoriesByCategory(categoryId);
                filtered = true;
            } else {
                Map<String,Object> resp = apiService.getFilmCategoriesPaged(page);
                data = (List<Map<String,Object>>) resp.get("content");
                model.addAttribute("currentPage", resp.get("number"));
                model.addAttribute("totalPages",  resp.get("totalPages"));
            }
        } catch (Exception e) {
            if (filmId != null || categoryId != null) filtered = true;
            else model.addAttribute("error", e.getMessage());
        }
        if (filtered && data.isEmpty()) {
            model.addAttribute("errorMessage", "Record not found");
        }
        model.addAttribute("filmcategories", data);
        model.addAttribute("filterFilmId", filmId);
        model.addAttribute("filterCategoryId", categoryId);
        return "filmcategory/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer filmId,
            @RequestParam(required=false) Integer categoryId,
            @RequestParam(required=false) String rating,
            Model model) {

        model.addAttribute("mode",             mode);
        model.addAttribute("searchFilmId",     filmId);
        model.addAttribute("searchCategoryId", categoryId);
        model.addAttribute("searchRating",     rating);

        if (mode == null) return "filmcategory/search";

        Object result = null;
        try {
            switch (mode) {
                case "filmId" -> { if (filmId != null)
                    result = apiService.getFilmCategoriesByFilm(filmId); }
                case "categoryId" -> { if (categoryId != null)
                    result = apiService.getFilmCategoriesByCategory(categoryId); }
                case "filmCategoryId" -> { if (filmId != null && categoryId != null)
                    result = List.of(apiService.getFilmCategoryById(filmId, categoryId)); }
                case "categoryFilms" -> { if (categoryId != null)
                    result = apiService.getFilmsByCategory(categoryId); }
                case "filmCategories" -> { if (filmId != null)
                    result = apiService.getCategoriesByFilm(filmId); }
                case "categoryRating" -> { if (categoryId != null && rating != null && !rating.isBlank())
                    result = apiService.getFilmCategoriesByRating(categoryId, rating); }
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
            model.addAttribute("filmcategories", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("filmcategories", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "filmcategory/search";
    }

    @GetMapping("/{filmId}/{categoryId}")
    public String getById(@PathVariable Integer filmId, @PathVariable Integer categoryId,
                          Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> fc = apiService.getFilmCategoryById(filmId, categoryId);
            if (fc == null || fc.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found.");
                return "redirect:/filmcategories";
            }
            fc.put("filmId", filmId);
            fc.put("categoryId", categoryId);
            model.addAttribute("filmcategory", fc);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/detail";
    }

    @GetMapping("/{filmId}/{categoryId}/edit")
    public String editForm(@PathVariable Integer filmId, @PathVariable Integer categoryId,
                           Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> fc = apiService.getFilmCategoryById(filmId, categoryId);
            if (fc == null || fc.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found.");
                return "redirect:/filmcategories";
            }
            fc.put("filmId", filmId);
            fc.put("categoryId", categoryId);
            model.addAttribute("filmcategory", fc);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/form";
    }

    @GetMapping("/create")
    public String createForm() { return "filmcategory/form"; }

    @PostMapping("/save")
    public String save(@RequestParam Integer filmId, @RequestParam Integer categoryId,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("filmId", filmId); req.put("categoryId", categoryId);
        try {
            apiService.createFilmCategory(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/filmcategories";
    }

    @PostMapping("/{filmId}/{categoryId}/update")
    public String update(@PathVariable Integer filmId, @PathVariable Integer categoryId,
                         @RequestParam Integer newFilmId, @RequestParam Integer newCategoryId,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("filmId", newFilmId); req.put("categoryId", newCategoryId);
        try {
            apiService.updateFilmCategory(filmId, categoryId, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/filmcategories";
    }

    @GetMapping("/film/{filmId}/categories")
    public String getCategoriesForFilm(@PathVariable Integer filmId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("filmcategories", apiService.getCategoriesByFilm(filmId));
            model.addAttribute("filmId",         filmId);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/list";
    }

    @GetMapping("/category/{categoryId}/films")
    public String getFilmsInCategory(@PathVariable Integer categoryId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("filmcategories", apiService.getFilmsByCategory(categoryId));
            model.addAttribute("categoryId",     categoryId);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/list";
    }

    @GetMapping("/category/{categoryId}/rating/{rating}")
    public String getByRating(
            @PathVariable Integer categoryId,
            @PathVariable String rating,
            Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("filmcategories", apiService.getFilmCategoriesByRating(categoryId, rating));
            model.addAttribute("categoryId",     categoryId);
            model.addAttribute("rating",         rating);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/list";
    }

    @GetMapping("/category/{categoryId}/count")
    public String countFilmsInCategory(@PathVariable Integer categoryId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("countData",  apiService.countFilmsInCategory(categoryId));
            model.addAttribute("categoryId", categoryId);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/detail";
    }

    @GetMapping("/film/{filmId}/count")
    public String countCategoriesForFilm(@PathVariable Integer filmId, Model model, RedirectAttributes ra) {
        try {
            model.addAttribute("countData", apiService.countCategoriesForFilm(filmId));
            model.addAttribute("filmId",    filmId);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found.");
            return "redirect:/filmcategories";
        }
        return "filmcategory/detail";
    }
}