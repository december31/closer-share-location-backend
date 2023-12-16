package com.harian.share.location.closersharelocation.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harian.share.location.closersharelocation.user.Gender;
import com.harian.share.location.closersharelocation.user.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  private Integer id;
  private String name;
  private String description;
  private String avatar;
  private Gender gender;
  private String email;
  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;

  public static class AuthenticationResponseBuilder {
    public AuthenticationResponseBuilder user(User user) {
      this.id = user.getId();
      this.name = user.getName();
      this.description = user.getDescription();
      this.avatar = user.getAvatar();
      this.gender = user.getGender();
      this.email = user.getEmail();
      return this;
    }
  }
}
