package com.planeo.planeo_auth.controller;

import com.planeo.planeo_auth.dto.LoginRequestDTO;
import com.planeo.planeo_auth.dto.LoginResponseDTO;
import com.planeo.planeo_auth.dto.RefreshRequestDTO;
import com.planeo.planeo_auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService service;

    public AuthController(AuthService service) {
        this.service = service;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(service.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(@RequestBody RefreshRequestDTO request) {
        return ResponseEntity.ok(service.refresh(request.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Côté serveur stateless, le logout est géré côté client
        // On invalidera les tokens via une blacklist plus tard si besoin
        return ResponseEntity.noContent().build();
    }


}
