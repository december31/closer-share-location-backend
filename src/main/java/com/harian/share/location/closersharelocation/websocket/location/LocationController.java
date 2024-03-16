package com.harian.share.location.closersharelocation.websocket.location;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/location/update")
    @SendTo("/topic/location/subscribe")
    public String updateLocation(@RequestParam String location) {
        System.out.println("got location" + location);
        return location;
    }
}
