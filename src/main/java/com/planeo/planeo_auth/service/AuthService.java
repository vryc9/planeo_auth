package com.planeo.planeo_auth.service;

import com.planeo.planeo_auth.domain.entity.User;
import com.planeo.planeo_auth.domain.ports.UserRepository;
import com.planeo.planeo_auth.dto.LoginRequestDTO;
import com.planeo.planeo_auth.dto.LoginResponseDTO;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final AuthenticationManager manager;
    private final UserRepository repository;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager manager, UserRepository repository, JwtService jwtService) {
        this.manager = manager;
        this.repository = repository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = repository.findByUsername(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO refresh(String refreshToken) {
        if (!jwtService.isTokenValid(refreshToken)) {
            throw new RuntimeException("Refresh token invalide ou expiré");
        }

        String username = jwtService.extractUsername(refreshToken);

        User user = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }
}
