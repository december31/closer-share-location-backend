package com.harian.share.location.closersharelocation.post;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.user.User;
import com.harian.share.location.closersharelocation.utils.FileUploadUtil;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.post.comment.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Post create(Post post, Principal connectedUser) {

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        post.setOwner(user);
        post.setCreatedTime(System.currentTimeMillis());
        post.setLastModified(System.currentTimeMillis());

        return postRepository.save(post);
    }

    public Comment createComment(Comment comment, Principal connectedUser, Long postId) throws PostNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        comment.setOwner(user);
        comment.setCreatedTime(System.currentTimeMillis());
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public List<Post> findByPage(Integer page, Integer pageSize) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        return postRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdTime").descending())).getContent();
    }

    public Post findById(Long id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + id + "' not found"));
    }

    public Post savePostImages(List<MultipartFile> files, Long postId) throws PostNotFoundException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        files.stream().forEach(file -> {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            String uploadDir = "avatar/" + post.getId();
            try {
                FileUploadUtil.saveFile(uploadDir, fileName, file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            post.getImages().add(fileName);
        });
        return postRepository.save(post);
    }
}
