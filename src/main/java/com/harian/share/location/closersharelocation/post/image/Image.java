package com.harian.share.location.closersharelocation.post.image;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.harian.share.location.closersharelocation.post.Post;
import com.harian.share.location.closersharelocation.post.comment.Comment;
import com.harian.share.location.closersharelocation.user.User;

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
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Image {
    @Id
    @GeneratedValue
    private Long id;
    private String url;

    @OneToMany(mappedBy = "image", fetch = FetchType.EAGER)
    private List<Comment> comments;

    @ManyToMany(mappedBy = "likedImages", fetch = FetchType.EAGER)
    @JsonIgnoreProperties("likedImages")
    private List<User> likes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Post post;
}
