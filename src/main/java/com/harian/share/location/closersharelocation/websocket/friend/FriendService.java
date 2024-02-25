package com.harian.share.location.closersharelocation.websocket.friend;

import java.security.Principal;
import java.util.Set;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.FriendRequestNotExisted;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.user.model.Friend;
import com.harian.share.location.closersharelocation.user.model.FriendRequest;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.FriendRepository;
import com.harian.share.location.closersharelocation.user.repository.FriendRequestRepository;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final FriendRepository friendRepository;

    public FriendRequest createFriendRequest(Long friendId, Principal connectedUser) throws UserNotFoundException {
        User requestor = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        requestor = userRepository.findById(requestor.getId())
                .orElseThrow(() -> new UserNotFoundException("sender with id '" + friendId + "' not found"));
        if (requestor.getId() == friendId) {
            return null;
        }
        User friend = userRepository.findById(friendId)
                .orElseThrow(() -> new UserNotFoundException("receiver with id '" + friendId + "' not found"));
        FriendRequest friendRequest = friendRequestRepository.findByUserIdAndRequestorId(friendId, requestor.getId())
                .orElse(null);
        if (friendRequest == null) {
            friendRequest = FriendRequest.builder()
                    .user(friend)
                    .requestor(requestor)
                    .since(System.currentTimeMillis())
                    .build();
        }
        return friendRequestRepository.save(friendRequest);
    }

    public Set<FriendRequest> cancelFriendRequest(Long userId, Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        friendRequestRepository.deleteByUserId(userId);
        return friendRequestRepository.findByUserId(user.getId());
    }

    public void acceptFriendRequest(Long requestorId, Principal connectedUser) throws FriendRequestNotExisted, UserNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        User requestor = userRepository.findById(requestorId)
                .orElseThrow(() -> new UserNotFoundException("requestor not existed"));
        FriendRequest friendRequest = friendRequestRepository.findByUserIdAndRequestorId(user.getId(), requestorId)
                .orElseThrow(() -> new FriendRequestNotExisted("this friend request no longer existed"));
        Friend friend = friendRepository.findByUserIdAndFriendId(user.getId(), requestorId).orElse(
                Friend.builder()
                        .friend(requestor)
                        .user(user)
                        .since(System.currentTimeMillis())
                        .build());
        friendRepository.save(friend);
        friendRequestRepository.delete(friendRequest);
    }
}
