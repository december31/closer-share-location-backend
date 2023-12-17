package com.harian.share.location.closersharelocation.exception;

import org.springframework.security.core.AuthenticationException;

public class EmailAlreadyExistedException extends AuthenticationException {
    public EmailAlreadyExistedException(String message) {
        super(message);
    }
}
