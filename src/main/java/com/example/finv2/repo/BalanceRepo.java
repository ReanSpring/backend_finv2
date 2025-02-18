package com.example.finv2.repo;

import com.example.finv2.model.Balance;
import com.example.finv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BalanceRepo extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUser(User user);
    List<Balance> findAllByUser(User user);
    Optional<Balance> findByUserAndDate(User user, LocalDateTime date);
    @Query("SELECT b FROM Balance b WHERE b.user = :user ORDER BY b.id DESC LIMIT 1")
    Balance findLatestBalanceByUser(@Param("user") User user);

}