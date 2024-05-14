package com.harian.share.location.closersharelocation.firebase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationData<T> {
    private Type type;
    private T data;

    public enum Type {
        FRIEND_REQUEST,
        MESSAGE
    }
}
