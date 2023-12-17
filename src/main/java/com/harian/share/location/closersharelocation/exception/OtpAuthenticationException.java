package com.harian.share.location.closersharelocation.exception;

import org.springframework.security.core.AuthenticationException;

public class OtpAuthenticationException extends AuthenticationException{
    public OtpAuthenticationException(String message) {
        super(message);
    }
}
