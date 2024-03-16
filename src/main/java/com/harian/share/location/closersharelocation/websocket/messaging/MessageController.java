package com.harian.share.location.closersharelocation.websocket.messaging;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MessageController {

    @MessageMapping("/greeting")
    public String String(@RequestParam String greeting) {
        String text = "[" + System.currentTimeMillis() + "]:" + greeting;
        System.out.println("got a socket message: " + greeting);
        return text;
    }

}
