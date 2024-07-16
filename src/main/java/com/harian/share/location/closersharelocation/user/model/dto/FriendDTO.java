package com.harian.share.location.closersharelocation.user.model.dto;

import com.harian.share.location.closersharelocation.user.model.Friend;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendDTO {
    private UserDTO information;
    private Long since;

    public static FriendDTO fromFriend(Friend friendRequest) {
        UserDTO friend = UserDTO.fromUser(friendRequest.getFriend());

        return FriendDTO.builder()
                .information(friend)
                .since(friendRequest.getSince())
                .build();
    }
}
