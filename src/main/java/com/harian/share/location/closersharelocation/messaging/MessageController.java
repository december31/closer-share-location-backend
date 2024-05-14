package com.harian.share.location.closersharelocation.messaging;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.messaging.model.MessageDTO;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/message")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService service;

    @PostMapping("send")
    public ResponseEntity<?> sendMessage(@RequestBody MessageDTO messageDTO, Principal connectedUser) {
        Response.ResponseBuilder<MessageDTO> response = Response.builder();
        try {
            response.status(HttpStatus.OK)
                    .message("successful")
                    .data(service.sendMessage(messageDTO, connectedUser));
        } catch (UserNotFoundException e) {
            response.status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null);
        }
        return new ResponseEntity<Response<?>>(response.build(), null, response.build().getStatusCode());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getMessage(@PathVariable(name = "id") Long userId, Principal connectedUser) {
        Response.ResponseBuilder<List<MessageDTO>> response = Response.builder();
        try {
            response.status(HttpStatus.OK)
                    .message("successful")
                    .data(service.getMessages(userId, connectedUser));
        } catch (UserNotFoundException e) {
            response.status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null);
        }
        return new ResponseEntity<Response<?>>(response.build(), null, response.build().getStatusCode());
    }
}
