package com.harian.share.location.closersharelocation.post;

import java.security.Principal;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.user.User;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public Post create(Post post, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        post.setOwner(user);
        post.setCreatedTime(System.currentTimeMillis());
        post.setLastModified(System.currentTimeMillis());

        return postRepository.save(post);
    }
}
