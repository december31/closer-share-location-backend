package com.harian.share.location.closersharelocation.auth;

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
  private String gender;
  private String email;
  private String password;
  private Role role;

  public void fillBlankValue() {
    name = name.isBlank() || name == null ? email.split("@")[0].split(".")[0] : name;
    role = role == null ? Role.USER : role;
  }
}
