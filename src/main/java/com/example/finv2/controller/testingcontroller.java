package com.example.finv2.controller;

import com.example.finv2.service.testingservice;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/testing")
public class testingcontroller {

    private final testingservice testingService;

    public testingcontroller(testingservice testingService) {
        this.testingService = testingService;
    }

    @GetMapping
    public ResponseEntity<?> getAllTesting() {
        return ResponseEntity.ok(testingService.findAllTesting());
    }
}