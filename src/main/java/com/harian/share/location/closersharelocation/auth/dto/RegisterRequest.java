package com.harian.share.location.closersharelocation.auth.dto;

import com.harian.share.location.closersharelocation.user.model.Gender;
import com.harian.share.location.closersharelocation.user.model.Role;

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
    private String otp;
    private Role role;

    public void fillBlankValue() {
        name = name == null || name.isBlank() ? email.split("@")[0] : name;
        role = role == null ? Role.USER : role;
        gender = gender == null ? Gender.UNDEFINED : gender;
        avatar = avatar == null ? ("avatar/avatar" + (int) Math.ceil(Math.random() * 12) + ".png") : avatar;
    }
}
