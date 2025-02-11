package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Monthly;
import com.example.finv2.model.User;
import com.example.finv2.model.Yearly;
import com.example.finv2.repo.MonthlyRepo;
import com.example.finv2.repo.UserRepo;
import com.example.finv2.repo.YearlyRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class MonthlyService {
    private final MonthlyRepo monthlyRepo;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final YearlyRepo yearlyRepo;

    public MonthlyService(MonthlyRepo monthlyRepo, JwtUtil jwtUtil, UserRepo userRepo, YearlyRepo yearlyRepo) {
        this.monthlyRepo = monthlyRepo;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.yearlyRepo = yearlyRepo;
    }

    public List<Monthly> findAllMonthly(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return monthlyRepo.findAllByUser(currentUser);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Monthly createMonthly(Monthly monthly, String token){
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            monthly.setUser(currentUser);
            return monthlyRepo.save(monthly);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Monthly updateMonthly(Long id, Monthly monthly, String token){
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        Monthly existingMonthly = monthlyRepo.findById(id).orElse(null);
        if (currentUser != null && existingMonthly != null && existingMonthly.getUser().equals(currentUser)) {
            monthly.setId(id);
            monthly.setUser(currentUser);
            return monthlyRepo.save(monthly);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Monthly deleteMonthly(Long id, String token){
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        Monthly existingMonthly = monthlyRepo.findById(id).orElse(null);
        if (currentUser != null && existingMonthly != null && existingMonthly.getUser().equals(currentUser)) {
            monthlyRepo.deleteById(id);
            return existingMonthly;
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }


}