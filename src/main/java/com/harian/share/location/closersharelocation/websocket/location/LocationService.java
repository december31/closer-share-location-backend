package com.harian.share.location.closersharelocation.websocket.location;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.utils.Utils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final UserRepository userRepository;
    private final SimpUserRegistry userRegistry;
    private final Utils utils;

    public void updateLocation(Location location, Principal connectedUser) {
        User user = utils.getUserFromPrincipal(connectedUser).orElse(null);
        System.out.println("===========================");
        userRegistry.findSubscriptions(it -> true).forEach( sub -> {
            System.out.println(sub.getDestination() + " -- " + sub.getId());
        });
        
        userRegistry.getUsers().forEach(u -> {
            System.out.println(u.getName());
        });
        if (user != null) {
            user.setLatitude(location.getLatitude());
            user.setLongitude(location.getLongitude());
            userRepository.save(user);

            Set<User> friends = user.getFriends().stream()
                    .map(friend -> userRepository.findById(friend.getFriend().getId()).orElse(null))
                    .filter(friend -> friend != null)
                    .collect(Collectors.toSet());

            friends.forEach(friend -> {
                simpMessagingTemplate.convertAndSend(friend.getLocationSubscribeSocketEndPoint(),
                        UserDTO.fromUser(user));
            });
        }
    }

}
