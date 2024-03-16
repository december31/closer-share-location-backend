package com.harian.share.location.closersharelocation.common;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ResponseCode {
    FRIEND_REQUEST_PENDING(600),
    FRIEND_REQUEST_ACCEPTED(409);

    private final Integer code;
}
