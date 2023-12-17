package com.harian.share.location.closersharelocation.post;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
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

    public List<Post> findByPage(Integer page, Integer pageSize) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdTime").descending())).getContent();
    }

    public Post findById(Long id) throws PostNotFoundException {
        return postRepository.findById(id).orElseThrow(() -> new PostNotFoundException("post with id '" + id + "' not found"));
    }
}
