package com.harian.share.location.closersharelocation.websocket.friend;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import com.harian.share.location.closersharelocation.user.model.FriendRequest;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class FriendSocketHandler extends AbstractWebSocketHandler {

    private final FriendService friendService;
    private final Set<WebSocketSession> sessions;

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        sessions.remove(session);
    }
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        Long friendId = Long.parseLong(message.getPayload().toString());
        try {
            FriendRequest friendRequest = friendService.createFriendRequest(friendId, session.getPrincipal());
            WebSocketSession receiverSession = sessions.stream().filter(s -> s.getPrincipal().getName().equals(friendRequest.getUser().getEmail())).findFirst().orElse(null);
            if (receiverSession != null) {
                TextMessage textMessage = new TextMessage("you got a friend request from " + friendRequest.getRequestor().getName());
                receiverSession.sendMessage(textMessage);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
