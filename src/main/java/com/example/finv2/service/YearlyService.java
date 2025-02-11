package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.User;
import com.example.finv2.model.Yearly;
import com.example.finv2.repo.YearlyRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class YearlyService {
    private final YearlyRepo yearlyRepo;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public YearlyService(YearlyRepo yearlyRepo, UserRepo userRepo, JwtUtil jwtUtil) {
        this.yearlyRepo = yearlyRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public List<Yearly> findAllYearly(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return yearlyRepo.findAllByUser(currentUser);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Yearly createYearly(Yearly yearly, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            yearly.setUser(currentUser);
            return yearlyRepo.save(yearly);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Yearly updateYearly(Long id, Yearly yearly, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        Yearly yearlyToUpdate = yearlyRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yearly not found"));
        if (currentUser != null && yearlyToUpdate.getUser().equals(currentUser)) {
            yearlyToUpdate.setYear(yearly.getYear());
            return yearlyRepo.save(yearlyToUpdate);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public void deleteYearly(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        Yearly yearlyToDelete = yearlyRepo.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Yearly not found"));
        if (currentUser != null && yearlyToDelete.getUser().equals(currentUser)) {
            yearlyRepo.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }
}