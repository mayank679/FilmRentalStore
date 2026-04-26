package com.film.ui.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@Service
public class ApiService {

	@Autowired
	private RestTemplate restTemplate;

	private final String BASE = "http://localhost:8082/api";

	// ── helpers ──────────────────────────────────────────────────────────────
	private HttpHeaders jsonHeaders() {
		HttpHeaders h = new HttpHeaders();
		h.setContentType(MediaType.APPLICATION_JSON);
		return h;
	}

	private <T> T get(String url, ParameterizedTypeReference<T> ref) {
		try {
			ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.GET, null, ref);
			return resp.getStatusCode().is2xxSuccessful() ? resp.getBody() : null;
		} catch (org.springframework.web.client.RestClientException e) {
			return null;
		}
	}

	private <T> T put(String url, Object body, ParameterizedTypeReference<T> ref) {
		ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.PUT,
				new HttpEntity<>(body, jsonHeaders()), ref);
		return resp.getStatusCode().is2xxSuccessful() ? resp.getBody() : null;
	}

	private <T> T post(String url, Object body, Class<T> clazz) {
		ResponseEntity<T> resp = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(body, jsonHeaders()), clazz);
		return resp.getStatusCode().is2xxSuccessful() ? resp.getBody() : null;
	}

	/** Returns true when backend accepted the request (2xx). */
	private boolean postOk(String url, Object body) {
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST,
				new HttpEntity<>(body, jsonHeaders()), String.class);
		return resp.getStatusCode().is2xxSuccessful();
	}

	/** Returns true when backend accepted the request (2xx). */
	private boolean putOk(String url, Object body) {
		ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.PUT,
				new HttpEntity<>(body, jsonHeaders()), String.class);
		return resp.getStatusCode().is2xxSuccessful();
	}

	// ═══════════════════════════════════════════════════════════
	// COUNTRY /api/countries
	// ═══════════════════════════════════════════════════════════
	public Map<String, Object> getCountries(int page) {
		return get(BASE + "/countries?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public Map<String, Object> getCountryById(Integer id) {
		return get(BASE + "/countries/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getCountriesByName(String name) {
		return get(BASE + "/countries/name/" + name, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public boolean createCountry(Map<String, Object> req) {
		return postOk(BASE + "/countries", req);
	}

	public boolean updateCountry(Integer id, Map<String, Object> req) {
		return putOk(BASE + "/countries/" + id, req);
	}

	// ═══════════════════════════════════════════════════════════
	// CITY /api/cities
	// ═══════════════════════════════════════════════════════════
	public Map<String, Object> getCities(int page) {
		return get(BASE + "/cities?page=" + page + "&size=10", new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public Map<String, Object> getCityById(Integer id) {
		return get(BASE + "/cities/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getCitiesByName(String name) {
		return get(BASE + "/cities/name/" + name, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getCitiesByCountryId(Integer countryId) {
		return get(BASE + "/cities/country/id/" + countryId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public boolean createCity(Map<String, Object> req) {
		return postOk(BASE + "/cities", req);
	}

	public boolean updateCity(Integer id, Map<String, Object> req) {
		return putOk(BASE + "/cities/" + id, req);
	}

	// ═══════════════════════════════════════════════════════════
	// ADDRESS /api/addresses
	// ═══════════════════════════════════════════════════════════
	public Map<String, Object> getAddresses(int page) {
		return get(BASE + "/addresses?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public Map<String, Object> getAddressById(Integer id) {
		return get(BASE + "/addresses/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getAddressesByCityId(Integer cityId) {
		return get(BASE + "/addresses/city/" + cityId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getAddressesByDistrict(String district) {
		return get(BASE + "/addresses/district/" + district,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getAddressesByPostalCode(String postalCode) {
		return get(BASE + "/addresses/postal/" + postalCode,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getAddressesByPhone(String phone) {
		return get(BASE + "/addresses/phone/" + phone, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getAddressesWithLocation() {
		return get(BASE + "/addresses/location", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public boolean createAddress(Map<String, Object> req) {
		return postOk(BASE + "/addresses", req);
	}

	public boolean updateAddress(Integer id, Map<String, Object> req) {
		return putOk(BASE + "/addresses/" + id, req);
	}

	// ═══════════════════════════════════════════════════════════
	// ACTOR /api/actors
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllActors() {
		return get(BASE + "/actors", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getActorById(Integer id) {
		return get(BASE + "/actors/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getActorsByFirstName(String firstName) {
		return get(BASE + "/actors/search/first-name?firstName=" + firstName,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getActorsByLastName(String lastName) {
		return get(BASE + "/actors/search/last-name?lastName=" + lastName,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public void createActor(Map<String, Object> req) {
		post(BASE + "/actors", req, Object.class);
	}

	public void updateActor(Integer id, Map<String, Object> req) {
		put(BASE + "/actors/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// FILM /films 
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllFilms() {
		return get(BASE + "/films", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getFilmById(Integer id) {
		return get(BASE + "/films/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getFilmsByTitle(String title) {
		return get(BASE + "/films/by-title?title=" + title,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByRating(String rating) {
		return get(BASE + "/films/by-rating?rating=" + rating,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByReleaseYear(int year) {
		return get(BASE + "/films/by-release-year?releaseYear=" + year,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByLanguageId(Integer langId) {
		return get(BASE + "/films/by-language-id?languageId=" + langId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByLanguageName(String name) {
		return get(BASE + "/films/by-language-name?name=" + name,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByRentalDuration(int duration) {
		return get(BASE + "/films/by-rental-duration?rentalDuration=" + duration,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByRentalRate(String rate) {
		return get(BASE + "/films/by-rental-rate?rentalRate=" + rate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public Map<String, Object> getFilmWithCategories(Integer filmId) {
		return get(BASE + "/films/" + filmId + "/categories",
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public Object getFilmCountByCategory() {
		return get(BASE + "/films/count-by-category", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmCountByRating() {
		return get(BASE + "/films/count-by-rating", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmCountByReleaseYear() {
		return get(BASE + "/films/count-by-release-year", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmAvgRentalRate() {
		return get(BASE + "/films/avg-rental-rate", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmAvgLength() {
		return get(BASE + "/films/avg-length", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmTotalReplacementCost() {
		return get(BASE + "/films/total-replacement-cost", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmTotalRentalDuration() {
		return get(BASE + "/films/total-rental-duration", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmMaxRentalRate() {
		return get(BASE + "/films/max-rental-rate", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmMinRentalRate() {
		return get(BASE + "/films/min-rental-rate", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmMaxReplacementCost() {
		return get(BASE + "/films/max-replacement-cost", new ParameterizedTypeReference<Object>() {
		});
	}

	public Object getFilmMinReplacementCost() {
		return get(BASE + "/films/min-replacement-cost", new ParameterizedTypeReference<Object>() {
		});
	}

	/*public void createFilm(Map<String, Object> req) {
		post(BASE + "/films", req, Object.class);
	}

	public void updateFilm(Integer id, Map<String, Object> req) {
		put(BASE + "/films/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}*/

	public void createFilm(Map<String, Object> req) {
		post(BASE + "/films", req, Object.class);
	}

	public void updateFilm(Integer id, Map<String, Object> req) {
		put(BASE + "/films/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	/** PATCH /films/{id}  — only sends the fields you supply */
	public Map<String, Object> patchFilm(Integer id, Map<String, Object> req) {
		return patch(BASE + "/films/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {});
	}


	// ═══════════════════════════════════════════════════════════
	// CATEGORY /categories 
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllCategories() {
		return get(BASE + "/categories", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getCategoryById(Integer id) {
		return get(BASE + "/categories/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public void createCategory(Map<String, Object> req) {
		post(BASE + "/categories", req, Object.class);
	}

	public void updateCategory(Integer id, Map<String, Object> req) {
		put(BASE + "/categories/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// LANGUAGE /api/languages
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllLanguages() {
		return get(BASE + "/languages", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getLanguageById(Integer id) {
		return get(BASE + "/languages/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public void createLanguage(Map<String, Object> req) {
		post(BASE + "/languages", req, Object.class);
	}

	public void updateLanguage(Integer id, Map<String, Object> req) {
		put(BASE + "/languages/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// FILM TEXT /api/filmtexts
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllFilmTexts() {
		return get(BASE + "/filmtexts", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getFilmTextById(Integer filmId) {
		return get(BASE + "/filmtexts/" + filmId, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> searchFilmTextsByTitle(String title) {
		return get(BASE + "/filmtexts/search/title?title=" + title,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> searchFilmTextsByKeyword(String keyword) {
		return get(BASE + "/filmtexts/search/keyword?keyword=" + keyword,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	// ═══════════════════════════════════════════════════════════
	// FILM-ACTOR /api/filmactor
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllFilmActors() {
		return get(BASE + "/filmactor", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getFilmActorsByFilm(Integer filmId) {
		return get(BASE + "/filmactor?filmId=" + filmId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getFilmActorsByActor(Integer actorId) {
		return get(BASE + "/filmactor?actorId=" + actorId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getFilmActorById(Integer actorId, Integer filmId) {
		return get(BASE + "/filmactor/" + actorId + "/" + filmId,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public void createFilmActor(Map<String, Object> req) {
		post(BASE + "/filmactor", req, Object.class);
	}

	public void updateFilmActor(Integer actorId, Integer filmId, Map<String, Object> req) {
		put(BASE + "/filmactor/" + actorId + "/" + filmId, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// FILM-CATEGORY /api/film-categories
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllFilmCategories() {
		return get(BASE + "/film-categories", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getFilmCategoriesByFilm(Integer filmId) {
		return get(BASE + "/film-categories?filmId=" + filmId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getFilmCategoriesByCategory(Integer categoryId) {
		return get(BASE + "/film-categories?categoryId=" + categoryId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public Map<String, Object> getFilmCategoryById(Integer filmId, Integer categoryId) {
		return get(BASE + "/film-categories/" + filmId + "/" + categoryId,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public List<Map<String, Object>> getFilmsByCategoryAndRating(Integer categoryId, String rating) {
		return get(BASE + "/film-categories/category/" + categoryId + "/rating/" + rating,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public void createFilmCategory(Map<String, Object> req) {
		post(BASE + "/film-categories", req, Object.class);
	}

	public void updateFilmCategory(Integer filmId, Integer categoryId, Map<String, Object> req) {
		put(BASE + "/film-categories/" + filmId + "/" + categoryId, req,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	// ═══════════════════════════════════════════════════════════
	// INVENTORY /api/inventory
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllInventory() {
		return get(BASE + "/inventory", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getInventoryById(Integer id) {
		return get(BASE + "/inventory/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getInventoryByFilm(Integer filmId) {
		return get(BASE + "/inventory/film/" + filmId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getInventoryByStore(Integer storeId) {
		return get(BASE + "/inventory/store/" + storeId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public void createInventory(Map<String, Object> req) {
		post(BASE + "/inventory", req, Object.class);
	}

	public void updateInventory(Integer id, Map<String, Object> req) {
		put(BASE + "/inventory/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}


	public List<Map<String, Object>> getInventoryByLastUpdate(String lastUpdate) {
		return get(BASE + "/inventory/last-update?lastUpdate=" + lastUpdate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getInventoryWithStore() {
		return get(BASE + "/inventory/inventory-store", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// RENTAL /api/rental
	// FIX: date-range/return-date-range now use startDate/endDate matching backend
	// FIX: rental-inventory/customer/staff now require rentalId + related ID
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllRentals() {
		return get(BASE + "/rental/getAllRental", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getRentalById(Integer id) {
		return get(BASE + "/rental/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getRentalsByCustomer(Integer customerId) {
		return get(BASE + "/rental/customer/" + customerId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getRentalsByStaff(Integer staffId) {
		return get(BASE + "/rental/staff/" + staffId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getRentalsByInventory(Integer inventoryId) {
		return get(BASE + "/rental/inventory/" + inventoryId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	
	public List<Map<String, Object>> getRentalsByDateRange(String startDate, String endDate) {
		return get(BASE + "/rental/date-range?startDate=" + startDate + "&endDate=" + endDate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	
	public List<Map<String, Object>> getRentalsByReturnDateRange(String startDate, String endDate) {
		return get(BASE + "/rental/return-date-range?startDate=" + startDate + "&endDate=" + endDate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	
	public Map<String, Object> getRentalWithInventory(Integer rentalId, Integer inventoryId) {
		return get(BASE + "/rental/rental-inventory?rentalId=" + rentalId + "&inventoryId=" + inventoryId,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	
	public Map<String, Object> getRentalWithCustomer(Integer rentalId, Integer customerId) {
		return get(BASE + "/rental/rental-customer?rentalId=" + rentalId + "&customerId=" + customerId,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	
	public Map<String, Object> getRentalWithStaff(Integer rentalId, Integer staffId) {
		return get(BASE + "/rental/rental-staff?rentalId=" + rentalId + "&staffId=" + staffId,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	public void createRental(Map<String, Object> req) {
		post(BASE + "/rental", req, Object.class);
	}

	public void updateRental(Integer id, Map<String, Object> req) {
		put(BASE + "/rental/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// PAYMENT /api/payment
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllPayments() {
		return get(BASE + "/payment", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getPaymentById(Integer id) {
		return get(BASE + "/payment/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsByCustomer(Integer customerId) {
		return get(BASE + "/payment/customer/" + customerId,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public List<Map<String, Object>> getPaymentsByStaff(Integer staffId) {
		return get(BASE + "/payment/staff/" + staffId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsByRental(Integer rentalId) {
		return get(BASE + "/payment/rental/" + rentalId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsByDate(String date) {
		return get(BASE + "/payment/date?date=" + date, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsWithStaff() {
		return get(BASE + "/payment/payment-staff", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsWithCustomer() {
		return get(BASE + "/payment/payment-customer", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public List<Map<String, Object>> getPaymentsWithRental() {
		return get(BASE + "/payment/payment-rental", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public void createPayment(Map<String, Object> req) {
		post(BASE + "/payment", req, Object.class);
	}

	public void updatePayment(Integer id, Map<String, Object> req) {
		put(BASE + "/payment/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// STAFF /api/staff
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllStaff() {
		return get(BASE + "/staff", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getStaffById(Integer id) {
		return get(BASE + "/staff/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getStaffByStore(Integer storeId) {
		return get(BASE + "/staff/store/" + storeId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public void createStaff(Map<String, Object> req) {
		post(BASE + "/staff", req, Object.class);
	}

	public void updateStaff(Integer id, Map<String, Object> req) {
		put(BASE + "/staff/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getStaffByFilter(Boolean active) {
		return get(BASE + "/staff/filter?active=" + active,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public Map<String, Object> getStaffByUsername(String username) {
		return get(BASE + "/staff/username/" + username, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// STORE /api/stores
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllStores() {
		return get(BASE + "/stores", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getStoreById(Integer id) {
		return get(BASE + "/stores/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public void createStore(Map<String, Object> req) {
		post(BASE + "/stores", req, Object.class);
	}

	public void updateStore(Integer id, Map<String, Object> req) {
		put(BASE + "/stores/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// CUSTOMER /api/customers
	// ═══════════════════════════════════════════════════════════
	public List<Map<String, Object>> getAllCustomers() {
		return get(BASE + "/customers", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public Map<String, Object> getCustomerById(Integer id) {
		return get(BASE + "/customers/" + id, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	public List<Map<String, Object>> getCustomersByStore(Integer storeId) {
		return get(BASE + "/customers/store/" + storeId, new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	public void createCustomer(Map<String, Object> req) {
		post(BASE + "/customers", req, Object.class);
	}

	public void updateCustomer(Integer id, Map<String, Object> req) {
		put(BASE + "/customers/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}


	// ── PATCH HELPER  (add after the put() helper) ──────────────
	private <T> T patch(String url, Object body, ParameterizedTypeReference<T> ref) {
		return restTemplate.exchange(url, HttpMethod.PATCH, new HttpEntity<>(body), ref).getBody();
	}

	// ═══════════════════════════════════════════════════════════
	//  ACTOR  — missing endpoints
	//  Backend base: /api/actors
	// ═══════════════════════════════════════════════════════════

	/** GET /api/actors/search/full-name?firstName=&lastName= */
	public Map<String, Object> getActorByFullName(String firstName, String lastName) {
		return get(BASE + "/actors/search/full-name?firstName=" + firstName + "&lastName=" + lastName,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	/** GET /api/actors/search/last-update/range?from=&to= */
	public List<Map<String, Object>> getActorsByLastUpdateRange(String from, String to) {
		return get(BASE + "/actors/search/last-update/range?from=" + from + "&to=" + to,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/actors/search/last-update/after?from= */
	public List<Map<String, Object>> getActorsUpdatedAfter(String from) {
		return get(BASE + "/actors/search/last-update/after?from=" + from,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/actors/search/last-update/before?to= */
	public List<Map<String, Object>> getActorsUpdatedBefore(String to) {
		return get(BASE + "/actors/search/last-update/before?to=" + to,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	// ═══════════════════════════════════════════════════════════
	//  FILM  — missing endpoints
	//  Backend base: /films  (BASE)
	// ═══════════════════════════════════════════════════════════

	/** GET /films/first10 */
	public List<Map<String, Object>> getFirst10Films() {
		return get(BASE + "/films/first10", new ParameterizedTypeReference<List<Map<String, Object>>>() {
		});
	}

	/** GET /films/by-id-and-rating?filmId=&rating= */
	public Map<String, Object> getFilmByIdAndRating(Integer filmId, String rating) {
		return get(BASE + "/films/by-id-and-rating?filmId=" + filmId + "&rating=" + rating,
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	/** GET /films/by-rating-and-rate?rating=&rentalRate= */
	public List<Map<String, Object>> getFilmsByRatingAndRate(String rating, String rentalRate) {
		return get(BASE + "/films/by-rating-and-rate?rating=" + rating + "&rentalRate=" + rentalRate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /films/by-length-and-year?length=&releaseYear= */
	public List<Map<String, Object>> getFilmsByLengthAndYear(Integer length, Integer releaseYear) {
		return get(BASE + "/films/by-length-and-year?length=" + length + "&releaseYear=" + releaseYear,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /films/rental-rate-range?min=&max= */
	public List<Map<String, Object>> getFilmsByRentalRateRange(String min, String max) {
		return get(BASE + "/films/rental-rate-range?min=" + min + "&max=" + max,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /films/replacement-cost-range?min=&max= */
	public List<Map<String, Object>> getFilmsByReplacementCostRange(String min, String max) {
		return get(BASE + "/films/replacement-cost-range?min=" + min + "&max=" + max,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	// ═══════════════════════════════════════════════════════════
	//  FILM CATEGORY  — missing endpoints
	//  Backend base: /api/film-categories
	// ═══════════════════════════════════════════════════════════

	/** GET /api/film-categories/film/{filmId}/categories */
	public List<Map<String, Object>> getCategoriesByFilm(Integer filmId) {
		return get(BASE + "/film-categories/film/" + filmId + "/categories",
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/film-categories/category/{categoryId}/films */
	public List<Map<String, Object>> getFilmsByCategory(Integer categoryId) {
		return get(BASE + "/film-categories/category/" + categoryId + "/films",
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/film-categories/category/{categoryId}/rating/{rating} */
	public List<Map<String, Object>> getFilmCategoriesByRating(Integer categoryId, String rating) {
		return get(BASE + "/film-categories/category/" + categoryId + "/rating/" + rating,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/film-categories/category/{categoryId}/count */
	public Map<String, Object> countFilmsInCategory(Integer categoryId) {
		return get(BASE + "/film-categories/category/" + categoryId + "/count",
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}

	/** GET /api/film-categories/film/{filmId}/count */
	public Map<String, Object> countCategoriesForFilm(Integer filmId) {
		return get(BASE + "/film-categories/film/" + filmId + "/count",
				new ParameterizedTypeReference<Map<String, Object>>() {
				});
	}
	
	// ═══════════════════════════════════════════════════════════
	//  FILM TEXT  — missing endpoint
	//  Backend base: /api/filmtexts
	// ═══════════════════════════════════════════════════════════

	/** GET /api/filmtexts/search/description?description= */
	public List<Map<String, Object>> searchFilmTextsByDescription(String description) {
		return get(BASE + "/filmtexts/search/description?description=" + description,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	public void createFilmText(Map<String, Object> req) {
		post(BASE + "/filmtexts", req, Object.class);
	}

	public void updateFilmText(Integer filmId, Map<String, Object> req) {
		put(BASE + "/filmtexts/" + filmId, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	//  RENTAL  — missing endpoints
	//  Backend base: /api/rental
	// ═══════════════════════════════════════════════════════════

	/** GET /api/rental/paged?page=&size= */
	public List<Map<String, Object>> getPagedRentals(int page, int size) {
		return get(BASE + "/rental/paged?page=" + page + "&size=" + size,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	/** GET /api/rental/last-update-range?startDate=&endDate= */
	public List<Map<String, Object>> getRentalsByLastUpdateRange(String startDate, String endDate) {
		return get(BASE + "/rental/last-update-range?startDate=" + startDate + "&endDate=" + endDate,
				new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
	}

	// ═══════════════════════════════════════════════════════════
	//  STAFF  — missing endpoint
	//  Backend base: /api/staff
	// ═══════════════════════════════════════════════════════════

	/** PATCH /api/staff/{id} */
	public Map<String, Object> patchStaff(Integer id, Map<String, Object> req) {
		return patch(BASE + "/staff/" + id, req, new ParameterizedTypeReference<Map<String, Object>>() {
		});
	}

	// ═══════════════════════════════════════════════════════════
	// PAGINATION ADDITIONS — new paged endpoints for all entities
	// ═══════════════════════════════════════════════════════════

	// ACTOR paged
	public Map<String, Object> getActorsPaged(int page) {
		return get(BASE + "/actors/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// CATEGORY paged
	public Map<String, Object> getCategoriesPaged(int page) {
		return get(BASE + "/categories/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// CUSTOMER paged
	public Map<String, Object> getCustomersPaged(int page) {
		return get(BASE + "/customers/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// FILM paged
	public Map<String, Object> getFilmsPaged(int page) {
		return get(BASE + "/films/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// LANGUAGE paged
	public Map<String, Object> getLanguagesPaged(int page) {
		return get(BASE + "/languages/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// PAYMENT paged
	public Map<String, Object> getPaymentsPaged(int page) {
		return get(BASE + "/payment/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// STAFF paged
	public Map<String, Object> getStaffPaged(int page) {
		return get(BASE + "/staff/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// STORE paged
	public Map<String, Object> getStoresPaged(int page) {
		return get(BASE + "/stores/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// INVENTORY paged 
	public Map<String, Object> getInventoryPaged(int page) {
		return get(BASE + "/inventory/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// FILMTEXT paged
	public Map<String, Object> getFilmTextsPaged(int page) {
		return get(BASE + "/filmtexts/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// FILMACTOR paged
	public Map<String, Object> getFilmActorsPaged(int page) {
		return get(BASE + "/filmactor/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// FILMCATEGORY paged
	public Map<String, Object> getFilmCategoriesPaged(int page) {
		return get(BASE + "/film-categories/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

	// RENTAL paged (proper Page response with totalPages metadata)
	public Map<String, Object> getRentalsPaged(int page) {
		return get(BASE + "/rental/paged?page=" + page + "&size=10",
				new ParameterizedTypeReference<Map<String, Object>>() {});
	}

}
