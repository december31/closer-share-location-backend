package com.harian.share.location.closersharelocation.post;

import java.util.Set;
import java.util.stream.Collectors;

import com.harian.share.location.closersharelocation.post.comment.CommentDTO;
import com.harian.share.location.closersharelocation.post.image.ImageDTO;
import com.harian.share.location.closersharelocation.user.model.User;
import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;

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
    private Long createdTime;
    private Long lastModified;
    private UserDTO owner;
    private Set<ImageDTO> images;
    private Set<CommentDTO> comments;
    private Set<UserDTO> likes;
    private Set<UserDTO> watches;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdTime = post.getCreatedTime();
        this.lastModified = post.getLastModified();
        this.owner = UserDTO.fromUser(post.getOwner());

        if (post.getImages() != null) {
            this.images = post.getImages().stream().map(image -> new ImageDTO(image)).collect(Collectors.toSet());
        }
        if (post.getComments() == null)
            post.setComments(Set.of());
        this.comments = post.getComments().stream().map(comment -> new CommentDTO(comment))
                .collect(Collectors.toSet());

        if (post.getLikes() == null)
            post.setLikes(Set.of());
        this.likes = post.getLikes().stream().map(user -> UserDTO.fromUser(user)).collect(Collectors.toSet());

        if (post.getWatches() == null)
            post.setWatches(Set.of());
        this.watches = post.getWatches().stream().map(user -> UserDTO.fromUser(user)).collect(Collectors.toSet());
    }

    public static PostDTO fromPost(Post post) {
        return new PostDTO(post);
    }
}
