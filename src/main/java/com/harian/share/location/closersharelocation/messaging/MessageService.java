package com.harian.share.location.closersharelocation.messaging;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.gson.Gson;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.firebase.FirebaseCloudMessagingService;
import com.harian.share.location.closersharelocation.firebase.model.NotificationData;
import com.harian.share.location.closersharelocation.firebase.model.NotificationRequest;
import com.harian.share.location.closersharelocation.messaging.model.Message;
import com.harian.share.location.closersharelocation.messaging.model.MessageDTO;
import com.harian.share.location.closersharelocation.messaging.repository.MessageRepository;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.user.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FirebaseCloudMessagingService firebaseService;

    public MessageDTO sendMessage(MessageDTO messageDTO, Principal connectedUser) throws UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Message message = Message.builder()
                .message(messageDTO.getMessage())
                .sender(user)
                .time(System.currentTimeMillis())
                .code(messageDTO.getCode())
                .status(Message.Status.SENT)
                .receiver(userRepository.findById(messageDTO.getReceiver().getId())
                        .orElseThrow(() -> new UserNotFoundException("receiver not found")))
                .build();
        message = messageRepository.save(message);
        MessageDTO response = MessageDTO.fromMessage(message);
        response.setType(MessageDTO.Type.RECEIVE);
        messagingTemplate.convertAndSend(message.getReceiver().getMessageSubscribeSocketEndPoint(), response);
        System.out.println("send message to socket: " + message.getReceiver().getMessageSubscribeSocketEndPoint());
        // TODO: manage message status

        try {
            firebaseService.pushNotification(NotificationRequest.builder()
                    .title(user.getName())
                    .data(NotificationData.builder()
                            .type(NotificationData.Type.POST)
                            .title(response.getMessage())
                            .data(new Gson().toJson(response))
                            .build())
                    .priority(AndroidConfig.Priority.HIGH)
                    .tokens(message.getReceiver().getDevices().stream()
                            .map(device -> device.getFirebaseMessagingToken()).toList())
                    .build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }

        return response;
    }

    public List<MessageDTO> getMessages(Long userID, Principal connectedUser) throws UserNotFoundException {
        User sender = userService.getUserFromPrincipal(connectedUser);
        User receiver = userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException("receiver not found"));
        List<Message> messages = messageRepository.findAllMessage(sender.getId(), receiver.getId());
        // TODO: pagination
        return messages.stream().map(message -> {
            MessageDTO messageDTO = MessageDTO.fromMessage(message);
            messageDTO.setType(
                    message.getReceiver().getId() == sender.getId() ? MessageDTO.Type.RECEIVE : MessageDTO.Type.SEND);
            return messageDTO;
        }).collect(Collectors.toList());
    }
}
