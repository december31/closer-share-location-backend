package com.harian.share.location.closersharelocation.user;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    UNDEFINED("undefined");

    private final String value;

    public String getValue() {
        return value;
    }
}