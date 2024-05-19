package com.harian.share.location.closersharelocation.user.service;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.harian.share.location.closersharelocation.exception.AlreadyFriendException;
import com.harian.share.location.closersharelocation.exception.FriendRequestAlreadyExistedException;
import com.harian.share.location.closersharelocation.exception.FriendRequestNotExistedException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.firebase.FirebaseCloudMessagingService;
import com.harian.share.location.closersharelocation.firebase.model.NotificationRequest;
import com.harian.share.location.closersharelocation.user.model.Friend;
import com.harian.share.location.closersharelocation.user.model.FriendRequest;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.FriendRequestDTO;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FriendService {
    private final UserRepository userRepository;
    private final UserService userService;
    private final FirebaseCloudMessagingService firebaseService;

    /**
     * 
     * @param friend the user who got the friend request
     * @return
     * @throws UserNotFoundException
     * @throws FriendRequestAlreadyExistedException
     */
    public UserDTO createFriendRequest(User _friend, Principal connectedUser)
            throws UserNotFoundException, FriendRequestAlreadyExistedException {
        User friend = userRepository.findByEmail(_friend.getEmail())
                .orElseThrow(() -> new UserNotFoundException(
                        "user with email " + _friend.getEmail() + " not existed"));
        User user = userService.getUserFromPrincipal(connectedUser)
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + connectedUser.getName() + " not existed"));

        FriendRequest friendRequest = user.getFriendRequests().stream()
                .filter(fr -> fr.getUser().getId() == friend.getId())
                .findFirst().orElse(null);

        if (friendRequest == null) {
            friendRequest = new FriendRequest(friend, user, System.currentTimeMillis());
        } else {
            switch (friendRequest.getStatus()) {
                case PENDING:
                    throw new FriendRequestAlreadyExistedException(
                            "you have already requested this user to be friend");
                case ACCEPTED:
                    throw new FriendRequestAlreadyExistedException(
                            "you and this user have already been friend");
                default:
                    friendRequest.setStatus(FriendRequest.Status.PENDING);
                    break;
            }
        }

        user.getFriendRequests().add(friendRequest);
        user = userRepository.save(user);

        try {
            firebaseService.pushNotification(NotificationRequest.builder()
                    .title("You got a friend request from " + user.getName())
                    .data("")
                    .tokens(friend.getDevices().stream()
                            .map(device -> device.getFirebaseMessagingToken()).toList())
                    .build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return UserDTO.fromUser(user);
    }

    public UserDTO acceptFriendRequest(User _requestor, Principal connectedUser)
            throws UserNotFoundException, FriendRequestNotExistedException, AlreadyFriendException {
        User user = userService.getUserFromPrincipal(connectedUser)
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + connectedUser.getName() + " not existed"));
        User requestor = userRepository.findByEmail(_requestor.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + _requestor.getEmail() + " not existed"));
        FriendRequest friendRequest = user.getFriendRequests().stream()
                .filter(fr -> fr.getRequestor().getId() == requestor.getId()).findFirst()
                .orElseThrow(() -> new FriendRequestNotExistedException(
                        "this friend request no longer existed"));
        if (friendRequest.getStatus() == FriendRequest.Status.ACCEPTED) {
            throw new AlreadyFriendException("You and " + requestor.getName() + " are already friends");
        }
        friendRequest.setStatus(FriendRequest.Status.ACCEPTED);
        user.getFriends().add(new Friend(user, requestor, System.currentTimeMillis()));
        requestor.getFriends().add(new Friend(requestor, user, System.currentTimeMillis()));
        return UserDTO.fromUser(userRepository.save(user));
    }

    public UserDTO denyFriendRequest(User _requestor, Principal connectedUser)
            throws UserNotFoundException, FriendRequestNotExistedException {
        User user = userService.getUserFromPrincipal(connectedUser)
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + connectedUser.getName() + " not existed"));
        User requestor = userRepository.findByEmail(_requestor.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + _requestor.getEmail() + " not existed"));
        FriendRequest friendRequest = user.getFriendRequests().stream()
                .filter(fr -> fr.getRequestor().getId() == requestor.getId()).findFirst()
                .orElseThrow(() -> new FriendRequestNotExistedException(
                        "this friend request no longer existed"));
        user.getFriendRequests().remove(friendRequest);
        return UserDTO.fromUser(userRepository.save(user));
    }

    public List<FriendRequestDTO> getFriendRequest(Principal connectedUser, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser)
                .orElseThrow(
                        () -> new UserNotFoundException("user with email "
                                + connectedUser.getName() + " not existed"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        return user.getFriendRequests().stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .filter(friendRequest -> friendRequest.getStatus() == FriendRequest.Status.PENDING)
                .map(friendRequest -> FriendRequestDTO.fromFriendRequest(friendRequest))
                .toList();
    }
}
