package com.harian.share.location.closersharelocation.user;

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

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.description = user.getDescription();
        this.avatar = user.getAvatar();
        this.email = user.getEmail();
        this.gender = user.getGender();
    }
}
