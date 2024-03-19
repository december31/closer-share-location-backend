package com.harian.share.location.closersharelocation.websocket.location;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Location {
    private Double latitude;
    private Double longitude;
}
