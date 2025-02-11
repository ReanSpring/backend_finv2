package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Weekly;
import com.example.finv2.model.Yearly;
import com.example.finv2.service.WeeklyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weekly")
public class WeeklyController {
    private final WeeklyService weeklyService;

    public WeeklyController(WeeklyService weeklyService){
        this.weeklyService = weeklyService;
    }

    @GetMapping
    public ResponseDTO<List<Weekly>> getAllWeekly(@RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Get all yearly success", weeklyService.findAllWeekly(token), "200");
    }

    @PostMapping
    public ResponseDTO<Weekly> addWeekly(@RequestBody Weekly weekly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Add weekly success", weeklyService.createWeekly(weekly, token), "200");
    }

    @PutMapping("/{id}")
    public ResponseDTO<Weekly> updateWeekly(@PathVariable Long id, @RequestBody Weekly weekly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Update weekly success", weeklyService.updateWeekly(id, weekly, token), "200");
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Weekly> deleteWeekly(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        weeklyService.deleteWeekly(id, token);
        return new ResponseDTO<>("Delete weekly success", null, "200");
    }
}
