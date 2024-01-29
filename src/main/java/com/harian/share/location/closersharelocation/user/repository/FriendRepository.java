package com.harian.share.location.closersharelocation.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harian.share.location.closersharelocation.user.model.Friend;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long>{
    
}
