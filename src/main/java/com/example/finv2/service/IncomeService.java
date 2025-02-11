package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Income;
import com.example.finv2.model.User;
import com.example.finv2.repo.IncomeRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IncomeService {
    private final IncomeRepo incomeRepo;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public IncomeService(IncomeRepo incomeRepo, UserRepo userRepo, JwtUtil jwtUtil) {
        this.incomeRepo = incomeRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public List<Income> findAllIncome(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return incomeRepo.findByUser(currentUser);
        } else {
            return null;
        }
    }

    public Income createIncome(Income income, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        income.setUser(currentUser);
        return incomeRepo.save(income);
    }

    public Income updateIncome(Income income, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        income.setUser(currentUser);
        return incomeRepo.save(income);
    }

    public void deleteIncome(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        incomeRepo.deleteById(id);
    }
}