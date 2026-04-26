package com.film.ui.controller;

import com.film.ui.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/addresses")
public class AddressController {

    @Autowired private ApiService apiService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page, Model model) {
        Map<String, Object> addressPage = apiService.getAddresses(page);
        model.addAttribute("addresses",   addressPage.get("content"));
        model.addAttribute("currentPage", (Integer) addressPage.get("number"));
        model.addAttribute("totalPages",  (Integer) addressPage.get("totalPages"));
        return "address/list";
    }

    @GetMapping("/location")
    public String getWithLocation(Model model) {
        model.addAttribute("addresses", apiService.getAddressesWithLocation());
        return "address/location";
    }

    @GetMapping("/search")
    public String search(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(required=false) String mode,
            @RequestParam(required=false) Integer id,
            @RequestParam(required=false) Integer cityId,
            @RequestParam(required=false) String district,
            @RequestParam(required=false) String postalCode,
            @RequestParam(required=false) String phone,
            Model model) {

        model.addAttribute("mode",             mode);
        model.addAttribute("searchId",         id);
        model.addAttribute("searchCityId",     cityId);
        model.addAttribute("searchDistrict",   district);
        model.addAttribute("searchPostalCode", postalCode);
        model.addAttribute("searchPhone",      phone);

        if (mode == null) return "address/search";

        Object result = null;
        try {
            switch (mode) {
                case "id" -> { if (id != null)
                    result = List.of(apiService.getAddressById(id)); }
                case "cityId" -> { if (cityId != null)
                    result = apiService.getAddressesByCityId(cityId); }
                case "district" -> { if (district != null && !district.isBlank())
                    result = apiService.getAddressesByDistrict(district); }
                case "postalCode" -> { if (postalCode != null && !postalCode.isBlank())
                    result = apiService.getAddressesByPostalCode(postalCode); }
                case "phone" -> { if (phone != null && !phone.isBlank())
                    result = apiService.getAddressesByPhone(phone); }
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
            model.addAttribute("addresses", allResults.subList(start, end));
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", totalPages);
            if (allResults.isEmpty()) model.addAttribute("errorMessage", "Record not found");
        } else {
            model.addAttribute("addresses", result);
            if (result == null) model.addAttribute("errorMessage", "Record not found");
        }

        return "address/search";
    }

    @GetMapping("/create")
    public String form() { return "address/form"; }

    @GetMapping("/{id}")
    public String getById(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> address = apiService.getAddressById(id);
            if (address == null || address.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Address #" + id + " does not exist.");
                return "redirect:/addresses";
            }
            normalise(address, id);
            model.addAttribute("address", address);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Address #" + id + " does not exist.");
            return "redirect:/addresses";
        }
        return "address/detail";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Integer id, Model model, RedirectAttributes ra) {
        try {
            Map<String, Object> address = apiService.getAddressById(id);
            if (address == null || address.isEmpty()) {
                ra.addFlashAttribute("errorMessage", "Record not found: Address #" + id + " does not exist.");
                return "redirect:/addresses";
            }
            normalise(address, id);
            model.addAttribute("address",   address);
            model.addAttribute("addressId", id);
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Record not found: Address #" + id + " does not exist.");
            return "redirect:/addresses";
        }
        return "address/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String address,
                       @RequestParam(required=false) String address2,
                       @RequestParam String district, @RequestParam Integer cityId,
                       @RequestParam(required=false) String postalCode,
                       @RequestParam String phone,
                       @RequestParam(required=false) String location,
                       RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("address", address); req.put("district", district);
        req.put("cityId", cityId); req.put("phone", phone);
        if (address2   != null && !address2.isBlank())   req.put("address2", address2);
        if (postalCode != null && !postalCode.isBlank()) req.put("postalCode", postalCode);
        if (location   != null && !location.isBlank())   req.put("location", location);
        boolean ok = apiService.createAddress(req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record added successfully." : "Failed to add record. Please check your input.");
        return "redirect:/addresses";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @RequestParam String address,
                         @RequestParam(required=false) String address2,
                         @RequestParam String district, @RequestParam Integer cityId,
                         @RequestParam(required=false) String postalCode,
                         @RequestParam String phone,
                         @RequestParam(required=false) String location,
                         RedirectAttributes ra) {
        Map<String, Object> req = new HashMap<>();
        req.put("address", address); req.put("district", district);
        req.put("cityId", cityId); req.put("phone", phone);
        if (address2   != null && !address2.isBlank())   req.put("address2", address2);
        if (postalCode != null && !postalCode.isBlank()) req.put("postalCode", postalCode);
        if (location   != null && !location.isBlank())   req.put("location", location);
        boolean ok = apiService.updateAddress(id, req);
        ra.addFlashAttribute(ok ? "successMessage" : "errorMessage",
                ok ? "Record updated successfully." : "Failed to update record. Please check your input.");
        return "redirect:/addresses";
    }

    private void normalise(Map<String, Object> address, Integer id) {
        address.put("addressId", id);
        Object loc = address.get("location");
        if (loc instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> m = (Map<String, Object>) loc;
            Object x = m.get("x"), y = m.get("y");
            address.put("location", (x != null && y != null) ? "POINT(" + x + " " + y + ")" : loc.toString());
        }
        address.putIfAbsent("address",    "");
        address.putIfAbsent("address2",   "");
        address.putIfAbsent("district",   "");
        address.putIfAbsent("cityId",     0);
        address.putIfAbsent("postalCode", "");
        address.putIfAbsent("phone",      "");
        address.putIfAbsent("location",   "");
    }
}
