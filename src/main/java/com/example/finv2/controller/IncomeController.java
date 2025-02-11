package com.example.finv2.controller;

import com.example.finv2.dto.ResponseDTO;
import com.example.finv2.model.Income;
import com.example.finv2.service.IncomeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/income")
public class IncomeController {
    private final IncomeService incomeService;

    public IncomeController(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @GetMapping
    public ResponseDTO<Income> getAllIncomes(@RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Success", incomeService.findAllIncome(token), "200");
    }

    @PostMapping
    public ResponseDTO<Income> addIncome(@RequestBody Income income, @RequestHeader("Authorization") String token) {
        return new ResponseDTO<>("Success", incomeService.createIncome(income, token), "200");
    }

    @PutMapping("/{id}")
    public ResponseDTO<Income> updateIncome(@PathVariable Long id, @RequestBody Income income, @RequestHeader("Authorization") String token) {
        income.setId(id);
        return new ResponseDTO<>("Success", incomeService.updateIncome(income, token), "200");
    }

    @DeleteMapping("/{id}")
    public ResponseDTO<Income> deleteIncome(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        incomeService.deleteIncome(id, token);
        return new ResponseDTO<>("Success", null, "200");
    }
}
