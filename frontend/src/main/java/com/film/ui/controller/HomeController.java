package com.film.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "members";
    }

    @GetMapping("/members")
    public String members() {
        return "members";
    }

    @GetMapping("/members/{name}")
    public String memberDetail(@PathVariable String name, Model model) {
        Map<String, Object> member = buildMember(name.toLowerCase());
        if (member == null) return "redirect:/members";
        model.addAttribute("member", member);
        return "member-detail";
    }

    // ─────────────────────────────────────────────────────────────────────
    //  DATA BUILDERS
    // ─────────────────────────────────────────────────────────────────────

    private Map<String, Object> buildMember(String name) {
        return switch (name) {
            case "mayank"  -> mayank();
            case "ananya"  -> ananya();
            case "adrija"  -> adrija();
            case "sneha"   -> sneha();
            case "akshay"  -> akshay();
            default -> null;
        };
    }

    private Map<String, Object> m(String name, String initial, String role,
                                  String gradient, String glow,
                                  List<Map<String, Object>> sections) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("name", name);
        m.put("initial", initial);
        m.put("role", role);
        m.put("gradient", gradient);
        m.put("glow", glow);
        m.put("sections", sections);
        return m;
    }

    private Map<String, Object> section(String entity, String path, String color, List<Map<String, Object>> apis) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("entity", entity);
        s.put("path", path);
        s.put("color", color);
        s.put("apis", apis);
        return s;
    }

    private Map<String, Object> api(String method, String path, String description, String uiPath) {
        Map<String, Object> a = new LinkedHashMap<>();
        a.put("method", method);
        a.put("path", path);
        a.put("description", description);
        a.put("uiPath", uiPath);
        return a;
    }

    private Map<String, Object> api(String method, String path, String description) {
        return api(method, path, description, null);
    }

    // ═══════════════════════════════════════════════════════════════════
    //  MAYANK — Country, City, Address
    // ═══════════════════════════════════════════════════════════════════
    private Map<String, Object> mayank() {
        List<Map<String, Object>> country = List.of(
                api("GET",    "/api/countries?page=0&size=10", "Get paginated list of all countries", "/countries"),
                api("GET",    "/api/countries/{id}",           "Get country by ID",                  "/countries/search?mode=id"),
                api("GET",    "/api/countries/name/{name}",    "Search countries by name",            "/countries/search?mode=name"),
                api("POST",   "/api/countries",                "Create a new country",                "/countries/create"),
                api("PUT",    "/api/countries/{id}",           "Update existing country by ID",       "/countries")
        );
        List<Map<String, Object>> city = List.of(
                api("GET",    "/api/cities?page=0&size=10",          "Get paginated list of all cities",       "/cities"),
                api("GET",    "/api/cities/{id}",                    "Get city by ID",                         "/cities/search?mode=id"),
                api("GET",    "/api/cities/name/{name}",             "Search cities by name",                  "/cities/search?mode=name"),
                api("GET",    "/api/cities/country/id/{countryId}",  "Get cities by country ID",               "/cities/search?mode=countryId"),
                api("POST",   "/api/cities",                         "Create a new city",                      "/cities/create"),
                api("PUT",    "/api/cities/{id}",                    "Update city by ID",                      "/cities")
        );
        List<Map<String, Object>> address = List.of(
                api("GET",    "/api/addresses?page=0&size=10",        "Get paginated list of all addresses",        "/addresses"),
                api("GET",    "/api/addresses/{id}",                  "Get address by ID",                          "/addresses/search?mode=id"),
                api("GET",    "/api/addresses/city/{cityId}",         "Get addresses by city ID",                   "/addresses/search?mode=cityId"),
                api("GET",    "/api/addresses/district/{district}",   "Get addresses by district",                  "/addresses/search?mode=district"),
                api("GET",    "/api/addresses/postal/{postalCode}",   "Get addresses by postal code",               "/addresses/search?mode=postalCode"),
                api("GET",    "/api/addresses/phone/{phone}",         "Get addresses by phone number",              "/addresses/search?mode=phone"),
                api("GET",    "/api/addresses/location",              "Get addresses that have a location set",     "/addresses/location"),
                api("POST",   "/api/addresses",                       "Create a new address",                       "/addresses/create"),
                api("PUT",    "/api/addresses/{id}",                  "Update address by ID",                       "/addresses")
        );
        return m("Mayank Raj", "MR", "Country · City · Address",
                "linear-gradient(135deg,#5b6ef5,#3b4fd4)", "rgba(91,110,245,0.4)",
                List.of(
                        section("Countries", "countries", "blue",    country),
                        section("Cities",    "cities",    "indigo",  city),
                        section("Addresses", "addresses", "purple",  address)
                ));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ANANYA — Customer, Store, Staff
    // ═══════════════════════════════════════════════════════════════════
    private Map<String, Object> ananya() {
        List<Map<String, Object>> customer = List.of(
                api("GET",    "/api/customers",               "Get all customers",                    "/customers"),
                api("GET",    "/api/customers/{id}",          "Get customer by ID",                   "/customers/search?mode=id"),
                api("GET",    "/api/customers/store/{storeId}","Get customers by store ID",           "/customers/search?mode=storeId"),
                api("POST",   "/api/customers",               "Create a new customer",                "/customers/create"),
                api("PUT",    "/api/customers/{id}",          "Update customer by ID",                "/customers")
        );
        List<Map<String, Object>> store = List.of(
                api("GET",    "/api/stores",       "Get all stores",            "/stores"),
                api("GET",    "/api/stores/{id}",  "Get store by ID",           "/stores/search?mode=id"),
                api("POST",   "/api/stores",       "Create a new store",        "/stores/create"),
                api("PUT",    "/api/stores/{id}",  "Update store by ID",        "/stores")
        );
        List<Map<String, Object>> staff = List.of(
                api("GET",    "/api/staff",                      "Get all staff members",               "/staff"),
                api("GET",    "/api/staff/{id}",                 "Get staff by ID",                     "/staff/search?mode=id"),
                api("GET",    "/api/staff/store/{storeId}",      "Get staff by store ID",               "/staff/search?mode=storeId"),
                api("GET",    "/api/staff/filter",               "Filter staff (active/inactive)",      "/staff/search?mode=active"),
                api("GET",    "/api/staff/username/{username}",  "Get staff by username",               "/staff/search?mode=username"),
                api("POST",   "/api/staff",                      "Create new staff member",             "/staff/create"),
                api("PUT",    "/api/staff/{id}",                 "Full update of staff by ID",          "/staff"),
                api("PATCH",  "/api/staff/{id}",                 "Partial update of staff by ID",       "/staff")
        );
        return m("Ananya Shree", "AS", "Customer · Store · Staff",
                "linear-gradient(135deg,#8b5cf6,#6d3fd4)", "rgba(139,92,246,0.4)",
                List.of(
                        section("Customers", "customers", "purple", customer),
                        section("Stores",    "stores",    "rose",   store),
                        section("Staff",     "staff",     "amber",  staff)
                ));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  ADRIJA — Film, Language, Category
    //  Each GET maps to /films/search?mode=<value> so only the
    //  relevant field is active on the search page.
    // ═══════════════════════════════════════════════════════════════════
    private Map<String, Object> adrija() {
        List<Map<String, Object>> film = List.of(
                // No-parameter GETs → go straight to the list / stats page
                api("GET",   "/films",                                 "Get all films",                                    "/films"),
                api("GET",   "/films/first10",                         "Get first 10 films",                               "/films/first10"),

                // Single-field search GETs → search page locked to that one field
                api("GET",   "/films/{id}",                            "Get film by ID",                                   "/films/search?mode=id"),
                api("GET",   "/films/by-title?title=",                 "Search films by title",                            "/films/search?mode=title"),
                api("GET",   "/films/by-rating?rating=",               "Get films by rating (G/PG/PG-13/R/NC-17)",         "/films/search?mode=rating"),
                api("GET",   "/films/by-release-year?releaseYear=",    "Get films by release year",                        "/films/search?mode=releaseYear"),
                api("GET",   "/films/by-language-id?languageId=",      "Get films by language ID",                         "/films/search?mode=languageId"),
                api("GET",   "/films/by-language-name",                "Get films by language name",                       "/films/search?mode=languageName"),
                api("GET",   "/films/by-rental-duration",              "Get films by rental duration",                     "/films/search?mode=rentalDuration"),
                api("GET",   "/films/by-rental-rate",                  "Get films by rental rate",                         "/films/search?mode=rentalRate"),

                // Dual-field search GETs → search page locked to those two fields
                api("GET",   "/films/by-id-and-rating?filmId=",        "Get film by ID filtered by rating",                "/films/search?mode=idRating"),
                api("GET",   "/films/by-rating-and-rate?rating=",      "Get films filtered by rating and rental rate",     "/films/search?mode=ratingRate"),
                api("GET",   "/films/by-length-and-year?length=",      "Get films filtered by length and release year",    "/films/search?mode=lengthYear"),
                api("GET",   "/films/rental-rate-range?min=",          "Get films within a rental rate range",             "/films/search?mode=rentalRange"),
                api("GET",   "/films/replacement-cost-range?min=",     "Get films within a replacement cost range",        "/films/search?mode=replaceRange"),

                // Category lookup → own page
                api("GET",   "/films/{filmId}/categories",             "Get categories for a specific film",               "/films/search?mode=id"),

                // Stats GETs → stats page
                api("GET",   "/films/count-by-category",               "Count films per category",                         "/films/stats"),
                api("GET",   "/films/count-by-rating",                 "Count films per rating",                           "/films/stats"),
                api("GET",   "/films/count-by-release-year",           "Count films per release year",                     "/films/stats"),
                api("GET",   "/films/avg-rental-rate",                 "Average rental rate across all films",             "/films/stats"),
                api("GET",   "/films/avg-length",                      "Average film length",                              "/films/stats"),
                api("GET",   "/films/max-rental-rate",                 "Maximum rental rate across all films",             "/films/stats"),
                api("GET",   "/films/min-rental-rate",                 "Minimum rental rate across all films",             "/films/stats"),
                api("GET",   "/films/max-replacement-cost",            "Maximum replacement cost across all films",        "/films/stats"),
                api("GET",   "/films/min-replacement-cost",            "Minimum replacement cost across all films",        "/films/stats"),
                api("GET",   "/films/total-replacement-cost",          "Total replacement cost of all films",              "/films/stats"),
                api("GET",   "/films/total-rental-duration",           "Total rental duration across all films",           "/films/stats"),

                // Write operations
                api("POST",  "/films",                                 "Create a new film",                                "/films/create"),
                api("PUT",   "/films/{id}",                            "Full update of film by ID",                        "/films"),
                api("PATCH", "/films/{id}",                            "Partial update of film by ID",                     "/films")
        );
        List<Map<String, Object>> language = List.of(
                api("GET",   "/api/languages",       "Get all languages",       "/languages"),
                api("GET",   "/api/languages/{id}",  "Get language by ID",      "/languages"),
                api("POST",  "/api/languages",       "Create a new language",   "/languages/create"),
                api("PUT",   "/api/languages/{id}",  "Update language by ID",   "/languages")
        );
        List<Map<String, Object>> category = List.of(
                api("GET",   "/categories",       "Get all categories",       "/categories"),
                api("GET",   "/categories/{id}",  "Get category by ID",       "/categories"),
                api("POST",  "/categories",       "Create a new category",    "/categories/create"),
                api("PUT",   "/categories/{id}",  "Update category by ID",    "/categories")
        );
        return m("Adrija Ghosh", "AG", "Film · Language · Category",
                "linear-gradient(135deg,#14b8a6,#0d8a7d)", "rgba(20,184,166,0.4)",
                List.of(
                        section("Films",      "films",      "teal",   film),
                        section("Languages",  "languages",  "blue",   language),
                        section("Categories", "categories", "indigo", category)
                ));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  SNEHA — Actor, FilmActor, FilmCategory, FilmText
    // ═══════════════════════════════════════════════════════════════════
    private Map<String, Object> sneha() {
        List<Map<String, Object>> actor = List.of(
                api("GET",   "/api/actors",                              "Get all actors",                      "/actors"),
                api("GET",   "/api/actors/{id}",                         "Get actor by ID",                     "/actors/search?mode=id"),
                api("GET",   "/api/actors/search/first-name?firstName=", "Search actors by first name",         "/actors/search?mode=firstName"),
                api("GET",   "/api/actors/search/last-name?lastName=",   "Search actors by last name",          "/actors/search?mode=lastName"),
                api("GET",   "/api/actors/search/full-name",             "Search actors by full name",          "/actors/search?mode=fullName"),
                api("GET",   "/api/actors/search/last-update/range",     "Get actors updated within date range","/actors/search?mode=updateRange"),
                api("GET",   "/api/actors/search/last-update/after",     "Get actors updated after a date",     "/actors/search?mode=updateAfter"),
                api("GET",   "/api/actors/search/last-update/before",    "Get actors updated before a date",    "/actors/search?mode=updateBefore"),
                api("POST",  "/api/actors",                              "Create a new actor",                  "/actors/create"),
                api("PUT",   "/api/actors/{id}",                         "Full update of actor by ID",          "/actors")
        );
        List<Map<String, Object>> filmActor = List.of(
                api("GET",   "/api/filmactor",                    "Get all film-actor associations",          "/filmactors"),
                api("GET",   "/api/filmactor?filmId=",            "Get actors for a film",                    "/filmactors/search?mode=filmId"),
                api("GET",   "/api/filmactor?actorId=",           "Get films for an actor",                   "/filmactors/search?mode=actorId"),
                api("GET",   "/api/filmactor/{actorId}/{filmId}", "Get a specific film-actor association",    "/filmactors/search?mode=actorFilm"),
                api("POST",  "/api/filmactor",                    "Create film-actor association",            "/filmactors/create"),
                api("PUT",   "/api/filmactor/{actorId}/{filmId}", "Update film-actor association",            "/filmactors")
        );
        List<Map<String, Object>> filmCategory = List.of(
                api("GET",   "/api/film-categories",                              "Get all film-category associations",          "/filmcategories"),
                api("GET",   "/api/film-categories?filmId=",                      "Get categories for a film",                   "/filmcategories/search?mode=filmId"),
                api("GET",   "/api/film-categories?categoryId=",                  "Get films in a category",                     "/filmcategories/search?mode=categoryId"),
                api("GET",   "/api/film-categories/{filmId}/{categoryId}",        "Get a specific film-category record",         "/filmcategories/search?mode=filmCategoryId"),
                api("GET",   "/api/film-categories/category/{categoryId}/films",  "Get all films under a category",              "/filmcategories/search?mode=categoryFilms"),
                api("GET",   "/api/film-categories/film/{filmId}/categories",     "Get all categories of a film",                "/filmcategories/search?mode=filmCategories"),
                api("GET",   "/api/film-categories/category/{categoryId}/rating/{rating}", "Get films in a category by rating", "/filmcategories/search?mode=categoryRating"),
                api("GET",   "/api/film-categories/category/{categoryId}/count",  "Count films in a category",                   "/filmcategories"),
                api("GET",   "/api/film-categories/film/{filmId}/count",          "Count categories assigned to a film",         "/filmcategories"),
                api("POST",  "/api/film-categories",                              "Create film-category association",            "/filmcategories/create"),
                api("PUT",   "/api/film-categories/{filmId}/{categoryId}",        "Update film-category association",            "/filmcategories")
        );
        List<Map<String, Object>> filmText = List.of(
                api("GET",   "/api/filmtexts",                               "Get all film texts",                  "/filmtexts"),
                api("GET",   "/api/filmtexts/{filmId}",                      "Get film text by film ID",            "/filmtexts/search?mode=id"),
                api("GET",   "/api/filmtexts/search/title?title=",           "Search film texts by title",          "/filmtexts/search?mode=title"),
                api("GET",   "/api/filmtexts/search/description?description=","Search film texts by description",  "/filmtexts/search?mode=description"),
                api("GET",   "/api/filmtexts/search/keyword?keyword=",       "Search film texts by keyword",        "/filmtexts/search?mode=keyword"),
                api("POST",  "/api/filmtexts",                               "Create a new film text entry",        "/filmtexts/create"),
                api("PUT",   "/api/filmtexts/{filmId}",                      "Update film text by film ID",         "/filmtexts")
        );
        return m("Sneha Singh", "SS", "Actor · Film-Actor · Film-Category · Film-Text",
                "linear-gradient(135deg,#f43f5e,#c22042)", "rgba(244,63,94,0.4)",
                List.of(
                        section("Actors",         "actors",         "rose",   actor),
                        section("Film-Actor",     "filmactors",     "purple", filmActor),
                        section("Film-Category",  "filmcategories", "indigo", filmCategory),
                        section("Film-Text",      "filmtexts",      "teal",   filmText)
                ));
    }

    // ═══════════════════════════════════════════════════════════════════
    //  AKSHAY — Inventory, Rental, Payment
    // ═══════════════════════════════════════════════════════════════════
    private Map<String, Object> akshay() {
        List<Map<String, Object>> inventory = List.of(
                api("GET",   "/api/inventory",                    "Get all inventory records",                  "/inventory"),
                api("GET",   "/api/inventory/{id}",               "Get inventory item by ID",                   "/inventory/search?mode=id"),
                api("GET",   "/api/inventory/film/{filmId}",      "Get inventory by film ID",                   "/inventory/search?mode=filmId"),
                api("GET",   "/api/inventory/store/{storeId}",    "Get inventory by store ID",                  "/inventory/search?mode=storeId"),
                api("GET",   "/api/inventory/inventory_page",     "Get paginated inventory records",            "/inventory"),
                api("GET",   "/api/inventory/last-update",        "Get inventory items by last update date",    "/inventory"),
                api("GET",   "/api/inventory/inventory-store",    "Get inventory with store details joined",    "/inventory"),
                api("GET",   "/api/inventory/inventory-film",     "Get inventory with film details joined",     "/inventory"),
                api("POST",  "/api/inventory",                    "Add a new inventory item",                   "/inventory/create"),
                api("PUT",   "/api/inventory/{id}",               "Update inventory item by ID",                "/inventory")
        );
        List<Map<String, Object>> rental = List.of(
                api("GET",   "/api/rental/getAllRental",          "Get all rentals",                        "/rentals"),
                api("GET",   "/api/rental/paged",                 "Get paginated rentals",                  "/rentals"),
                api("GET",   "/api/rental/{id}",                  "Get rental by ID",                       "/rentals/search?mode=id"),
                api("GET",   "/api/rental/customer/{id}",         "Get rentals by customer ID",             "/rentals/search?mode=customerId"),
                api("GET",   "/api/rental/inventory/{id}",        "Get rentals by inventory ID",            "/rentals/search?mode=inventoryId"),
                api("GET",   "/api/rental/staff/{id}",            "Get rentals by staff ID",                "/rentals/search?mode=staffId"),
                api("GET",   "/api/rental/date-range",            "Get rentals in rental date range",       "/rentals/search?mode=dateRange"),
                api("GET",   "/api/rental/return-date-range",     "Get rentals in return date range",       "/rentals/search?mode=returnDateRange"),
                api("GET",   "/api/rental/last-update-range",     "Get rentals by last-update date range",  "/rentals/search?mode=lastUpdateRange"),
                api("GET",   "/api/rental/rental-inventory",      "Get rentals with inventory details",     "/rentals"),
                api("GET",   "/api/rental/rental-customer",       "Get rentals with customer details",      "/rentals"),
                api("GET",   "/api/rental/rental-staff",          "Get rentals with staff details",         "/rentals"),
                api("POST",  "/api/rental",                       "Create a new rental",                    "/rentals/create"),
                api("PUT",   "/api/rental/{id}",                  "Update rental by ID",                    "/rentals")
        );
        List<Map<String, Object>> payment = List.of(
                api("GET",   "/api/payment",                          "Get all payments",                  "/payments"),
                api("GET",   "/api/payment/{id}",                     "Get payment by ID",                 "/payments/search?mode=id"),
                api("GET",   "/api/payment/customer/{customerId}",    "Get payments by customer ID",       "/payments/search?mode=customerId"),
                api("GET",   "/api/payment/staff/{staffId}",          "Get payments by staff ID",          "/payments/search?mode=staffId"),
                api("GET",   "/api/payment/rental/{rentalId}",        "Get payments by rental ID",         "/payments/search?mode=rentalId"),
                api("GET",   "/api/payment/date",                     "Get payments by date",              "/payments/search?mode=date"),
                api("GET",   "/api/payment/payment-staff",            "Get payments with staff details",   "/payments/search?mode=staffView"),
                api("GET",   "/api/payment/payment-customer",         "Get payments with customer details","/payments/search?mode=customerView"),
                api("GET",   "/api/payment/payment-rental",           "Get payments with rental details",  "/payments/search?mode=rentalView"),
                api("POST",  "/api/payment",                          "Create a new payment",              "/payments/create"),
                api("PUT",   "/api/payment/{id}",                     "Update payment by ID",              "/payments")
        );
        return m("Akshay Raj Saxena", "ARS", "Inventory · Rental · Payment",
                "linear-gradient(135deg,#f59e0b,#c47d08)", "rgba(245,158,11,0.4)",
                List.of(
                        section("Inventory", "inventory", "amber", inventory),
                        section("Rentals",   "rentals",   "rose",  rental),
                        section("Payments",  "payments",  "teal",  payment)
                ));
    }
}