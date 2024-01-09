package com.harian.share.location.closersharelocation.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @CrossOrigin
    @GetMapping
    public ResponseEntity<?> getUserInformation(Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(service.getUserInformation(connectedUser))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser) {

        service.changePassword(request, connectedUser);
        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message("change password successful")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request,
            Principal connectedUser) {

        service.resetPassword(request, connectedUser);
        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message("reset password successful")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }
}
