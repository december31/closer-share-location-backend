package com.harian.share.location.closersharelocation.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harian.share.location.closersharelocation.user.model.Gender;
import com.harian.share.location.closersharelocation.user.model.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String name;
    private String description;
    private String avatar;
    private String email;
    private Gender gender;
    private Double latitude;
    private Double longitude;

    @JsonProperty("is-friend")
    private Boolean isFriend;

    public UserDTO(User user, Boolean isFriend) {
        this.id = user.getId();
        this.name = user.getName();
        this.description = user.getDescription();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.gender = user.getGender();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
        this.isFriend = isFriend;
    }

    public static UserDTO fromUser(User user, Boolean isFriend) {
        return new UserDTO(user, isFriend);
    }

    public static UserDTO fromUser(User user) {
        return new UserDTO(user, null);
    }
}
