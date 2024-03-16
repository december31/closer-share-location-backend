package com.harian.share.location.closersharelocation.user.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harian.share.location.closersharelocation.user.model.keys.UserFriendKey;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * a relational entity for connecting user to their friends
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Friend {

    @EmbeddedId
    private UserFriendKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("friendId")
    @JoinColumn(name = "friend_id")
    @JsonIgnore
    private User friend;

    private Long since;

    public Friend(User user, User friend, Long since) {
        this.id = new UserFriendKey(user.getId(), friend.getId());
        this.user = user;
        this.friend = friend;
        this.since = since;
    }
}
