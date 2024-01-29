package com.harian.share.location.closersharelocation.websocket.friend;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.user.model.FriendRequest;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.FriendRequestRepository;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;

    public FriendRequest createFriendRequest(Long friendId, Principal connectedUser) throws UserNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        user = userRepository.findById(user.getId()).orElseThrow(() -> new UserNotFoundException("sender with id '" + friendId + "' not found"));
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("receiver with id '" + friendId + "' not found"));
        FriendRequest friendRequest = FriendRequest.builder()
                .user(friend)
                .requestor(user)
                .build();
        return friendRequestRepository.save(friendRequest);
    }
}
