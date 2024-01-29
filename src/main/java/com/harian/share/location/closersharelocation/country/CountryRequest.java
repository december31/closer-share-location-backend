package com.harian.share.location.closersharelocation.country;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryRequest {
    private String iso2;
    private String iso3;
    private String country;
    private List<String> cities;
}
