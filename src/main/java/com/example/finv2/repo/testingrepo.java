package com.example.finv2.repo;

import com.example.finv2.model.testing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface testingrepo extends JpaRepository<testing, Long> {
}