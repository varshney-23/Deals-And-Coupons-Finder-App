package com.dealsandcoupons.user_service.repository;

import com.dealsandcoupons.user_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsById(Long id);
    Optional<User> findByEmail(String email);
}