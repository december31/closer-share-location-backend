package com.harian.share.location.closersharelocation.user.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.harian.share.location.closersharelocation.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  Optional<User> findByEmail(String email);

  List<User> findByEmailContaining(String query);

  List<User> findByNameContaining(String query);

  List<User> findByAddressContaining(String query);

  List<User> findByPhoneNumberContaining(String query);
}
