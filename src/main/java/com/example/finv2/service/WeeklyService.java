package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.Monthly;
import com.example.finv2.model.User;
import com.example.finv2.model.Weekly;
import com.example.finv2.repo.MonthlyRepo;
import com.example.finv2.repo.UserRepo;
import com.example.finv2.repo.WeeklyRepo;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class WeeklyService {
    private final WeeklyRepo weeklyRepo;
    private final MonthlyRepo monthlyRepo;
    private final UserRepo userRepo;
    private final JwtUtil jwtUtil;

    public WeeklyService(WeeklyRepo weeklyRepo, MonthlyRepo monthlyRepo, UserRepo userRepo, JwtUtil jwtUtil) {
        this.weeklyRepo = weeklyRepo;
        this.monthlyRepo = monthlyRepo;
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    public List<Weekly> findAllWeekly(String token) {
        User currentUser = getUserFromToken(token);
        return weeklyRepo.findAllByUser(currentUser);
    }

    public Weekly createWeekly(Weekly weekly, String token) {
        User currentUser = getUserFromToken(token);
        weekly.setUser(currentUser);
        Weekly savedWeekly = weeklyRepo.save(weekly);
        updateMonthlyTotal(savedWeekly);
        return savedWeekly;
    }

    public Weekly updateWeekly(Long id, Weekly weekly, String token) {
        User currentUser = getUserFromToken(token);
        Weekly weeklyToUpdate = weeklyRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weekly record not found or unauthorized"));

        weeklyToUpdate.setWeek(weekly.getWeek());
        weeklyToUpdate.setAmount(weekly.getAmount());
        Weekly updatedWeekly = weeklyRepo.save(weeklyToUpdate);

        updateMonthlyTotal(updatedWeekly);
        return updatedWeekly;
    }

    public void deleteWeekly(Long id, String token) {
        User currentUser = getUserFromToken(token);
        Weekly weeklyToDelete = weeklyRepo.findByIdAndUser(id, currentUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Weekly record not found or unauthorized"));

        weeklyRepo.delete(weeklyToDelete);
        updateMonthlyTotal(weeklyToDelete);
    }

    private void updateMonthlyTotal(Weekly weekly) {
        try {
            LocalDate startDate = LocalDate.parse(weekly.getWeek().split(" - ")[0], DateTimeFormatter.ISO_DATE);
            YearMonth yearMonth = YearMonth.from(startDate);
            LocalDate startOfMonth = yearMonth.atDay(1);
            LocalDate endOfMonth = yearMonth.atEndOfMonth();

            List<Weekly> weeklies = weeklyRepo.findAllByUserAndWeekBetween(weekly.getUser(), startOfMonth.toString(), endOfMonth.toString());
            double totalIncome = weeklies.stream().mapToDouble(Weekly::getAmount).sum();

            List<Monthly> monthlies = monthlyRepo.findByMonthAndUser(yearMonth.toString(), weekly.getUser());
            Monthly monthly;
            if (monthlies.isEmpty()) {
                monthly = new Monthly(); // Create a new Monthly instance if none found
            } else {
                monthly = monthlies.get(0); // Get the first entry if found
            }

            monthly.setMonth(yearMonth.toString());
            monthly.setAmount(totalIncome);
            monthly.setYear(String.valueOf(yearMonth.getYear()));
            monthly.setUser(weekly.getUser());

            monthlyRepo.save(monthly);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid week format: " + weekly.getWeek(), e);
        }
    }
    private User getUserFromToken(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        return userRepo.findUserByEmail(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}
