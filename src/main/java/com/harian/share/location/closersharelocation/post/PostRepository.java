package com.harian.share.location.closersharelocation.post;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.harian.share.location.closersharelocation.user.model.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByOwner(User owner, PageRequest pageRequest);

    List<Post> findByTitleContaining(String query);

    List<Post> findByContentContaining(String query);
}
