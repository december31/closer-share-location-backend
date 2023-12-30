package com.harian.share.location.closersharelocation.post.image;

import java.util.List;
import java.util.stream.Collectors;

import com.harian.share.location.closersharelocation.post.comment.CommentDTO;
import com.harian.share.location.closersharelocation.user.UserDTO;

import lombok.Data;

@Data
public class ImageDTO {
    private Long id;
    private String url;
    private List<CommentDTO> comments;
    private List<UserDTO> likes;

    public ImageDTO(Image image) {
        this.id = image.getId();
        this.url = image.getUrl();

        if (image.getComments() != null) {
            this.comments = image.getComments().stream().map(comment -> new CommentDTO(comment))
                    .collect(Collectors.toList());
        }

        if (image.getLikes() != null) {
            this.likes = image.getLikes().stream().map(user -> new UserDTO(user)).collect(Collectors.toList());
        }
    }
}
