package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Balance;
import com.example.finv2.model.User;
import com.example.finv2.repo.BalanceRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class BalanceService {
    private final BalanceRepo balanceRepo;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;

    public BalanceService(BalanceRepo balanceRepo, JwtUtil jwtUtil, UserRepo userRepo) {
        this.balanceRepo = balanceRepo;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
    }

    public Optional<Balance> findAllBalance(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return balanceRepo.findAllByUser(currentUser);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public void updateUserBalance(User user) {
        Balance balance = balanceRepo.findByUser(user).orElse(null);
        if (balance == null) {
            balance = new Balance();
            balance.setUser(user);
        }
        balance.setAmount(user.getYearlies().stream().mapToDouble(y -> y.getAmount()).sum() +
                user.getMonthlies().stream().mapToDouble(m -> m.getAmount()).sum() +
                user.getWeeklies().stream().mapToDouble(w -> w.getAmount()).sum() +
                user.getDailies().stream().mapToDouble(d -> d.getAmount()).sum());
        balanceRepo.save(balance);
    }

}
