package com.example.finv2.repo;

import com.example.finv2.model.Monthly;
import com.example.finv2.model.User;
import com.example.finv2.model.Yearly;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;

public interface YearlyRepo extends JpaRepository<Yearly, Long> {
    List<Yearly> findAllByUser(User user);
    List<Yearly> findByYearAndUser(String year, User user);
    Optional<Yearly> findByUserAndYear(User user, String year); // ✅ Updated method

}