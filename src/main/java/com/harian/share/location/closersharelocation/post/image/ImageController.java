package com.harian.share.location.closersharelocation.post.image;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.harian.share.location.closersharelocation.common.Response;
import com.harian.share.location.closersharelocation.exception.ImageNotFoundException;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.utils.Constants;

import lombok.RequiredArgsConstructor;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/post/image")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService service;

    @PostMapping("comment")
    public Object comment(@RequestBody Comment comment, @RequestParam(name = "id") Long imageId,
            Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(service.comment(comment, imageId, connectedUser))
                    .build();
        } catch (ImageNotFoundException e) {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, null, response.getStatusCode());
    }

    @PostMapping("like")
    public ResponseEntity<?> like(@RequestParam(name = "id") Long imageId, Principal connectedUser) {
        Response<Object> response;
        try {
            response = Response.builder()
                    .status(HttpStatus.OK)
                    .message(Constants.SUCCESSFUL)
                    .data(service.like(imageId, connectedUser))
                    .build();
        } catch (ImageNotFoundException e) {
            e.printStackTrace();
            response = Response.builder()
                    .status(HttpStatus.NOT_FOUND)
                    .message(e.getMessage())
                    .data(null)
                    .build();
        }
        return new ResponseEntity<>(response, response.getStatusCode());
    }
}
