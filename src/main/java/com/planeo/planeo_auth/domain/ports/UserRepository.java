package com.planeo.planeo_auth.domain.ports;

import com.planeo.planeo_auth.domain.entity.User;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findByUsername(String username);
    User save(User user);
}
