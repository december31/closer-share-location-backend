package com.harian.share.location.closersharelocation.post;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.gson.Gson;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.exception.UserNotFoundException;
import com.harian.share.location.closersharelocation.firebase.FirebaseCloudMessagingService;
import com.harian.share.location.closersharelocation.firebase.model.NotificationData;
import com.harian.share.location.closersharelocation.firebase.model.NotificationRequest;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.repository.UserRepository;
import com.harian.share.location.closersharelocation.user.service.UserService;
import com.harian.share.location.closersharelocation.utils.Utils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.post.comment.CommentDTO;
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
    private final UserService userService;
    private final Utils utils;
    private final FirebaseCloudMessagingService firebaseService;

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

        PostDTO postDTO = PostDTO.fromPost(post);

        Set<User> friends = user.getFriends().stream().map(friend -> friend.getFriend()).collect(Collectors.toSet());

        try {
            firebaseService.pushNotification(NotificationRequest.builder()
                    .title(user.getName())
                    .data(NotificationData.builder()
                            .type(NotificationData.Type.POST)
                            .title("Just post a new moment")
                            .data(new Gson().toJson(postDTO))
                            .build())
                    .priority(AndroidConfig.Priority.HIGH)
                    .tokens(friends.stream().flatMap(f -> f.getDevices().stream())
                            .map(device -> device.getFirebaseMessagingToken()).toList())
                    .build());
        } catch (FirebaseMessagingException e) {
            e.printStackTrace();
        }
        return postDTO;
    }

    private void saveImages(Post post, HttpServletRequest request, User user) throws IOException, ServletException {
        List<Part> imageParts = request.getParts().stream().filter(part -> part.getName().equals("image"))
                .collect(Collectors.toList());
        String absoluteFolderPath = "E:/CloserShareLocation/post/" + user.getId() + "/images";
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

    public Comment createComment(CommentDTO commentDTO, Principal connectedUser, Long postId)
            throws PostNotFoundException, UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        Comment comment = Comment.builder()
                .content(commentDTO.getContent())
                .owner(user)
                .post(post)
                .createdTime(System.currentTimeMillis())
                .build();

        if (comment.getOwner().getId() != user.getId()) {
            try {
                firebaseService.pushNotification(NotificationRequest.builder()
                        .title(user.getName())
                        .data(NotificationData.builder()
                                .type(NotificationData.Type.POST)
                                .title("Just comment on your post")
                                .data(new Gson().toJson(PostDTO.fromPost(post)))
                                .build())
                        .priority(AndroidConfig.Priority.HIGH)
                        .tokens(post.getOwner().getDevices().stream()
                                .map(device -> device.getFirebaseMessagingToken()).toList())
                        .build());
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
        return commentRepository.save(comment);
    }

    public PostDTO like(Long postId, Principal connectedUser) throws PostNotFoundException, UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        post.getLikes().add(user);
        user.getLikedPosts().add(post);
        userRepository.save(user);
        PostDTO postDTO = PostDTO.fromPost(post);
        postDTO.setIsLiked(
                !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                        .isEmpty());

        User owner = post.getOwner();
        if (owner.getId() != user.getId()) {
            try {
                firebaseService.pushNotification(NotificationRequest.builder()
                        .title(user.getName())
                        .data(NotificationData.builder()
                                .type(NotificationData.Type.POST)
                                .title("Just like your post")
                                .data(new Gson().toJson(postDTO))
                                .build())
                        .priority(AndroidConfig.Priority.HIGH)
                        .tokens(owner.getDevices().stream()
                                .map(device -> device.getFirebaseMessagingToken()).toList())
                        .build());
            } catch (FirebaseMessagingException e) {
                e.printStackTrace();
            }
        }
        return postDTO;
    }

    public PostDTO unlike(Long postId, Principal connectedUser) throws PostNotFoundException, UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        post.getLikes().remove(user);
        user.getLikedPosts().remove(post);
        userRepository.save(user);
        PostDTO postDTO = PostDTO.fromPost(post);
        postDTO.setIsLiked(
                !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                        .isEmpty());
        return postDTO;
    }

    public PostDTO watch(Long postId, Principal connectedUser) throws PostNotFoundException, UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + postId + "' not found"));
        if (!post.getWatches().contains(user)) {
            post.getWatches().add(user);
            user.getWatchedPosts().add(post);
        }
        userRepository.save(user);
        PostDTO postDTO = PostDTO.fromPost(post);
        postDTO.setIsLiked(
                !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                        .isEmpty());
        return postDTO;
    }

    public List<PostDTO> findByPage(Integer page, Integer pageSize, Principal connectedUser)
            throws UserNotFoundException {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 10 : pageSize;
        User user = userService.getUserFromPrincipal(connectedUser);
        List<Post> posts = postRepository.findAll(PageRequest.of(page, pageSize, Sort.by("createdTime").descending()))
                .getContent();
        List<PostDTO> postDTOs = posts.stream().map(post -> {
            PostDTO postDTO = PostDTO.fromPost(post);
            postDTO.setIsLiked(
                    !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                            .isEmpty());
            return postDTO;
        })
                .collect(Collectors.toList());
        return postDTOs;
    }

    public List<PostDTO> searchPost(String query, Integer page, Integer pageSize, Principal connectedUser)
            throws UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        List<Post> posts = new ArrayList<>();
        List<User> users = userService.searchUsers(query, page, pageSize, connectedUser);
        posts.addAll(postRepository.findByTitleContaining(query));
        posts.addAll(postRepository.findByContentContaining(query));
        for (User u : users) {
            posts.addAll(u.getOwnedPosts());
        }
        posts = posts.stream().distinct().limit(20).collect(Collectors.toList());
        return posts.stream().map(post -> {
            PostDTO postDTO = PostDTO.fromPost(post);
            postDTO.setIsLiked(
                    !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                            .isEmpty());
            return postDTO;
        }).collect(Collectors.toList());
    }

    public Post findById(Long id) throws PostNotFoundException {
        return postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + id + "' not found"));
    }

    public PostDTO findById(Long id, Principal connectedUser) throws PostNotFoundException, UserNotFoundException {
        User user = userService.getUserFromPrincipal(connectedUser);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new PostNotFoundException("post with id '" + id + "' not found"));
        PostDTO postDTO = PostDTO.fromPost(post);
        postDTO.setIsLiked(
                !postDTO.getLikes().stream().filter(u -> u.getId() == user.getId()).collect(Collectors.toList())
                        .isEmpty());
        return postDTO;
    }
}
