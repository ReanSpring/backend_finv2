package com.example.finv2.service;

import com.example.finv2.Security.JwtUtil;
import com.example.finv2.model.*;
import com.example.finv2.repo.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;

@Service
public class DailyService {
    private final DailyRepo dailyRepo;
    private final JwtUtil jwtUtil;
    private final UserRepo userRepo;
    private final WeeklyRepo weeklyRepo;
    private final MonthlyRepo monthlyRepo;
    private final YearlyRepo yearlyRepo;
    private final BalanceRepo balanceRepo;

    public DailyService(DailyRepo dailyRepo, JwtUtil jwtUtil, UserRepo userRepo, WeeklyRepo weeklyRepo, MonthlyRepo monthlyRepo, YearlyRepo yearlyRepo, BalanceRepo balanceRepo) {
        this.dailyRepo = dailyRepo;
        this.jwtUtil = jwtUtil;
        this.userRepo = userRepo;
        this.weeklyRepo = weeklyRepo;
        this.monthlyRepo = monthlyRepo;
        this.yearlyRepo = yearlyRepo;
        this.balanceRepo = balanceRepo;
    }

    public List<Daily> findAllDaily(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            List<Daily> dailies = dailyRepo.findAllByUser(currentUser);
            dailies.sort((d1, d2) -> d2.getDate().compareTo(d1.getDate()));
            return dailies;
        }
        return null;
    }

    public Daily createDaily(Daily daily, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        daily.setUser(currentUser);
        if (daily.getDate() == null) {
            daily.setDate(LocalDate.now()); // default to today if no date provided
        }
        if (daily.getDay() == null) {
            daily.setDay(daily.getDate().getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        }

        // Check if a record with the same date already exists
        List<Daily> existingDailies = dailyRepo.findByDateAndUser(daily.getDate(), currentUser);
        if (!existingDailies.isEmpty()) {
            Daily existingDaily = existingDailies.get(0); // Assume only one record per day
            existingDaily.setAmount(existingDaily.getAmount() + daily.getAmount());
            existingDaily.setSource(existingDaily.getSource() + "\n" + daily.getSource());

            Daily updatedDaily = dailyRepo.save(existingDaily);
            updateUserBalance(currentUser, daily.getDate()); // Pass the actual date
            return updatedDaily;
        }

        // If no existing record, save as a new entry
        Daily savedDaily = dailyRepo.save(daily);
        updateUserBalance(currentUser, daily.getDate()); // Pass the actual date
        return savedDaily;
    }

    private void updateUserBalance(User user, LocalDate dailyDate) {
        // Get the year and month from the daily record date
        YearMonth currentMonth = YearMonth.from(dailyDate);
        int currentYear = dailyDate.getYear();

        // Aggregate Daily Income
        double totalDailyIncome = dailyRepo.findAllByUserAndDateBetween(
                        user, currentMonth.atDay(1), currentMonth.atEndOfMonth()).stream()
                .mapToDouble(Daily::getAmount).sum();

        // Update balance record
        Balance balance = balanceRepo.findByUser(user).orElse(new Balance());
        balance.setUser(user);
        balance.setAmount(totalDailyIncome);
        balanceRepo.save(balance);

        // Aggregate Weekly Data
        int weekOfYear = currentMonth.atDay(1).get(WeekFields.ISO.weekOfWeekBasedYear());
        Weekly weekly = weeklyRepo.findByUserAndWeek(user, String.valueOf(weekOfYear))
                .orElseGet(() -> new Weekly(user, String.valueOf(weekOfYear)));

        weekly.setAmount(totalDailyIncome);
        weeklyRepo.save(weekly);

        // Aggregate Monthly Data
        Monthly monthly = monthlyRepo.findByUserAndMonth(user, currentMonth.toString())
                .orElseGet(() -> new Monthly(user, currentMonth.toString(), String.valueOf(currentYear)));
        monthly.setAmount(totalDailyIncome);
        monthlyRepo.save(monthly);

        // Aggregate Yearly Data
        Yearly yearly = yearlyRepo.findByUserAndYear(user, String.valueOf(currentYear))
                .orElseGet(() -> new Yearly(user, String.valueOf(currentYear)));
        yearly.setAmount(dailyRepo.findAllByUserAndDateBetween(user,
                        LocalDate.of(currentYear, 1, 1), LocalDate.of(currentYear, 12, 31))
                .stream().mapToDouble(Daily::getAmount).sum());
        yearlyRepo.save(yearly);
    }




    public Daily updateDaily(Long id, Daily daily, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Daily dailyToUpdate = dailyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Daily record not found"));

        if (!dailyToUpdate.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized to update this record");
        }

        dailyToUpdate.setAmount(daily.getAmount());
        dailyToUpdate.setSource(daily.getSource());
        dailyToUpdate.setDate(daily.getDate());
        dailyToUpdate.setDay(daily.getDay());

        Daily updatedDaily = dailyRepo.save(dailyToUpdate);
        updateUserBalance(currentUser, daily.getDate()); // Pass the actual date
        return updatedDaily;
    }



    public void deleteDaily(Long id, String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Daily dailyToDelete = dailyRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Daily record not found"));

        if (!dailyToDelete.getUser().equals(currentUser)) {
            throw new IllegalArgumentException("Unauthorized to delete this record");
        }

        dailyRepo.deleteById(id);
        updateUserBalance(currentUser, dailyToDelete.getDate()); // Pass the actual date
    }


    public double calculateBalance(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Balance balance = balanceRepo.findByUser(currentUser).orElse(new Balance());
        return balance.getAmount();
    }



}
