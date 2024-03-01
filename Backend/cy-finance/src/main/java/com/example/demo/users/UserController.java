package com.example.demo.users;

import com.example.demo.earnings.Earnings;
import com.example.demo.earnings.EarningsRepository;
import com.example.demo.expenses.Expenses;
import com.example.demo.expenses.ExpensesRepository;
import com.example.demo.netWorth.NetWorth;
import com.example.demo.netWorth.NetWorthRepository;
import com.example.demo.util.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@RestController
public class UserController {

    Response<User> users = new Response<>();

    @Autowired
    EarningsRepository earningsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NetWorthRepository netWorthRepository;

    @Autowired
    ExpensesRepository expensesRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(@CookieValue(name = "user-id", required = false) String userId){
        if (userId == null || userId.equals("") || !userRepository.findById(Integer.parseInt(userId)).getRole().equals("admin")) {
            return ResponseEntity.ok(null);
        } else {
            return ResponseEntity.ok(userRepository.findAll());
        }
    }

    @GetMapping("/users/{id}")
    public User getUser(@PathVariable int id){
        return userRepository.findById(id);
    }

    @PostMapping("/users")
    public ResponseEntity<Response<String>> setUser(@RequestBody User user){
        Response<String> response = new Response<>();
        if (user == null) {
            response.put("message", "No user specified");
            return ResponseEntity.ok(response);
        } else {
            userRepository.save(user);
            response.put("message", "User created");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestParam String email, @RequestParam String password){
        Response<String> response = new Response<>();

        User foundUser = userRepository.findByEmail(email);
        if (foundUser.getPassword().equals(password)){
            response.put("message", "success");
            ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(foundUser.getId()))
                    .maxAge(60)
                    .build();
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                    .body(response);
        } else {
            response.put("message", "failure");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout(){
        Response<String> response = new Response<>();
        response.put("message", "Successfully logged out");
        ResponseCookie springCookie = ResponseCookie
                .from("user-id", null)
                .build();
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, springCookie.toString())
                .body(response);
    }

    @PutMapping("/users")
    public ResponseEntity<Response<String>> changeUser(@RequestBody User user){
        Response<String> response = new Response<>();
        if (user == null) {
            response.put("message", "No user provided");
            return ResponseEntity.ok(response);
        } else {
            userRepository.save(user);
            response.put("message", "User modified");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/users/{userId}/earnings/{earningsId}")
    String assignEarningsToUser(@PathVariable int userId,@PathVariable int earningsId){
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        Earnings earnings = earningsRepository.findById(earningsId);
        if(user == null || earnings == null) {
            response.put("message", "Failed to assign earnings");
        } else {
            earnings.setUser(user);
            user.setEarnings(earnings);
            userRepository.save(user);
            response.put("message", "Earnings assigned to user");
        }
        return response.toString();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Response<String>> deleteUser(@PathVariable int id){
        Response<String> response = new Response<>();
        userRepository.deleteById(id);
        response.put("message", "User deleted");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/networth/{netWorthId}")
    public ResponseEntity<Response<String>> attachNetWorthToUser(@PathVariable int userId, @PathVariable int netWorthId) {
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        NetWorth netWorth = netWorthRepository.findById(netWorthId);
        if(user == null || netWorth == null) {
            response.put("message", "Failed to assign net worth");
            return ResponseEntity.ok(response);
        } else {
            netWorth.setUser(user);
            user.setNetWorth(netWorth);
            userRepository.save(user);
            response.put("message", "Net-worth assigned to user");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/users/{userId}/expenses/{expensesId}")
    String attachExpensesToUser(@PathVariable int userId, @PathVariable int expensesId) {
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        Expenses expenses = expensesRepository.findById(expensesId);
        if(user == null || expenses == null) {
            response.put("message", "Failed to assign expenses");
        } else {
            expenses.setUser(user);
            user.setExpenses(expenses);
            userRepository.save(user);
            response.put("message", "Expenses assigned");
        }
        return response.toString();
    }
}
