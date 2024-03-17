package com.harian.share.location.closersharelocation.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.utils.Constants;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/post")
public class PostController {

    private final PostService service;

    @PostMapping(value = "create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public Object create(HttpServletRequest request, Principal connectedUser)
            throws IOException, ServletException {
        Response<?> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("create post successful")
                    .data(service.create(request, connectedUser))
                    .build();
        } catch (IOException | ServletException | UserNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("comment")
    public Object comment(@RequestBody Comment comment, @RequestParam("id") Long postId, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(service.createComment(comment, connectedUser, postId))
                    .build();
        } catch (PostNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, null, response.getStatusCode());
    }

    @PostMapping("like")
    public ResponseEntity<?> like(@RequestParam(name = "id") Long postId, Principal connectedUser) {
        Response<Object> response;
        try {
            PostDTO postDTO = new PostDTO(service.like(postId, connectedUser));
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(postDTO)
                    .build();
        } catch (PostNotFoundException e) {
            e.printStackTrace();
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @PostMapping("watch")
    public Object watch(@RequestParam(name = "id") Long postId, Principal connectedUser) {
        Response<Object> response;
        try {
            PostDTO postDTO = new PostDTO(service.watch(postId, connectedUser));
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(postDTO)
                    .build();
        } catch (PostNotFoundException e) {
            e.printStackTrace();
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, response.getStatusCode());
    }

    @GetMapping("popular")
    public Object getPopular(@RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize) {
        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message(Constants.SUCCESSFUL)
                .data(service.findByPage(page, pageSize))
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Object getById(@RequestParam(name = "id") Long postId) {
        Response<?> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message("successful")
                    .data(service.findById(postId))
                    .build();
        } catch (PostNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, null, response.getStatusCode());
    }
}
