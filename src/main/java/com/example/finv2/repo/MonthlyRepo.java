package com.example.finv2.repo;

import com.example.finv2.model.Monthly;
import com.example.finv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MonthlyRepo extends JpaRepository<Monthly, Long> {
    List<Monthly> findAllByYearAndUser(String year, User user);
    List<Monthly> findAllByUser(User user);
    List<Monthly> findByMonthAndUser(String month, User user);
    Optional<Monthly> findByUserAndMonth(User user, String month); // ✅ Updated method

}