package com.planeo.planeo_auth.service;

import com.planeo.planeo_auth.domain.ports.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Erases the credentials of a user (username, password hash, role). Removing the row both
 * disables the account and revokes its tokens: login and refresh look the user up and fail
 * from then on, so no session can mint a new access token. Idempotent.
 */
@Service
public class AccountErasureService {

    private final UserRepository userRepository;

    public AccountErasureService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public void erase(String username) {
        userRepository.findByUsername(username).ifPresent(userRepository::delete);
    }
}
