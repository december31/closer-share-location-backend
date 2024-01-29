package com.harian.share.location.closersharelocation.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestOtpRequest {
    private String email;
    private String name;
}
