package com.harian.share.location.closersharelocation.messaging.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.harian.share.location.closersharelocation.messaging.model.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(value = "SELECT * FROM message WHERE (sender_id = ?1 AND receiver_id = ?2) OR (sender_id = ?2 AND receiver_id = ?1) ORDER BY time desc", nativeQuery = true)
    List<Message> findAllMessage(Long userId1, Long userId2);
}
