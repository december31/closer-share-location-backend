package com.harian.share.location.closersharelocation.firebase;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FirebaseCloudMessagingInitializer {
    @PostConstruct
    public void initialize() {
        try {
            Resource resource = new ClassPathResource("closer-share-location-firebase-adminsdk-fxrnf-fa9b1da86a.json");
            InputStream serviceAccount = resource.getInputStream();
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
            log.info("Firebase cloud messaging initiated");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
