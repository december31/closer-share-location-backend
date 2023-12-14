package com.harian.share.location.closersharelocation.common;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
    HttpStatusCode status;
    String message;
    T data;

    @JsonIgnore
    public HttpStatusCode getStatusCode() {
        return status;
    }

    public int getStatus() {
        return status.value();
    }
}
