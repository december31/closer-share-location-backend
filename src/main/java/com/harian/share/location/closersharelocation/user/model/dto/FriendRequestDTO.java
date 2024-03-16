package com.harian.share.location.closersharelocation.user.model.dto;

import com.harian.share.location.closersharelocation.user.model.FriendRequest;
import com.harian.share.location.closersharelocation.user.model.FriendRequest.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendRequestDTO {
    private UserDTO requestor;
    private Status status;
    private Long since;

    public static FriendRequestDTO fromFriendRequest(FriendRequest friendRequest) {
        UserDTO requestor = UserDTO.builder()
                .id(friendRequest.getRequestor().getId())
                .name(friendRequest.getRequestor().getName())
                .description(friendRequest.getRequestor().getDescription())
                .avatar(friendRequest.getRequestor().getAvatar())
                .email(friendRequest.getRequestor().getEmail())
                .gender(friendRequest.getRequestor().getGender())
                .build();

        return FriendRequestDTO.builder()
                .requestor(requestor)
                .since(friendRequest.getSince())
                .status(friendRequest.getStatus())
                .build();
    }
}
