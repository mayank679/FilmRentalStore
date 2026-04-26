package com.film.services;

import com.film.dto.CityDto;
import com.film.entity.City;
import com.film.entity.Country;
import com.film.exception.DuplicateResourceException;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.CityRepository;
import com.film.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private CountryRepository countryRepository;

    //GET ALL
    public Page<CityDto.Response> findAllPaginated(int page, int size) {
        return cityRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    //GET BY CITY ID
    public CityDto.Response findById(Integer id) {
        return toResponse(fetchCity(id));
    }

    //GET BY CITY NAME
    public List<CityDto.Response> getCitiesByName(String cityName) {
        List<City> cities = cityRepository.findByCityContainingIgnoreCase(cityName);
        if (cities.isEmpty()) {
            throw new ResourceNotFoundException("City", "name", cityName);
        }
        return cities.stream()
                .map(this::toResponse)
                .toList();
    }

    //GET BY COUNTRY ID — returns DetailedResponse with countryName included
    public List<CityDto.DetailedResponse> findByCountryId(Integer countryId) {
        return cityRepository.findByCountry_CountryId(countryId)
                .stream()
                .map(this::toDetailedResponse)
                .toList();
    }

    //POST
    @Transactional
    public CityDto.Response create(CityDto.Request request) {
        Country country = fetchCountry(request.getCountryId());

        if (cityRepository.existsByCityIgnoreCaseAndCountry_CountryId(
                request.getCity(), request.getCountryId())) {
            throw new DuplicateResourceException(
                    "City '" + request.getCity() + "' already exists in this country");
        }

        City city = new City();
        city.setCity(request.getCity());
        city.setCountry(country);

        return toResponse(cityRepository.save(city));
    }

    //PUT
    @Transactional
    public CityDto.Response update(Integer id, CityDto.Request request) {
        City    city    = fetchCity(id);
        Country country = fetchCountry(request.getCountryId());

        cityRepository.findByCityIgnoreCase(request.getCity())
                .ifPresent(existing -> {
                    boolean sameCountry     = existing.getCountry().getCountryId().equals(request.getCountryId());
                    boolean differentRecord = !existing.getCityId().equals(id);
                    if (sameCountry && differentRecord) {
                        throw new DuplicateResourceException(
                                "City '" + request.getCity() + "' already exists in this country");
                    }
                });

        city.setCity(request.getCity());
        city.setCountry(country);
        return toResponse(cityRepository.save(city));
    }

    //HELPER METHODS
    private City fetchCity(Integer id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    private Country fetchCountry(Integer id) {
        return countryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Country", "id", id));
    }

    // Simple response — no nested country name
    private CityDto.Response toResponse(City c) {
        CityDto.Response response = new CityDto.Response();
        response.setCityId(c.getCityId());
        response.setCity(c.getCity());
        if (c.getCountry() != null) {
            response.setCountryId(c.getCountry().getCountryId());
        }
        response.setLastUpdate(c.getLastUpdate());
        return response;
    }

    // Detailed response — includes countryName, used only for FK lookup
    private CityDto.DetailedResponse toDetailedResponse(City c) {
        CityDto.DetailedResponse response = new CityDto.DetailedResponse();
        response.setCityId(c.getCityId());
        response.setCity(c.getCity());
        Country country = c.getCountry();
        if (country != null) {
            response.setCountryId(country.getCountryId());
            response.setCountryName(country.getCountry());
        }
        response.setLastUpdate(c.getLastUpdate());
        return response;
    }
}