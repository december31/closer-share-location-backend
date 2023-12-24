package com.harian.share.location.closersharelocation.post;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harian.share.location.closersharelocation.exception.PostNotFoundException;
import com.harian.share.location.closersharelocation.user.User;
import com.harian.share.location.closersharelocation.utils.FileUploadUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.post.comment.CommentRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public Post create(HttpServletRequest request, Principal connectedUser) throws IOException, ServletException {
        ObjectMapper mapper = new ObjectMapper();
        
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        Post post = mapper.readValue(request.getParameter("post"), Post.class);
        post.setOwner(user);
        post.setCreatedTime(System.currentTimeMillis());
        post.setLastModified(System.currentTimeMillis());
        post.setImages(saveImages(request, user));
        return postRepository.save(post);
    }
    
    private List<String> saveImages(HttpServletRequest request, User user) throws IOException, ServletException {
        ArrayList<String> imageUrls = new ArrayList<>();
        List<Part> imageParts = request.getParts().stream().filter(part -> part.getName().equals("image")).collect(Collectors.toList());
        String absoluteFolderPath = "C:/apache-tomcat-10.1.16/webapps/closer/WEB-INF/classes/static/post/" + user.getId() + "/images";
        String relativeFolderPath = "post/" + user.getId() + "/images";
        File folder = new File(absoluteFolderPath);
        if (!folder.exists()) folder.mkdirs();
        imageParts.forEach(item -> {
            try {
                String path = absoluteFolderPath + "/" + item.getSubmittedFileName();
                String dbPath = relativeFolderPath + "/" + item.getSubmittedFileName();
                item.write(path);
                imageUrls.add(dbPath);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return imageUrls;
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
