package com.harian.share.location.closersharelocation.post;

import java.util.List;
import java.util.stream.Collectors;

import com.harian.share.location.closersharelocation.comment.CommentDTO;
import com.harian.share.location.closersharelocation.user.User;
import com.harian.share.location.closersharelocation.user.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDTO {

    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private Long createdTime;
    private Long lastModified;
    private User owner;
    List<CommentDTO> comments;
    List<UserDTO> likes;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.images = post.getImages();
        this.createdTime = post.getCreatedTime();
        this.lastModified = post.getLastModified();
        this.owner = post.getOwner();

        if (post.getComments() == null)
            post.setComments(List.of());
        this.comments = post.getComments().stream().map(comment -> new CommentDTO(comment))
                .collect(Collectors.toList());

        if (post.getLikes() == null)
            post.setLikes(List.of());
        this.likes = post.getLikes().stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
    }
}
