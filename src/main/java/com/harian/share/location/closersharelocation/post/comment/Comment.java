package com.harian.share.location.closersharelocation.post.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.harian.share.location.closersharelocation.post.Post;
import com.harian.share.location.closersharelocation.post.image.Image;
import com.harian.share.location.closersharelocation.user.model.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Comment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Image image;

    @Column(columnDefinition = "ntext")
    private String content;

    @Column(columnDefinition = "bigint")
    private Long createdTime;
}
