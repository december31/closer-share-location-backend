package com.harian.share.location.closersharelocation.post.image;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.post.comment.CommentDTO;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/v1/post/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService service;

    @PostMapping("comment")
    public ImageDTO postMethodName(@RequestBody CommentDTO comment, @RequestParam(name = "image-id") Long imageId, Principal connectedUser) {
        return service.comment(comment, imageId, connectedUser);
    }
}
