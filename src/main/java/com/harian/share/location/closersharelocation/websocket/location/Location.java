package com.harian.share.location.closersharelocation.websocket.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private Long userId;
    private Long latitude;
    private Long longitude;
}
