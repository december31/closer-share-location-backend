package com.harian.share.location.closersharelocation.country;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.country.city.City;
import com.harian.share.location.closersharelocation.exception.CountryNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CountryService {

    private final CountryRepository repository;

    public Country getCountry(String countryCode) throws CountryNotFoundException {
        if (countryCode == null) {
            throw new CountryNotFoundException("Country with code is null");
        }
        String upperCaseCountryCode = countryCode.toUpperCase();
        if (upperCaseCountryCode == null) {
            throw new CountryNotFoundException("Country with code is null");
        }
        return repository.findById(upperCaseCountryCode)
                .orElseThrow(() -> new CountryNotFoundException("Country with code " + countryCode + " not found"));
    }

    public List<Country> getCountries() {
        return repository.findAll();
    }

    public List<Country> init(List<CountryRequest> countriesRequest) {
        List<Country> countries = countriesRequest.stream().map(countryRequest -> {
            Country country = Country.builder()
                    .iso2(countryRequest.getIso2())
                    .iso3(countryRequest.getIso3())
                    .country(countryRequest.getCountry())
                    .build();
            country.setCities(countryRequest.getCities().stream().map(city -> City.builder()
                    .name(city)
                    .country(country)
                    .build())
                    .collect(Collectors.toList()));
            return country;
        }).collect(Collectors.toList());

        return repository.saveAll(countries);
    }
}
