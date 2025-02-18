package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Balance;
import com.example.finv2.model.User;
import com.example.finv2.repo.BalanceRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

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

    public List<Balance> findAllBalance(String token) {
    String username = jwtUtil.extractUsername(token.substring(7));
    User currentUser = userRepo.findUserByEmail(username).orElse(null);
    if (currentUser != null) {
        List<Balance> balances = balanceRepo.findAllByUser(currentUser);
        Collections.reverse(balances);
        return balances;
    }
    return null;
}

    public Balance createBalance(Balance balance, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (balance.getDate() == null) {
            balance.setDate(LocalDate.now());
        }
        balance.setUser(currentUser);

        // Fetch the latest balance
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(currentUser);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;

        // Debugging logs
        System.out.println("Latest total before transaction: " + currentTotal);
        System.out.println("Transaction Type: " + balance.getType());
        System.out.println("Transaction Amount: " + balance.getAmount());

        // Update total based on transaction type
        if (balance.getType() == Balance.Type.ADD) {
            currentTotal += balance.getAmount();
        } else if (balance.getType() == Balance.Type.SUBTRACT) {
            if (currentTotal >= balance.getAmount()) { // Prevent negative balance
                currentTotal -= balance.getAmount();
            } else {
                throw new IllegalArgumentException("Insufficient balance to subtract.");
            }
        } else {
            throw new IllegalArgumentException("Invalid balance type.");
        }

        // Debugging log after calculation
        System.out.println("New total after transaction: " + currentTotal);

        balance.setTotal(currentTotal);

        return balanceRepo.save(balance);
    }

}