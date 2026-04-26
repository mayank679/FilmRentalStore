package com.film.services;

import com.film.dto.CountryDto; 
import com.film.entity.Country;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CountryService {

    @Autowired
    private CountryRepository countryRepository;

    // GET
    public Page<CountryDto.Response> findAllPaginated(int page, int size) {
        return countryRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    // GET BY ID
    public CountryDto.Response findById(Integer id) {
        return toResponse(fetchCountry(id)); 
    }

    // GET BY COUNTRY NAME
    public List<CountryDto.Response> getCountriesByName(String name) {

        List<Country> countries = countryRepository.findByCountryContainingIgnoreCase(name);

        if (countries.isEmpty()) {
            throw new ResourceNotFoundException("Country", "name", name);
        }

        return countries.stream()
                .map(this::toResponse)
                .toList();
    }
    
    
    

    // POST
    @Transactional
    public CountryDto.Response create(CountryDto.Request request) {

        if (countryRepository.existsByCountryIgnoreCase(request.getCountry())) {
            throw new DuplicateResourceException(
                    "Country '" + request.getCountry() + "' already exists");
        }

        Country country = new Country();
        country.setCountry(request.getCountry());

        return toResponse(countryRepository.save(country));
    }

    
    // PUT
    @Transactional
    public CountryDto.Response update(Integer id, CountryDto.Request request) {

        Country country = fetchCountry(id);

        countryRepository.findByCountryIgnoreCase(request.getCountry())
                .ifPresent(existing -> {
                    boolean differentRecord = !existing.getCountryId().equals(id);
                    if (differentRecord) {
                        throw new DuplicateResourceException(
                                "Country '" + request.getCountry() + "' already exists");
                    }
                });

        country.setCountry(request.getCountry());

        return toResponse(countryRepository.save(country));
    }

    
    
    
    // HELPERS METHOD 
    private Country fetchCountry(Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
    }

    private CountryDto.Response toResponse(Country c) {
        CountryDto.Response response = new CountryDto.Response();

        response.setCountryId(c.getCountryId());
        response.setCountry(c.getCountry());
        response.setLastUpdate(c.getLastUpdate());

        return response;
    }
}