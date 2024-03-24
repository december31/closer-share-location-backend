package com.harian.share.location.closersharelocation.user;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.FriendRequestAlreadyExistedException;
import com.harian.share.location.closersharelocation.exception.FriendRequestNotExistedException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.requests.ChangePasswordRequest;
import com.harian.share.location.closersharelocation.user.requests.ResetPasswordRequest;
import com.harian.share.location.closersharelocation.user.service.FriendService;
import com.harian.share.location.closersharelocation.user.service.UserService;

import java.security.Principal;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendService friendService;

    @GetMapping
    public ResponseEntity<?> getUserInformation(Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getUserInformation(connectedUser))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getUserInformation(@PathVariable(name = "id") Long userId, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getUserInformation(userId, connectedUser))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @GetMapping("{id}/friends")
    public ResponseEntity<?> getUserFriends(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize,
            @PathVariable(name = "id") Long userId) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getFriends(userId, page, pageSize))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }
    
    @GetMapping("{id}/posts")
    public ResponseEntity<?> getUserPosts(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize,
            @PathVariable(name = "id") Long userId) {
                Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getPost(userId, page, pageSize))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @GetMapping("friends")
    public ResponseEntity<?> getFriends(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize,
            Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getFriends(connectedUser, page, pageSize))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @GetMapping("posts")
    public ResponseEntity<?> getPosts(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize,
            Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(userService.getPost(connectedUser, page, pageSize))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @PostMapping("friend/request")
    public ResponseEntity<?> createFriendRequest(@RequestBody User friend, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(friendService.createFriendRequest(friend, connectedUser))
                    .build();
        } catch (UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        } catch (FriendRequestAlreadyExistedException e) {
            response = Response.builder()
                    .status(HttpStatus.CONFLICT)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @PostMapping("friend/accept")
    public ResponseEntity<?> acceptFriendRequest(@RequestBody User requestor, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(friendService.acceptFriendRequest(requestor, connectedUser))
                    .build();
        } catch (UserNotFoundException | FriendRequestNotExistedException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<Response<?>>(response, null, response.getStatusCode());
    }

    @PostMapping("friend/deny")
    public ResponseEntity<?> denyFriendRequest(@RequestBody User requestor, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(friendService.denyFriendRequest(requestor, connectedUser))
                    .build();
        } catch (UserNotFoundException | FriendRequestNotExistedException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
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

        userService.changePassword(request, connectedUser);
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

        userService.resetPassword(request, connectedUser);
        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message("reset password successful")
                .data(null)
                .build();
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> acceptFriendRequest() {
        return null;
    }

    @PostMapping("friend/requests")
    public ResponseEntity<?> getFriendRequest(Principal connectedUser, @RequestParam(name = "id") Long id) {
        return null;
    }
}
