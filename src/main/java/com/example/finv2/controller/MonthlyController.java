package com.example.finv2.controller;


import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Monthly;
import com.example.finv2.service.MonthlyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/monthly")
public class MonthlyController {
    private final MonthlyService monthlyService;

    public MonthlyController(MonthlyService monthlyService) {
        this.monthlyService = monthlyService;
    }

    @GetMapping
    public ResponseDTO<List<Monthly>> getAllMonthly(@RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Get all monthly success", monthlyService.findAllMonthly(token), "200");
    }

    @PostMapping
    public ResponseDTO<Monthly> addMonthly(@RequestBody Monthly monthly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Add monthly success", monthlyService.createMonthly(monthly, token), "200");
    }

    @PutMapping("/{id}")
    public ResponseDTO<Monthly> updateMonthly(@PathVariable Long id, @RequestBody Monthly monthly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Update monthly success", monthlyService.updateMonthly(id, monthly, token), "200");
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Monthly> deleteMonthly(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        monthlyService.deleteMonthly(id, token);
        return new ResponseDTO<>("Delete monthly success", null, "200");
    }

}
