package com.planeo.planeo_auth.infrastructure.kafka;

public record UserEventDTO(
        String username,
        String password,
        String role
) {
}
