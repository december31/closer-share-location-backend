package com.harian.share.location.closersharelocation.post;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/api/v1/post")
public class PostController {

    private final PostService service;

    @PostMapping("create")
    public Object create(@RequestBody Post entity, Principal connectedUser) {
        return new PostDTO(service.create(entity, connectedUser));
    }
}
