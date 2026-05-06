package com.planeo.planeo_auth.infrastructure.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planeo.planeo_auth.domain.entity.User;
import com.planeo.planeo_auth.domain.enums.Role;
import com.planeo.planeo_auth.domain.ports.UserRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    public UserEventConsumer(UserRepository userRepository,
                             PasswordEncoder passwordEncoder,
                             ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "user.created", groupId = "planeo-auth-group")
    public void consume(String message) {
        try {
            UserEventDTO event = objectMapper.readValue(message, UserEventDTO.class);

            if (userRepository.findByUsername(event.username()).isPresent()) {
                return;
            }

            User user = new User(
                    event.username(),
                    passwordEncoder.encode(event.password()),
                    Role.valueOf(event.role())
            );

            userRepository.save(user);
            System.out.println("User créé dans planeo_auth : " + event.username());

        } catch (Exception e) {
            System.err.println("Erreur lors de la consommation de l'event : " + e.getMessage());
        }
    }
}
