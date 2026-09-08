package com.example.budgetbook.controller;

import com.example.budgetbook.record.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class SummaryController {

    private final RecordService recordService;

    public SummaryController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/summary")
    public String summary(Model model) {

        model.addAttribute("monthlyExpenses", recordService.getMonthlyExpense());
        model.addAttribute("dailyExpenses", recordService.getDailyExpense());
        model.addAttribute("monthlyIncome",recordService.getCurrentMonthIncome());
        model.addAttribute("monthlyExpense",recordService.getCurrentMonthExpense());
        model.addAttribute("monthlyBalance",recordService.getCurrentMonthBalance());
        model.addAttribute("categoryExpenses",recordService.getCurrentMonthCategoryExpenses());
        return "summary";
    }
}
