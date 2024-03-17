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
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.utils.Utils;

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
    private final UserRepository userRepository;
    private final Utils utils;

    public PostDTO create(HttpServletRequest request, Principal connectedUser)
            throws IOException, ServletException, UserNotFoundException {
        ObjectMapper mapper = new ObjectMapper();

        User user = utils.getUserFromPrincipal(connectedUser)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
                
        Post post = mapper.readValue(request.getParameter("post"), Post.class);
        post.setOwner(user);
        post.setCreatedTime(System.currentTimeMillis());
        post.setLastModified(System.currentTimeMillis());
        post = postRepository.save(post);
        saveImages(post, request, user);
        return PostDTO.fromPost(post);
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

    public Post like(Long postId, Principal connectedUser) throws PostNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        post.getLikes().add(user);
        user.getLikedPosts().add(post);
        userRepository.save(user);
        return postRepository.save(post);
    }

    public Post watch(Long postId, Principal connectedUser) throws PostNotFoundException {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        if (!post.getWatches().contains(user)) {
            post.getWatches().add(user);
            user.getWatchedPosts().add(post);
        }
        userRepository.save(user);
        return postRepository.save(post);
    }

    public List<PostDTO> findByPage(Integer page, Integer pageSize) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        List<Post> posts = postRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent();
        List<PostDTO> postDTOs = posts.stream().map(post -> new PostDTO(post))
                .collect(Collectors.toList());
        return postDTOs;
    }

    public Post findById(Long id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + id + "' not found"));
    }
}
