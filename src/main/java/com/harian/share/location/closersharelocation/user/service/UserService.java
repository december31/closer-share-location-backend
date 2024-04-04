package com.harian.share.location.closersharelocation.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.post.PostDTO;
import com.harian.share.location.closersharelocation.post.PostRepository;
import com.harian.share.location.closersharelocation.user.model.Device;
import com.harian.share.location.closersharelocation.user.model.Friend;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.DeviceDTO;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;
import com.harian.share.location.closersharelocation.user.repository.DeviceRepository;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.user.requests.ChangePasswordRequest;
import com.harian.share.location.closersharelocation.user.requests.ResetPasswordRequest;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final PostRepository postRepository;
    private final DeviceRepository deviceRepository;

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
        return UserDTO.fromUser(repository.save(user));
    }

    public UserDTO getUserInformation(Principal connectedUser) throws UserNotFoundException {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return UserDTO.fromUser(
                repository.findByEmail(user.getEmail())
                        .orElseThrow(() -> new UserNotFoundException("User not found")));
    }

    public UserDTO getUserInformation(Long userId, Principal connectedUser) throws UserNotFoundException {
        User currentUser = getUserFromPrincipal(connectedUser)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Friend friend = currentUser.getFriends().stream().filter(f -> f.getFriend().getId() == user.getId())
                .findFirst()
                .orElse(null);

        UserDTO userDto = UserDTO.fromUser(user, user.getId() == currentUser.getId() ? null : friend != null);

        return userDto;
    }

    public List<UserDTO> getFriends(Principal connectedUser, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        return user.getFriends().stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .map(friend -> UserDTO.fromUser(friend.getFriend()))
                .collect(Collectors.toList());
    }

    public List<UserDTO> getFriends(Long userId, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        return user.getFriends().stream()
                .skip(page * pageSize)
                .limit(pageSize)
                .map(friend -> UserDTO.fromUser(friend.getFriend()))
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPost(Principal connectedUser, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository
                .findByOwner(user, PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent().stream()
                .map(post -> PostDTO.fromPost(post))
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPost(Long userId, Integer page, Integer pageSize) throws UserNotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository
                .findByOwner(user, PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent().stream()
                .map(post -> PostDTO.fromPost(post))
                .collect(Collectors.toList());
    }

    public DeviceDTO updateDevice(Device _device, Principal connectedUser) throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser).orElseThrow(() -> new UserNotFoundException("user not found"));
        Device device = user.getDevices().stream().filter(d -> d.getId().equals(_device.getId())).findFirst()
                .orElse(null);
        if (device == null) {
            device = _device;
            user.getDevices().add(device);
            device.setUser(user);
            user = repository.save(user);
        } else {
            device.setModel(_device.getModel());
            device.setManufacturer(_device.getManufacturer());
            device.setBrand(_device.getBrand());
            device.setType(_device.getType());
            device.setVersionCodeBase(_device.getVersionCodeBase());
            device.setIncremental(_device.getIncremental());
            device.setSdk(_device.getSdk());
            device.setBoard(_device.getBoard());
            device.setHost(_device.getHost());
            device.setFingerprint(_device.getFingerprint());
            device.setVersionCode(_device.getVersionCode());
            device.setFirebaseMessagingToken(_device.getFirebaseMessagingToken());
        }
        device = deviceRepository.save(device);
        return DeviceDTO.fromDevice(device);
    }

    public List<DeviceDTO> getDevices(Principal connectedUser) throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser).orElseThrow(() -> new UserNotFoundException("user not found"));
        return user.getDevices().stream().map(device -> DeviceDTO.fromDevice(device)).collect(Collectors.toList());
    }

    public Optional<User> getUserFromPrincipal(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return repository.findByEmail(user.getEmail());
    }
}
