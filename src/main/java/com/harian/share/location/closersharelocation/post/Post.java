package com.harian.share.location.closersharelocation.post;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harian.share.location.closersharelocation.comment.Comment;
import com.harian.share.location.closersharelocation.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

@Data
@Entity
public class Post {
    @Id
    @GeneratedValue
    private Long id;
    private String title;
    private String content;
    private List<String> images;
    private Long createdTime;
    private Long lastModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnoreProperties("ownedPosts")
    private User owner;

    @ManyToMany(mappedBy = "likedPosts", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("likedPosts")
    List<User> likes;

    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    List<Comment> comments;
}
