package com.example.demo.expenses;


import com.example.demo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ExpensesController {

    @Autowired
    ExpensesRepository expensesRepository;

    @GetMapping(path = "/expenses")
    List<Expenses> getExpenses() {
        return expensesRepository.findAll();
    }

    @GetMapping(path = "/expenses/{id}")
    Expenses getExpensesById(@PathVariable int id) {
        return expensesRepository.findById(id);
    }

    @PostMapping(path = "/expenses")
    String createExpenses(@RequestBody Expenses expenses) {
        Response<String> response = new Response<>();
        if (expenses == null) {
            response.put("message", "No expenses specified");
        } else {
            response.put("message", "expenses created");
            expensesRepository.save(expenses);
        }
        return response.toString();
    }

    @PutMapping(path = "/expenses")
    String modifyExpenses(@RequestBody Expenses expenses) {
        Response<String> response = new Response<>();
        if (expenses == null) {
            response.put("message", "No expenses provided");
        } else if (expensesRepository.findById(expenses.getId()) == null) {
            response.put("message", "Expenses doesn't exist");
        } else {
            if (expenses.getFood() == -1)
                expenses.setFood(expensesRepository.findById(expenses.getId()).getFood());
            if (expenses.getMisc() == -1)
                expenses.setMisc(expensesRepository.findById(expenses.getId()).getMisc());
            if (expenses.getSchool() == -1)
                expenses.setSchool(expensesRepository.findById(expenses.getId()).getSchool());
            if (expenses.getRentandBills() == -1)
                expenses.setRentandBills(expensesRepository.findById(expenses.getId()).getRentandBills());
            if (expenses.getOtherNeeds() == -1)
                expenses.setOtherNeeds(expensesRepository.findById(expenses.getId()).getOtherNeeds());
            expensesRepository.save(expenses);
            response.put("message", "Expenses modified");
        }
        return response.toString();
    }

    @DeleteMapping(path = "/expenses/{id}")
    String deleteExpenses(@PathVariable int id) {
        Response<String> response = new Response<>();
        expensesRepository.deleteById(id);
        response.put("message", "Expenses deleted");
        return response.toString();
    }
}
