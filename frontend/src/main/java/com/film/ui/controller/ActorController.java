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
@RequestMapping("/actors")
public class ActorController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        try {
            Map<String,Object> resp = apiService.getActorsPaged(page);
            model.addAttribute("actors",      resp.get("content"));
            model.addAttribute("currentPage", resp.get("number"));
            model.addAttribute("totalPages",  resp.get("totalPages"));
        } catch (Exception e) { model.addAttribute("error", e.getMessage()); }
        return "actor/list";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) String firstName,
            @RequestParam(required=false) String lastName,
            @RequestParam(required=false) String from,
            @RequestParam(required=false) String to,
            Model model) {

        model.addAttribute("mode",          mode);
        model.addAttribute("searchId",      id);
        model.addAttribute("searchFirstName", firstName);
        model.addAttribute("searchLastName",  lastName);
        model.addAttribute("searchFrom",    from);
        model.addAttribute("searchTo",      to);

        if (mode == null) return "actor/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getActorById(id)); }
                case "firstName" -> { if (firstName != null && !firstName.isBlank())
                    result = apiService.getActorsByFirstName(firstName); }
                case "lastName" -> { if (lastName != null && !lastName.isBlank())
                    result = apiService.getActorsByLastName(lastName); }
                case "fullName" -> { if (firstName != null && lastName != null)
                    result = List.of(apiService.getActorByFullName(firstName, lastName)); }
                case "updateRange" -> { if (from != null && to != null)
                    result = apiService.getActorsByLastUpdateRange(from, to); }
                case "updateAfter" -> { if (from != null)
                    result = apiService.getActorsUpdatedAfter(from); }
                case "updateBefore" -> { if (to != null)
                    result = apiService.getActorsUpdatedBefore(to); }
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
            model.addAttribute("actors", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("actors", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "actor/search";
    }

    @GetMapping("/create")
    public String createForm() { return "actor/form"; }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> a = apiService.getActorById(id);
            if (a == null || a.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Actor #" + id + " does not exist.");
                return "redirect:/actors";
            }
            a.put("actorId", id);
            model.addAttribute("actor", a);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Actor #" + id + " does not exist.");
            return "redirect:/actors";
        }
        return "actor/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String,Object> a = apiService.getActorById(id);
            if (a == null || a.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Actor #" + id + " does not exist.");
                return "redirect:/actors";
            }
            a.put("actorId", id);
            model.addAttribute("actor", a);
            model.addAttribute("actorId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Actor #" + id + " does not exist.");
            return "redirect:/actors";
        }
        return "actor/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String firstName, @RequestParam String lastName,
                       RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("firstName", firstName); req.put("lastName", lastName);
        try {
            apiService.createActor(req);
            ra.addFlashAttribute("successMessage", "Record added successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to add record. Please check your input.");
        }
        return "redirect:/actors";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam String firstName, @RequestParam String lastName,
                         RedirectAttributes ra) {
        Map<String,Object> req = new HashMap<>();
        req.put("firstName", firstName); req.put("lastName", lastName);
        try {
            apiService.updateActor(id, req);
            ra.addFlashAttribute("successMessage", "Record updated successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Failed to update record. Please check your input.");
        }
        return "redirect:/actors";
    }
}
