package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Yearly;
import com.example.finv2.service.YearlyService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/yearly")
public class YearlyController {
    private final YearlyService yearlyService;

    public YearlyController(YearlyService yearlyService) {
        this.yearlyService = yearlyService;
    }

    @GetMapping
    public ResponseDTO<List<Yearly>> getAllYearly(@RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Get all yearly success", yearlyService.findAllYearly(token), "200");
    }

    @PostMapping
    public ResponseDTO<Yearly> addYearly(@RequestBody Yearly yearly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Add yearly success", yearlyService.createYearly(yearly, token), "200");
    }

    @PutMapping("/{id}")
    public ResponseDTO<Yearly> updateYearly(@PathVariable Long id, @RequestBody Yearly yearly, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Update yearly success", yearlyService.updateYearly(id, yearly, token), "200");
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Yearly> deleteYearly(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        yearlyService.deleteYearly(id, token);
        return new ResponseDTO<>("Delete yearly success", null, "200");
    }
}