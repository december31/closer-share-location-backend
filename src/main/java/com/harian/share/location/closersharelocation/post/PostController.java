package com.harian.share.location.closersharelocation.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;

import lombok.RequiredArgsConstructor;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/post")
public class PostController {

    private final PostService service;

    @PostMapping("create")
    public Object create(@RequestParam(name = "image", required = false) MultipartFile file, @RequestBody Post post,
            Principal connectedUser) {
        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message("create post successful")
                .data(new PostDTO(service.create(post, connectedUser)))
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public Object getPopular(@RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "page-size", required = false) Integer pageSize) {

        List<PostDTO> postDTOs = service.findByPage(page, pageSize).stream().map(post -> new PostDTO(post))
                .collect(Collectors.toList());

        Response<?> response = Response.builder()
                .status(HttpStatus.OK)
                .message("create post successful")
                .data(postDTOs)
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
