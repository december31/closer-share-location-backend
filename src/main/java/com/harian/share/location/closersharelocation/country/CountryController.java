package com.harian.share.location.closersharelocation.country;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.CountryNotFoundException;
import com.harian.share.location.closersharelocation.utils.Constants;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1/country")
@RequiredArgsConstructor
public class CountryController {

    private final CountryService service;

    @GetMapping
    public Object getCountry(@RequestParam(name = "country-code") String countryCode) {
        Response<?> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(service.getCountry(countryCode))
                    .build();
        } catch (CountryNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("init")
    public Object init() {
        return service.init();
    }
}
