package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Balance;
import com.example.finv2.model.Income;
import com.example.finv2.model.User;
import com.example.finv2.repo.BalanceRepo;
import com.example.finv2.repo.IncomeRepo;
import com.example.finv2.repo.UserRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


@Service
public class IncomeService {
    private final IncomeRepo incomeRepo;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;
    private final BalanceRepo balanceRepo;


    public IncomeService(IncomeRepo incomeRepo, UserRepo userRepo, JwtUtil jwtUtil, BalanceRepo balanceRepo) {
        this.incomeRepo = incomeRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
        this.balanceRepo = balanceRepo;
    }

    public Page<Income> findAllIncome(String token, int page, int size) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return incomeRepo.findByUser(currentUser, pageable);
    }

    public Income createIncome(Income income, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // If date is null, set date to current date
        if (income.getDate() == null) {
            income.setDate(LocalDate.now());
        }
        income.setUser(currentUser);

        // Fetch the latest balance
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(currentUser);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;

        // Add the income amount to the current total
        currentTotal += income.getAmount();

        // Create a new balance record
        Balance newBalance = new Balance();
        newBalance.setUser(currentUser);
        newBalance.setDate(LocalDate.now());
        newBalance.setAmount(income.getAmount());
        newBalance.setTotal(currentTotal);
        newBalance.setReason(income.getSource());
        newBalance.setType(Balance.Type.ADD);
        balanceRepo.save(newBalance);

        // Save the income record
        return incomeRepo.save(income);
    }

    public Income updateIncome(Income income, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Income existingIncome = incomeRepo.findById(income.getId())
                .orElseThrow(() -> new IllegalArgumentException("Income record not found"));

        if (!existingIncome.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized to update this record");
        }

        double oldAmount = existingIncome.getAmount();
        double newAmount = income.getAmount();

        if (newAmount != oldAmount) {
            double difference = newAmount - oldAmount;
            updateBalance(currentUser, difference);
        }

        existingIncome.setAmount(newAmount);
        existingIncome.setSource(income.getSource());
        existingIncome.setDate(income.getDate());
        return incomeRepo.save(existingIncome);
    }

    private void updateBalance(User user, double amount) {
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(user);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;

        if (amount < 0 && currentTotal < -amount) {
            throw new IllegalArgumentException("Insufficient balance to subtract.");
        }

        currentTotal += amount;

        Balance newBalance = new Balance();
        newBalance.setUser(user);
        newBalance.setDate(LocalDate.now());
        newBalance.setAmount(Math.abs(amount));
        newBalance.setTotal(currentTotal);
        newBalance.setReason("Income update");
        newBalance.setType(amount > 0 ? Balance.Type.ADD : Balance.Type.SUBTRACT);
        balanceRepo.save(newBalance);
    }

    public void deleteIncome(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Income incomeToDelete = incomeRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Income record not found"));

        if (!incomeToDelete.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized to delete this record");
        }

        // Subtract the income amount from the user's balance
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(currentUser);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;
        double incomeAmount = incomeToDelete.getAmount();
        if (currentTotal >= incomeAmount) {
            currentTotal -= incomeAmount;
            Balance newBalance = new Balance();
            newBalance.setUser(currentUser);
            newBalance.setDate(LocalDate.now());
            newBalance.setAmount(incomeAmount);
            newBalance.setTotal(currentTotal);
            newBalance.setReason("Income deletion");
            newBalance.setType(Balance.Type.SUBTRACT);
            balanceRepo.save(newBalance);
        } else {
            throw new IllegalArgumentException("Insufficient balance to subtract.");
        }

        incomeRepo.deleteById(id);
    }
}