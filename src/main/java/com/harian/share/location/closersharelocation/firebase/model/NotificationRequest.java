package com.harian.share.location.closersharelocation.firebase.model;

import java.io.Serializable;
import java.util.List;

import com.google.firebase.messaging.AndroidConfig;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRequest<T> implements Serializable {
    private String title;
    private String topic;
    private List<String> tokens;
    private AndroidConfig.Priority priority;
    private T data;

    public String toJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(this);
    }

    public String getDataJson() {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(data);
    }
}
