package com.harian.share.location.closersharelocation.messaging.model;

import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private UserDTO receiver;
    private String message;
    private Long time;
    private Long code;
    private Message.Status status;
    private Type type;

    public static MessageDTO fromMessage(Message message) {
        return MessageDTO.builder()
                .receiver(UserDTO.fromUser(message.getReceiver()))
                .message(message.getMessage())
                .time(message.getTime())
                .status(message.getStatus())
                .code(message.getCode())
                .build();
    }

    public enum Type {
        SEND,
        RECEIVE
    }
}
