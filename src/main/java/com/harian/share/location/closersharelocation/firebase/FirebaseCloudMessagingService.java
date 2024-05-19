package com.harian.share.location.closersharelocation.firebase;

import org.springframework.stereotype.Service;
import org.threeten.bp.Duration;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.AndroidNotification;
import com.google.firebase.messaging.ApnsConfig;
import com.google.firebase.messaging.Aps;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.harian.share.location.closersharelocation.firebase.model.NotificationRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseCloudMessagingService {

    public <T> void pushNotification(NotificationRequest<T> notification) throws FirebaseMessagingException {
        if (notification.getTokens().isEmpty()) {
            log.warn("missing device tokens");
            return;
        }
        MulticastMessage message = getPreConfiguredMessageToToken(notification);
        sendAndGetResponse(message);
    }

    private void sendAndGetResponse(MulticastMessage message) throws FirebaseMessagingException {
        FirebaseMessaging.getInstance().sendEachForMulticast(message);
    }

    private <T> MulticastMessage getPreConfiguredMessageToToken(NotificationRequest<T> notificationRequest) {
        AndroidConfig androidConfig = getAndroidConfig(notificationRequest);
        ApnsConfig apnsConfig = getApnsConfig(notificationRequest);
        Notification notification = Notification.builder()
                .setTitle(notificationRequest.getTitle())
                .setBody(notificationRequest.getDataJson())
                .build();
        return MulticastMessage.builder()
                .setAndroidConfig(androidConfig)
                .setApnsConfig(apnsConfig)
                .setNotification(notification)
                .addAllTokens(notificationRequest.getTokens())
                .build();
    }

    private <T> AndroidConfig getAndroidConfig(NotificationRequest<T> notification) {
        return AndroidConfig.builder()
                .setTtl(Duration.ofMinutes(2).toMillis())
                .setCollapseKey(notification.getTopic())
                .setPriority(notification.getPriority())
                .setNotification(AndroidNotification.builder().setTag(notification.getTopic()).build())
                .build();
    }

    private <T> ApnsConfig getApnsConfig(NotificationRequest<T> notification) {
        return ApnsConfig.builder()
                .setAps(Aps.builder()
                        .setCategory(notification.getTopic())
                        .setThreadId(notification.getTopic())
                        .build())
                .build();
    }
}
