package com.harian.share.location.closersharelocation.post.comment;

import com.harian.share.location.closersharelocation.user.model.dto.UserDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private UserDTO owner;
    private Long createdTime;

    public CommentDTO(Comment comment) {
        this.id = comment.getId(); 
        this.content = comment.getContent();
        this.owner = new UserDTO(comment.getOwner());
        this.createdTime = comment.getCreatedTime();
    }
}
