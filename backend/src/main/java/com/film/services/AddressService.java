package com.film.services;

import com.film.dto.AddressDto;
import com.film.entity.Address;
import com.film.entity.City;
import com.film.entity.Country;
import com.film.exception.ResourceNotFoundException;
import com.film.repository.AddressRepository;
import com.film.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.WKTReader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


@Service
@Transactional(readOnly = true)
public class AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private CityRepository cityRepository;

    //GET ALL
    public Page<AddressDto.Response> findAllPaginated(int page, int size) {
        return addressRepository.findAll(PageRequest.of(page, size))
                .map(this::toResponse);
    }

    //GET BY ID
    public AddressDto.Response findById(Integer id) {
        return toResponse(fetchAddress(id));
    }

    //GET BY CITY ID — returns DetailedResponse with cityName + countryId + countryName
    public List<AddressDto.DetailedResponse> findByCityId(Integer cityId) {
        return addressRepository.findByCity_CityId(cityId)
                .stream()
                .map(this::toDetailedResponse)
                .collect(Collectors.toList());
    }

    //GET BY DISTRICT
    public List<AddressDto.Response> findByDistrict(String district) {
        return addressRepository.findByDistrictIgnoreCase(district)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //GET BY POSTAL CODE
    public List<AddressDto.Response> findByPostalCode(String postalCode) {
        return addressRepository.findByPostalCode(postalCode)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //GET BY PHONE
    public List<AddressDto.Response> findByPhone(String phone) {
        return addressRepository.findByPhone(phone)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //GET ALL WITH LOCATION
    public List<AddressDto.Response> findAllWithLocation() {
        return addressRepository.findAllWithLocation()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    //POST
    @Transactional
    public AddressDto.Response create(AddressDto.Request request) {
        City city = fetchCity(request.getCityId());

        Address address = Address.builder()
                .address(request.getAddress())
                .district(request.getDistrict())
                .city(city)
                .postalCode(request.getPostalCode())
                .phone(request.getPhone())
                .location(convertToPoint(request.getLocation()))
                .build();

        return toResponse(addressRepository.save(address));
    }

    //PUT
    @Transactional
    public AddressDto.Response update(Integer id, AddressDto.Request request) {
        Address address = fetchAddress(id);
        City    city    = fetchCity(request.getCityId());

        address.setAddress(request.getAddress());
        address.setDistrict(request.getDistrict());
        address.setCity(city);
        address.setPostalCode(request.getPostalCode());
        address.setPhone(request.getPhone());
        address.setLocation(convertToPoint(request.getLocation()));

        return toResponse(addressRepository.save(address));
    }

    //HELPER METHODS
    private Address fetchAddress(Integer id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address", "id", id));
    }

    private City fetchCity(Integer id) {
        return cityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("City", "id", id));
    }

    // Simple response — only cityId, no nested names
    private AddressDto.Response toResponse(Address a) {
        return AddressDto.Response.builder()
                .addressId(a.getAddressId())
                .address(a.getAddress())
                .district(a.getDistrict())
                .cityId(a.getCity() != null ? a.getCity().getCityId() : null)
                .postalCode(a.getPostalCode())
                .phone(a.getPhone())
                .location(convertToWKT(a.getLocation()))
                .lastUpdate(a.getLastUpdate())
                .build();
    }

    // Detailed response — includes cityName + countryId + countryName, used only for FK lookup
    private AddressDto.DetailedResponse toDetailedResponse(Address a) {
        City    city        = a.getCity();
        Country country     = (city != null) ? city.getCountry() : null;

        return AddressDto.DetailedResponse.builder()
                .addressId(a.getAddressId())
                .address(a.getAddress())
                .district(a.getDistrict())
                .cityId(city    != null ? city.getCityId()           : null)
                .cityName(city  != null ? city.getCity()             : null)
                .countryId(country   != null ? country.getCountryId() : null)
                .countryName(country != null ? country.getCountry()   : null)
                .postalCode(a.getPostalCode())
                .phone(a.getPhone())
                .location(convertToWKT(a.getLocation()))
                .lastUpdate(a.getLastUpdate())
                .build();
    }

    private Point convertToPoint(String wkt) {
        if (wkt == null) return null;
        try {
            return (Point) new WKTReader().read(wkt);
        } catch (Exception e) {
            throw new RuntimeException("Invalid location format. Use: POINT(lon lat)");
        }
    }

    private String convertToWKT(Point point) {
        if (point == null) return null;
        return "POINT(" + point.getX() + " " + point.getY() + ")";
    }
}