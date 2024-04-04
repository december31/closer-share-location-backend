package com.harian.share.location.closersharelocation.user.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harian.share.location.closersharelocation.user.model.Device;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO {
    private String id;

    private String model;
    private String manufacturer;
    private String brand;
    private String type;

    @JsonProperty("version-code-base")
    private Integer versionCodeBase;

    private String incremental;
    private Integer sdk;
    private String board;
    private String host;
    private String fingerprint;

    @JsonProperty("version-code")
    private String versionCode;

    @JsonProperty("firebase-messaging-token")
    private String firebaseMessagingToken;

    private UserDTO user;

    public static DeviceDTO fromDevice(Device device) {
        return DeviceDTO.builder()
                .id(device.getId())
                .model(device.getModel())
                .manufacturer(device.getManufacturer())
                .brand(device.getBrand())
                .type(device.getType())
                .versionCodeBase(device.getVersionCodeBase())
                .incremental(device.getIncremental())
                .sdk(device.getSdk())
                .board(device.getBoard())
                .host(device.getHost())
                .fingerprint(device.getFingerprint())
                .versionCode(device.getVersionCode())
                .firebaseMessagingToken(device.getFirebaseMessagingToken())
                .build();
    }
}
