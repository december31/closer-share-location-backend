package com.harian.share.location.closersharelocation.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ResetPasswordRequest {
    @JsonProperty("new_password")
    private String newPassword;
}
