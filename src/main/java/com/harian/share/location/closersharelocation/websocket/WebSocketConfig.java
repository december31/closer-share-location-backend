package com.harian.share.location.closersharelocation.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.harian.share.location.closersharelocation.websocket.friend.FriendSocketHandler;
import com.harian.share.location.closersharelocation.websocket.location.LocationSocketHandler;
import com.harian.share.location.closersharelocation.websocket.messaging.MessagingSocketHandler;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocket
@CrossOrigin
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final LocationSocketHandler locationSocketHandler;
    private final MessagingSocketHandler messagingSocketHandler;
    private final FriendSocketHandler friendSocketHandler;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(locationSocketHandler, "/ws/location");
        registry.addHandler(messagingSocketHandler, "/ws/message");
        registry.addHandler(friendSocketHandler, "/ws/friend");
    }
}