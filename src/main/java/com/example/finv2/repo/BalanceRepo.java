package com.example.finv2.repo;

import com.example.finv2.model.Balance;
import com.example.finv2.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface BalanceRepo extends JpaRepository<Balance, Long> {
    Optional<Balance> findByUser(User user);
//findAllByUser
    Optional<Balance> findAllByUser(User user);

}
