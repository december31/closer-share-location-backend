package com.harian.share.location.closersharelocation.post;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    @Id
    @GeneratedValue
    private Long id;

    @Column(columnDefinition = "nvarchar(25500)")
    private String title;
    @Column(columnDefinition = "nvarchar(25500)")
    private String content;

    private Long createdTime;
    private Long lastModified;

    @ElementCollection
    private List<String> images;

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
