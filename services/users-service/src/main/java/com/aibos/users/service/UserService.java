package com.aibos.users.service;

import com.aibos.users.model.User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {

    // In-memory storage for demo — replace with DB later
    private final Map<String, User> usersById = new ConcurrentHashMap<>();
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();

    public User register(String email, String password, String name) {
        if (usersByEmail.containsKey(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        String id = UUID.randomUUID().toString();
        User u = new User(id, email, password, name);
        usersById.put(id, u);
        usersByEmail.put(email, u);
        return u;
    }

    public User login(String email, String password) {
        User u = usersByEmail.get(email);
        if (u == null) throw new IllegalArgumentException("User not found");
        if (!u.getPassword().equals(password)) throw new IllegalArgumentException("Invalid credentials");
        return u;
    }

    public User getById(String id) {
        return usersById.get(id);
    }
}
