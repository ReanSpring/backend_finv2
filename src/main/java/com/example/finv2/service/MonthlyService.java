package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Daily;
import com.example.finv2.model.Monthly;
import com.example.finv2.model.User;
import com.example.finv2.repo.DailyRepo;
import com.example.finv2.repo.MonthlyRepo;
import com.example.finv2.repo.UserRepo;
import com.example.finv2.repo.YearlyRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.List;

@Service
public class MonthlyService {
    private final MonthlyRepo monthlyRepo;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final YearlyRepo yearlyRepo;
    private final DailyRepo dailyRepo;

    public MonthlyService(MonthlyRepo monthlyRepo, JwtUtil jwtUtil, UserRepo userRepo, YearlyRepo yearlyRepo, DailyRepo dailyRepo) {
        this.monthlyRepo = monthlyRepo;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.yearlyRepo = yearlyRepo;
        this.dailyRepo = dailyRepo;
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

    public Monthly createMonthly(Monthly monthly, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            monthly.setUser(currentUser);
            return monthlyRepo.save(monthly);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
    }

    public Monthly updateMonthly(Long id, Monthly monthly, String token) {
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

    public Monthly deleteMonthly(Long id, String token) {
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

    public void downloadMonthlyReport(Long monthlyId, String token, HttpServletResponse response) {
    String username = jwtUtil.extractUsername(token.substring(7));
    User currentUser = userRepo.findUserByEmail(username).orElse(null);
    Monthly monthly = monthlyRepo.findById(monthlyId).orElse(null);

    if (currentUser != null && monthly != null && monthly.getUser().equals(currentUser)) {
        YearMonth yearMonth = YearMonth.parse(monthly.getMonth());
        List<Daily> dailyRecords = dailyRepo.findAllByUserAndDateBetween(
                currentUser, yearMonth.atDay(1), yearMonth.atEndOfMonth());

        String month = yearMonth.getMonth().name().toLowerCase();
        String year = String.valueOf(yearMonth.getYear());
        String filename = "monthly_report_" + month + "_" + year + ".csv";

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=" + filename);

        try (PrintWriter responseWriter = response.getWriter()) {
            responseWriter.println("Date,Amount,Source");
            double totalAmount = 0.0;
            for (Daily daily : dailyRecords) {
                responseWriter.printf("%s,%.2f,%s%n", daily.getDate(), daily.getAmount(), daily.getSource());
                totalAmount += daily.getAmount();
            }
            responseWriter.printf("Total,%.2f,%n", totalAmount);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error generating report", e);
        }
    } else {
        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
    }
}
}