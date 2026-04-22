package com.example.gateway.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProfileController {

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal Object principal) {
        String username = principal instanceof UserDetails ud ? ud.getUsername() : principal.toString();
        return ResponseEntity.ok(Map.of(
                "username", username,
                "message", "Welcome to your profile"
        ));
    }

    @GetMapping("/docs")
    public ResponseEntity<Map<String, Object>> getDocs() {
        return ResponseEntity.ok(Map.of(
                "message", "API documentation",
                "version", "1.0.0"
        ));
    }
}
