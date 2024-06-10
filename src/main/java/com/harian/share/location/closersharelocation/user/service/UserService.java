package com.harian.share.location.closersharelocation.user.service;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.PasswordIncorrectException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.post.PostDTO;
import com.harian.share.location.closersharelocation.post.PostRepository;
import com.harian.share.location.closersharelocation.user.model.Device;
import com.harian.share.location.closersharelocation.user.model.Friend;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.DeviceDTO;
import com.harian.share.location.closersharelocation.user.model.dto.FriendDTO;
import com.harian.share.location.closersharelocation.user.model.dto.FriendsResponse;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;
import com.harian.share.location.closersharelocation.user.repository.DeviceRepository;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.user.requests.ChangePasswordRequest;
import com.harian.share.location.closersharelocation.user.requests.ResetPasswordRequest;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final PostRepository postRepository;
    private final DeviceRepository deviceRepository;

    public UserDTO changePassword(ChangePasswordRequest request, Principal connectedUser)
            throws PasswordIncorrectException {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        // check if the current password is correct
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new PasswordIncorrectException("Wrong password");
        }

        // update the password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        // save the new password
        return UserDTO.fromUser(repository.save(user));
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

    public List<User> searchUsers(String query, Integer page, Integer pageSize, Principal connectedUser)
            throws UserNotFoundException {
        // User user = getUserFromPrincipal(connectedUser);
        List<User> users = new ArrayList<>();
        users.addAll(repository.findByNameContaining(query));
        users.addAll(repository.findByEmailContaining(query));
        users.addAll(repository.findByAddressContaining(query));
        users.addAll(repository.findByPhoneNumberContaining(query));
        users = users.stream().distinct().limit(20).collect(Collectors.toList());
        return users;
    }

    public UserDTO getUserInformation(Long userId, Principal connectedUser) throws UserNotFoundException {
        User currentUser = getUserFromPrincipal(connectedUser);
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found"));

        Friend friend = currentUser.getFriends().stream().filter(f -> f.getFriend().getId() == user.getId())
                .findFirst()
                .orElse(null);

        UserDTO userDto = UserDTO.fromUser(user, user.getId() == currentUser.getId() ? null : friend != null);

        return userDto;
    }

    public FriendsResponse getFriends(Principal connectedUser, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser);
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        FriendsResponse friendsResponse = FriendsResponse.builder()
                .count(user.getFriends().size())
                .friends(user.getFriends().stream()
                        .skip(page * pageSize)
                        .limit(pageSize)
                        .map(friend -> FriendDTO.fromFriend(friend))
                        .collect(Collectors.toList()))
                .build();
        return friendsResponse;
    }

    public FriendsResponse getFriends(Long userId, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 6 : pageSize;
        FriendsResponse friendsResponse = FriendsResponse.builder()
                .count(user.getFriends().size())
                .friends(user.getFriends().stream()
                        .skip(page * pageSize)
                        .limit(pageSize)
                        .map(friend -> FriendDTO.fromFriend(friend))
                        .collect(Collectors.toList()))
                .build();
        return friendsResponse;
    }

    public List<PostDTO> getPost(Principal connectedUser, Integer page, Integer pageSize)
            throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser);
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository
                .findByOwner(user, PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent().stream()
                .map(post -> PostDTO.fromPost(post))
                .collect(Collectors.toList());
    }

    public List<PostDTO> getPost(Long userId, Integer page, Integer pageSize) throws UserNotFoundException {
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException("user not found"));
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository
                .findByOwner(user, PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent().stream()
                .map(post -> PostDTO.fromPost(post))
                .collect(Collectors.toList());
    }

    public DeviceDTO updateDevice(Device _device, Principal connectedUser) throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser);
        Device device = user.getDevices().stream().filter(d -> d.getId().equals(_device.getId())).findFirst()
                .orElse(null);
        if (device == null) {
            device = _device;
            if (!user.getDevices().stream().filter(d -> d.getId() == _device.getId()).toList().isEmpty()) {
                user.getDevices().removeIf(d -> d.getId() == _device.getId());
            }
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
        User user = getUserFromPrincipal(connectedUser);
        return user.getDevices().stream().map(device -> DeviceDTO.fromDevice(device)).collect(Collectors.toList());
    }

    public UserDTO updateAvatar(HttpServletRequest request, Principal connectedUser)
            throws UserNotFoundException, IOException, ServletException {
        User user = getUserFromPrincipal(connectedUser);
        String path = saveAvatarImage(request, user);
        user.setAvatar(path);
        user = repository.save(user);
        return UserDTO.fromUser(user);
    }

    private String saveAvatarImage(HttpServletRequest request, User user) throws IOException, ServletException {
        Part imagePart = request.getPart("image");
        String absoluteFolderPath = "E:/CloserShareLocation/avatar/" + user.getId();
        String relativeFolderPath = "avatar/" + user.getId();
        File folder = new File(absoluteFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        String path = absoluteFolderPath + "/" + imagePart.getSubmittedFileName();
        String dbPath = relativeFolderPath + "/" + imagePart.getSubmittedFileName();
        imagePart.write(path);
        return dbPath;
    }

    public UserDTO updateInformation(UserDTO userDTO, Principal connectedUser) throws UserNotFoundException {
        User user = getUserFromPrincipal(connectedUser);
        user.setAddress(userDTO.getAddress());
        user.setLastModified(System.currentTimeMillis());
        user.setName(userDTO.getName());
        user.setDescription(userDTO.getDescription());
        user.setEmail(userDTO.getEmail());
        user.setGender(userDTO.getGender());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        user = repository.save(user);
        return UserDTO.fromUser(user);
    }

    public User getUserFromPrincipal(Principal connectedUser) throws UserNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        return repository.findByEmail(user.getEmail())
                .orElseThrow(() -> new UserNotFoundException("connected user not found"));
    }
}
