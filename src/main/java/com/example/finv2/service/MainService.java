package com.example.finv2.service;

import com.example.finv2.repo.UserRepo;
import com.example.finv2.Security.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class MainService {

    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public MainService(UserRepo userRepo, JwtUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public boolean isAuthorized(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        return userRepo.findUserByEmail(username).isPresent();
    }

    public String extractUsername(String token) {
        return jwtUtil.extractUsername(token.substring(7));
    }



}
