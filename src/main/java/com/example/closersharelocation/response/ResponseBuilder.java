package com.example.closersharelocation.response;

import org.springframework.http.HttpStatus;

import com.example.closersharelocation.base.BaseResponse;

public class ResponseBuilder {
    private BaseResponse<Object> response;

    public ResponseBuilder() {
        response = new BaseResponse<Object>();
    }

    public ResponseBuilder withStatusCode(HttpStatus statusCode) {
        response.setStatus(statusCode.value());
        return this;
    }

    public ResponseBuilder withMessage(String message) {
        response.setMessage(message);
        return this;
    }

    public ResponseBuilder withData(Object data) {
        response.setData(data);
        return this;
    }

    public BaseResponse<Object> build() {
        return response;
    }
}
