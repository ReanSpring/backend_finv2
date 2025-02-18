package com.example.finv2.repo;

import com.example.finv2.model.Income;
import com.example.finv2.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IncomeRepo extends JpaRepository<Income, Long> {
    Optional<Income> findIncomeById(Long id);
    Optional<Income> findIncomeByAmount(Double amount);
    List<Income> findByUser(User user);
    Page<Income> findByUser(User user, Pageable pageable);

}