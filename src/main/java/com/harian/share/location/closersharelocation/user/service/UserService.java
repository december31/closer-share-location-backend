package com.harian.share.location.closersharelocation.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.user.requests.ChangePasswordRequest;
import com.harian.share.location.closersharelocation.user.requests.ResetPasswordRequest;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;

    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        // check if the two new passwords are the same
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password are not the same");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        repository.save(user);
    }

    public UserDTO resetPassword(ResetPasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        // save the new password
        return new UserDTO(repository.save(user));
    }

    public UserDTO getUserInformation(Principal connectedUser) throws UserNotFoundException {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return UserDTO.fromUser(repository.findByEmail(user.getEmail()).orElseThrow(() -> new UserNotFoundException("User not found")));
    }
}
