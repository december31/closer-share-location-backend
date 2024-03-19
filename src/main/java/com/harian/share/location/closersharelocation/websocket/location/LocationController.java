package com.harian.share.location.closersharelocation.websocket.location;

import java.security.Principal;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @Transactional
    @MessageMapping("/location/update")
    public void updateLocation(@RequestParam Location location, Principal connectedUser) {
        locationService.updateLocation(location, connectedUser);
    }
}
