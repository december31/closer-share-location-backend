package com.harian.share.location.closersharelocation.user.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harian.share.location.closersharelocation.user.model.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    public Set<Friend> findByUserId(Long userId);

    public void deleteByUserId(Long userId);

    public Optional<Friend> findByUserIdAndFriendId(Long userId, Long requestorId);
}
