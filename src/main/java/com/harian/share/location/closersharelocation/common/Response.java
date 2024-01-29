package com.harian.share.location.closersharelocation.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.lang.NonNull;

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
    private HttpStatusCode status;
    private String message;
    private T data;

    @NonNull
    @JsonIgnore
    public HttpStatusCode getStatusCode() {
        if (status != null) {
            return status;
        } else {
            return HttpStatus.OK;
        }
    }

    public int getStatus() {
        return status.value();
    }
}
