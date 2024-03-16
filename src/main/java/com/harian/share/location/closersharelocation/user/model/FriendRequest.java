package com.harian.share.location.closersharelocation.user.model;

import org.apache.commons.lang3.builder.EqualsExclude;
import org.apache.commons.lang3.builder.HashCodeExclude;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.harian.share.location.closersharelocation.user.model.keys.UserFriendRequestKey;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/*
 * a relational entity for connecting user to their friend requests
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FriendRequest {

    @EmbeddedId
    private UserFriendRequestKey id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @MapsId("requestorId")
    @JoinColumn(name = "requestor_id")
    @JsonIgnore
    private User requestor;

    @EqualsExclude
    @HashCodeExclude
    private Long since;

    private Status status;

    public FriendRequest(User user, User requestor, Long since) {
        this.id = new UserFriendRequestKey(user.getId(), requestor.getId());
        this.user = user;
        this.requestor = requestor;
        this.since = since;
        this.status = Status.PENDING;
    }

    public static enum Status {
        ACCEPTED,
        PENDING,
        DENIED
    }
}
