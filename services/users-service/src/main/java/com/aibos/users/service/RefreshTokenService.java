package com.aibos.users.service;

import com.aibos.users.model.RefreshToken;
import com.aibos.users.model.User;
import com.aibos.users.repository.RefreshTokenRepository;
import com.aibos.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private static final long REFRESH_EXPIRATION_SECONDS = 7 * 24 * 60 * 60;

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    public RefreshTokenService(RefreshTokenRepository repo,
                               UserRepository userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    @Transactional
    public String create(String userId) {
        repo.deleteByUserId(userId);

        RefreshToken token = new RefreshToken(
                UUID.randomUUID().toString(),   // id
                userId,
                UUID.randomUUID().toString(),   // refresh token value
                Instant.now().plusSeconds(REFRESH_EXPIRATION_SECONDS)
        );

        repo.save(token);
        return token.getToken();
    }

    @Transactional
    public User validate(String token) {
        RefreshToken rt = repo.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        if (rt.getExpiresAt().isBefore(Instant.now())) {
            repo.delete(rt);
            throw new IllegalArgumentException("Refresh token expired");
        }

        return userRepo.findById(rt.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    @Transactional
    public void deleteByUserId(String userId) {
        repo.deleteByUserId(userId);
    }

    @Transactional
    protected void deleteExpired(RefreshToken token) {
        repo.delete(token);
    }
}
