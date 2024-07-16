package com.harian.share.location.closersharelocation.firebase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationData {
    private Type type;
    private String title;
    private String data;

    public enum Type {
        FRIEND_REQUEST,
        MESSAGE,
        POST
    }
}
