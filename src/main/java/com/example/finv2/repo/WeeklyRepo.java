package com.example.finv2.repo;

import com.example.finv2.model.Daily;
import com.example.finv2.model.User;
import com.example.finv2.model.Weekly;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface WeeklyRepo extends JpaRepository<Weekly, Long> {

    List<Weekly> findAllByUser(User user);
    List<Weekly> findAllByUserAndWeekBetween(User user, String startDate, String endDate);
    Optional<Weekly> findByIdAndUser(Long id, User user);
    List<Weekly> findAllByWeekBetween(String startDate, String endDate);
    List<Weekly> findByWeekAndUser(String week, User user);
    Optional<Weekly> findByUserAndWeek(User user, String week); // ✅ Updated method
    @Query("SELECT w FROM Weekly w LEFT JOIN FETCH w.dailies WHERE w.user = :user")
    List<Weekly> findAllByUserWithDailies(@Param("user") User user);

}