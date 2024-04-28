package com.harian.share.location.closersharelocation.user.model.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendsResponse {
    private List<FriendDTO> friends;
    private int count;
}
