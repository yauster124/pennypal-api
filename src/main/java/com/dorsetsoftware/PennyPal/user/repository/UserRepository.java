package com.dorsetsoftware.PennyPal.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dorsetsoftware.PennyPal.user.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String email);
    boolean existsByUsername(String email);
}
