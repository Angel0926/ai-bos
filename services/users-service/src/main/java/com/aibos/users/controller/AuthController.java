package com.aibos.users.controller;

import com.aibos.users.model.User;
import com.aibos.users.security.JwtService;
import com.aibos.users.service.RefreshTokenService;
import com.aibos.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(UserService userService,
                          JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        User u = userService.register(
                body.get("email"),
                body.get("password"),
                body.getOrDefault("name", "")
        );
        return ResponseEntity.ok(
                Map.of(
                        "id", u.getId(),
                        "email", u.getEmail(),
                        "role", u.getRole()
                )
        );

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || password == null) {
            throw new IllegalArgumentException("Email and password required");
        }

        User u = userService.login(email, password);

        String accessToken = jwtService.generateToken(
                u.getId(), u.getRole()
        );

        String refreshToken = refreshTokenService.create(u.getId());

        return ResponseEntity.ok(
                Map.of(
                        "accessToken", accessToken,
                        "refreshToken", refreshToken
                )
        );
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logout() {

        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        refreshTokenService.deleteByUserId(userId);

        return ResponseEntity.ok(
                Map.of("message", "Logged out successfully")
        );
    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> body) {

        User user = refreshTokenService.validate(
                body.get("refreshToken")
        );

        String newAccessToken = jwtService.generateToken(
                user.getId(), user.getRole()
        );

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @GetMapping("/secure-test")
    public ResponseEntity<?> secure() {
        return ResponseEntity.ok(
                Map.of("userId",
                        SecurityContextHolder.getContext()
                                .getAuthentication()
                                .getPrincipal())
        );
    }

    @GetMapping("/admin-only")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> admin() {
        return ResponseEntity.ok("ADMIN OK");
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        String userId = (String) SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        return ResponseEntity.ok(
                userService.getById(userId)
        );
    }
}
