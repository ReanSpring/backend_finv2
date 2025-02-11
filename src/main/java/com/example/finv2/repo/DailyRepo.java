package com.example.finv2.repo;

import com.example.finv2.model.Daily;
import com.example.finv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DailyRepo extends JpaRepository<Daily, Long> {
    List<Daily> findAllByUser(User user);
    Optional<Daily> findByIdAndUser(Long id, User user);
    List<Daily> findByDateAndUser(LocalDate date, User user);
    List<Daily> findAllByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    List<Daily> findByDateAndDayAndUser(LocalDate date, String day, User user);
}