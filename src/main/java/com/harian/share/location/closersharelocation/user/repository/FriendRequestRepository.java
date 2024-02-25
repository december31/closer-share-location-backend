package com.harian.share.location.closersharelocation.user.repository;

import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harian.share.location.closersharelocation.user.model.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {
    public Set<FriendRequest> findByUserId(Long userId);

    public void deleteByUserId(Long userId);

    public Optional<FriendRequest> findByUserIdAndRequestorId(Long userId, Long requestorId);
}
