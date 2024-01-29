package com.harian.share.location.closersharelocation.post.image;

import java.security.Principal;

import org.springframework.stereotype.Service;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.harian.share.location.closersharelocation.exception.ImageNotFoundException;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.post.comment.CommentRepository;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ImageService {
    private final ImageRepository imageRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public ImageDTO comment(Comment comment, Long imageId, Principal connectedUser) throws ImageNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("post with id '" + imageId + "' not found"));
        comment.setOwner(user);
        comment.setCreatedTime(System.currentTimeMillis());
        comment.setImage(image);
        commentRepository.save(comment);
        image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("post with id '" + imageId + "' not found"));
        return new ImageDTO(image);
    }

    public ImageDTO like(Long imageId, Principal connectedUser) throws ImageNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Image image = imageRepository.findById(imageId)
                .orElseThrow(() -> new ImageNotFoundException("image with id '" + imageId + "' not found"));
        image.getLikes().add(user);
        user.getLikedImages().add(image);
        userRepository.save(user);
        return new ImageDTO(imageRepository.save(image));
    }
}
