package com.harian.share.location.closersharelocation.user.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Device {
    @Id
    private String id;

    private String model;
    private String manufacturer;
    private String brand;
    private String type;
    private String incremental;
    private Integer sdk;
    private String board;
    private String host;
    private String fingerprint;

    @JsonProperty("version-code-base")
    private Integer versionCodeBase;

    @JsonProperty("version-code")
    private String versionCode;

    @JsonProperty("firebase-messaging-token")
    private String firebaseMessagingToken;

    @ManyToOne
    private User user;
}
