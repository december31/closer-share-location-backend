package com.harian.share.location.closersharelocation.utils;

import java.security.Principal;
import java.util.Optional;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public final class Utils {

    private final UserRepository userRepository;

    public Optional<User> getUserFromPrincipal(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return userRepository.findByEmail(user.getEmail());
    }
}
