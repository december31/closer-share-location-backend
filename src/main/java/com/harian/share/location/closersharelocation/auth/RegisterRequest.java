package com.harian.share.location.closersharelocation.auth;

import com.harian.share.location.closersharelocation.user.Gender;
import com.harian.share.location.closersharelocation.user.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    private String name;
    private String description;
    private String avatar;
    private Gender gender;
    private String email;
    private String password;
    private Role role;

    public void fillBlankValue() {
        name = name == null || name.isBlank() ? email.split("@")[0] : name;
        role = role == null ? Role.USER : role;
        gender = gender == null ? Gender.UNDEFINED : gender;
    }
}
