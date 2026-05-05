package com.planeo.planeo_auth.infrastructure.adapter.repository;

import com.planeo.planeo_auth.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JpaUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}