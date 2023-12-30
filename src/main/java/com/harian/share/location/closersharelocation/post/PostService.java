package com.harian.share.location.closersharelocation.post;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.user.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.post.comment.CommentRepository;
import com.harian.share.location.closersharelocation.post.image.Image;
import com.harian.share.location.closersharelocation.post.image.ImageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final ImageRepository imageRepository;

    public Post create(HttpServletRequest request, Principal connectedUser) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();

        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Post post = mapper.readValue(request.getParameter("post"), Post.class);
        post.setOwner(user);
        post.setCreatedTime(System.currentTimeMillis());
        post.setLastModified(System.currentTimeMillis());
        post = postRepository.save(post);
        saveImages(post, request, user);
        return post;
    }

    private void saveImages(Post post, HttpServletRequest request, User user) throws IOException, ServletException {
        List<Part> imageParts = request.getParts().stream().filter(part -> part.getName().equals("image"))
                .collect(Collectors.toList());
        String absoluteFolderPath = "D:/CloserShareLocation/post/" + user.getId() + "/images";
        String relativeFolderPath = "post/" + user.getId() + "/images";
        File folder = new File(absoluteFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        imageParts.forEach(item -> {
            try {
                String path = absoluteFolderPath + "/" + item.getSubmittedFileName();
                String dbPath = relativeFolderPath + "/" + item.getSubmittedFileName();
                item.write(path);
                Image image = Image.builder().url(dbPath).post(post).build();
                imageRepository.save(image);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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
}
