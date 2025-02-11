package com.example.finv2.service;

import com.example.finv2.model.testing;
import com.example.finv2.repo.testingrepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class testingservice {

    private final testingrepo testingRepo;

    public testingservice(testingrepo testingRepo) {
        this.testingRepo = testingRepo;
    }

    public List<testing> findAllTesting() {
        return testingRepo.findAll();
    }
}