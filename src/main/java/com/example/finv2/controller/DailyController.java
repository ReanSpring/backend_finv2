package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Daily;
import com.example.finv2.service.DailyService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/daily")
public class DailyController {
    private final DailyService dailyService;

    public DailyController(DailyService dailyService) {
        this.dailyService = dailyService;
    }

    @GetMapping
    public ResponseDTO<Page<Daily>> getAllDaily(@RequestHeader("Authorization") String token,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return new ResponseDTO<>("Get all daily success", dailyService.findAllDaily(token, page, size), "200");
    }

    @GetMapping("/weekly")
    public ResponseDTO<List<Daily>> getAllWeeklyWithDailies(@RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Get all weekly with dailies success", dailyService.findAllWeeklyWithDailies(token), "200");
    }

    @PostMapping
    public ResponseDTO<Daily> addDaily(@RequestBody Daily daily, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Add daily success", dailyService.createDaily(daily, token), "200");
    }

    @PutMapping("/{id}")
    public ResponseDTO<Daily> updateDaily(@PathVariable Long id, @RequestBody Daily daily, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Update daily success", dailyService.updateDaily(id, daily, token), "200");
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Daily> deleteDaily(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        dailyService.deleteDaily(id, token);
        return new ResponseDTO<>("Delete daily success", null, "200");
    }

}
