package com.aibos.users.controller;

import com.aibos.users.model.User;
import com.aibos.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        String name = body.getOrDefault("name", "");
        try {
            User u = userService.register(email, password, name);
            return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail(), "name", u.getName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");
        try {
            User u = userService.login(email, password);
            // For now return simple profile (later: JWT)
            return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail(), "name", u.getName()));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(401).body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@RequestParam String id) {
        User u = userService.getById(id);
        if (u == null) return ResponseEntity.status(404).body(Map.of("error", "User not found"));
        return ResponseEntity.ok(Map.of("id", u.getId(), "email", u.getEmail(), "name", u.getName()));
    }
}
