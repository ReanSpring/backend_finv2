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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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

    public Page<Daily> findAllDaily(String token, int page, int size) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "date"));
        return dailyRepo.findAllByUser(currentUser, pageable);
    }

    public List<Weekly> findAllWeeklyWithDailies(String token) {
        String username = jwtUtil.extractUsername(token.substring(7));
        User currentUser = userRepo.findUserByEmail(username).orElse(null);
        if (currentUser != null) {
            return weeklyRepo.findAllByUserWithDailies(currentUser);
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

        // Ensure Weekly instance is saved
        int weekOfYear = daily.getDate().get(WeekFields.ISO.weekOfWeekBasedYear());
        Weekly weekly = weeklyRepo.findByUserAndWeek(currentUser, String.valueOf(weekOfYear))
                .orElseGet(() -> new Weekly(currentUser, String.valueOf(weekOfYear)));
        weeklyRepo.save(weekly);
        daily.setWeekly(weekly);

        // Ensure Yearly instance is saved
        Yearly yearly = yearlyRepo.findByUserAndYear(currentUser, String.valueOf(daily.getDate().getYear()))
                .orElseGet(() -> new Yearly(currentUser, String.valueOf(daily.getDate().getYear())));
        yearlyRepo.save(yearly);
        daily.setYearly(yearly);

        // Ensure Monthly instance is saved
        YearMonth currentMonth = YearMonth.from(daily.getDate());
        Monthly monthly = monthlyRepo.findByUserAndMonth(currentUser, currentMonth.toString())
                .orElseGet(() -> new Monthly(currentUser, currentMonth.toString(), String.valueOf(daily.getDate().getYear())));
        monthly.setYearly(yearly); // Set the yearly instance in the monthly instance
        monthlyRepo.save(monthly);
        daily.setMonthly(monthly);

        // Update yearly amount
        List<Monthly> monthlyRecords = monthlyRepo.findAllByYearAndUser(String.valueOf(daily.getDate().getYear()), currentUser);
        double totalMonthlyIncome = monthlyRecords.stream()
                .mapToDouble(Monthly::getAmount).sum();
        yearly.setAmount(totalMonthlyIncome);
        yearlyRepo.save(yearly);

        // Check if a record with the same date already exists
        List<Daily> existingDailies = dailyRepo.findByDateAndUser(daily.getDate(), currentUser);
        if (!existingDailies.isEmpty()) {
            Daily existingDaily = existingDailies.get(0); // Assume only one record per day
            existingDaily.setAmount(existingDaily.getAmount() + daily.getAmount());
            existingDaily.setSource(existingDaily.getSource() + "," + daily.getSource());

            Daily updatedDaily = dailyRepo.save(existingDaily);
            updateUserBalance(currentUser, daily.getDate()); // Pass the actual date
            return updatedDaily;
        }

        Daily savedDaily = dailyRepo.save(daily);
        updateUserBalance(currentUser, daily.getDate()); // Pass the actual date

        // Subtract the daily amount from the user's balance
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(currentUser);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;
        if (currentTotal >= daily.getAmount()) {
            currentTotal -= daily.getAmount();
            Balance newBalance = new Balance();
            newBalance.setUser(currentUser);
            newBalance.setDate(LocalDate.now());
            newBalance.setAmount(daily.getAmount());
            newBalance.setTotal(currentTotal);
            newBalance.setReason(daily.getSource());
            newBalance.setType(Balance.Type.SUBTRACT);
            balanceRepo.save(newBalance);
        } else {
            throw new IllegalArgumentException("Insufficient balance to subtract.");
        }

        return savedDaily;
    }


    private void updateUserBalance(User user, LocalDate dailyDate) {
        // Get the year and month from the daily record date
        YearMonth currentMonth = YearMonth.from(dailyDate);
        int currentYear = dailyDate.getYear();

        // Aggregate Daily Income for the month
        List<Daily> dailyRecords = dailyRepo.findAllByUserAndDateBetween(
                user, currentMonth.atDay(1), currentMonth.atEndOfMonth());

        double totalDailyIncome = dailyRecords.stream()
                .mapToDouble(Daily::getAmount).sum();

        // Aggregate Weekly Data
        int weekOfYear = dailyDate.get(WeekFields.ISO.weekOfWeekBasedYear());

        List<Daily> weeklyDailyRecords = dailyRepo.findAllByUserAndDateBetween(
                user, dailyDate.with(WeekFields.ISO.dayOfWeek(), 1),
                dailyDate.with(WeekFields.ISO.dayOfWeek(), 7));

        Weekly weekly = weeklyRepo.findByUserAndWeek(user, String.valueOf(weekOfYear))
                .orElseGet(() -> new Weekly(user, String.valueOf(weekOfYear)));

        weekly.setAmount(weeklyDailyRecords.stream().mapToDouble(Daily::getAmount).sum());
        weekly.setDailies(weeklyDailyRecords);  // Ensure Weekly has a list of Daily records
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

        double oldAmount = dailyToUpdate.getAmount();
        double newAmount = daily.getAmount();

        if (newAmount != oldAmount) {
            double difference = newAmount - oldAmount;
            updateBalance(currentUser, difference);
        }

        dailyToUpdate.setAmount(newAmount);
        dailyToUpdate.setSource(daily.getSource());
        dailyToUpdate.setDate(daily.getDate());
        dailyToUpdate.setDay(daily.getDay());

        Daily updatedDaily = dailyRepo.save(dailyToUpdate);
        updateUserBalance(currentUser, daily.getDate()); // Pass the actual date

        return updatedDaily;
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
        newBalance.setReason("Daily update");
        newBalance.setType(amount > 0 ? Balance.Type.ADD : Balance.Type.SUBTRACT);
        balanceRepo.save(newBalance);
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

        // Subtract the daily amount from the user's balance
        Balance latestBalance = balanceRepo.findLatestBalanceByUser(currentUser);
        double currentTotal = (latestBalance != null) ? latestBalance.getTotal() : 0.0;
        double dailyAmount = dailyToDelete.getAmount();
        if (currentTotal >= dailyAmount) {
            currentTotal -= dailyAmount;
            Balance newBalance = new Balance();
            newBalance.setUser(currentUser);
            newBalance.setDate(LocalDate.now());
            newBalance.setAmount(dailyAmount);
            newBalance.setTotal(currentTotal);
            newBalance.setReason("Daily deletion");
            newBalance.setType(Balance.Type.SUBTRACT);
            balanceRepo.save(newBalance);
        } else {
            throw new IllegalArgumentException("Insufficient balance to subtract.");
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
