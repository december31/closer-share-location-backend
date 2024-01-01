package com.harian.share.location.closersharelocation.post.image;

import java.security.Principal;

import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.post.comment.CommentDTO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository repository;

    public ImageDTO comment(CommentDTO comment, Long imageId, Principal connectedUser) {
        return null;
    }

    public ImageDTO like(Long imageId, Principal connectedUser) {
        return null;
    }
}
