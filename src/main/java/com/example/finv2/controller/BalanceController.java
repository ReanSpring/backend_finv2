package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Balance;
import com.example.finv2.service.BalanceService;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/balance")
public class BalanceController {
    private final BalanceService balanceService;

    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @GetMapping
    public ResponseDTO<List<Balance>> getAllDaily(@RequestHeader("Authorization") String token){
        return new ResponseDTO<>("Get all daily success", balanceService.findAllBalance(token), "200");
    }

    @PostMapping
    public ResponseDTO<Balance> addDaily(@RequestBody Balance balance, @RequestHeader("Authorization") String token){
        return new ResponseDTO<>("Add daily success", balanceService.createBalance(balance, token), "200");
    }
}