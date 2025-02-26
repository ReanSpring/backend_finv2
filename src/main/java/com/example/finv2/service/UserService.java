package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Balance;
import com.example.finv2.model.User;
import com.example.finv2.repo.UserRepo;
import com.example.finv2.request.AuthRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public Map<String, String> signUp(AuthRequest authRequest) {
        if (!authRequest.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (authRequest.getPassword().length() < 8 || !authRequest.getPassword().matches(".*[!@#$%^&*()].*")) {
            throw new IllegalArgumentException("Password must be at least 8 characters long and contain at least one special character");
        }

        if (userRepo.findUserByEmail(authRequest.getEmail()).isEmpty()) {
            User user = new User();
            user.setUsername(authRequest.getUsername());
            user.setEmail(authRequest.getEmail());
            user.setPassword(passwordEncoder.encode(authRequest.getPassword()));
            if (user.getCreatedAt() == null) {
                user.setCreatedAt(LocalDate.now());
            }
            userRepo.save(user);
            Map<String, String> token = jwtUtil.generateToken(user.getEmail(), "USER");
            token.put("username", user.getUsername());
            return token;
        } else {
            throw new IllegalArgumentException("Email already exists");
        }
    }

    public Map<String, String> login(AuthRequest authRequest) {
        User user = userRepo.findUserByEmail(authRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + authRequest.getEmail()));
        if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            Map<String, String> token = jwtUtil.generateToken(user.getEmail(), "USER");
            token.put("username", user.getUsername());
            return token;
        } else {
            throw new IllegalArgumentException("Invalid password");
        }
    }

    //    profile
   public User profile(String token) {
    String username = jwtUtil.extractUsername(token.substring(7));
    User currentUser = userRepo.findUserByEmail(username).orElse(null);

    if (currentUser != null) {
        // Take the last total from balance
        List<Balance> balances = currentUser.getBalances();
        if (!balances.isEmpty()) {
            Balance lastBalance = balances.get(balances.size() - 1);
            double total = lastBalance.getTotal();
            currentUser.setBalance(total);
        }
        return currentUser;
    } else {
        throw new IllegalArgumentException("User not found");
    }
}


    //    list all users
    public List<User> findAllUsers(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return userRepo.findAll();
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}