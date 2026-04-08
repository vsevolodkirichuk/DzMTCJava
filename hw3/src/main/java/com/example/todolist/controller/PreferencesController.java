package com.example.todolist.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/preferences")
@Tag(name = "Preferences", description = "User preferences API")
public class PreferencesController {

    @GetMapping("/view")
    @Operation(summary = "Get view preference")
    public ResponseEntity<String> getViewPreference(
            @CookieValue(value = "viewPreference", defaultValue = "detailed") String viewPreference) {
        return ResponseEntity.ok(viewPreference);
    }

    @PostMapping("/view")
    @Operation(summary = "Set view preference")
    public ResponseEntity<String> setViewPreference(@RequestParam String mode, HttpServletResponse response) {
        Cookie cookie = new Cookie("viewPreference", mode);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 30);
        response.addCookie(cookie);
        return ResponseEntity.ok(mode);
    }
}
