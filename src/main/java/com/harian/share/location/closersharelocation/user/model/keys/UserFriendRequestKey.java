package com.harian.share.location.closersharelocation.user.model.keys;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Embeddable
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class UserFriendRequestKey implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "requestor_id")
    private Long requestorId;
}
