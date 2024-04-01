package com.example.demo.users;

import com.example.demo.earnings.Earnings;
import com.example.demo.earnings.EarningsRepository;
import com.example.demo.expenses.Expenses;
import com.example.demo.expenses.ExpensesRepository;
import com.example.demo.userGroups.Groups;
import com.example.demo.userGroups.GroupsRepository;
import com.example.demo.netWorth.NetWorth;
import com.example.demo.netWorth.NetWorthRepository;
import com.example.demo.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    EarningsRepository earningsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    NetWorthRepository netWorthRepository;

    @Autowired
    ExpensesRepository expensesRepository;

    @Autowired
    GroupsRepository groupsRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(
            @CookieValue(name = "user-id", required = false) String userId) {
        logger.info("[GET /users] Cookie: " + userId);
        if (isValidUserId(userId) && isAdmin(userId)) {
            logger.info("[GET /users] Successfully accessed data by user: " + userRepository.findById(Integer.parseInt(userId)).getName());
            return ResponseEntity.ok(userRepository.findAll());
        } else {
            if (isValidUserId(userId))
                logger.info("[GET /users] Role: " + userRepository.findById(Integer.parseInt(userId)).getRole());
            logger.warn("[GET /users] Attempted access from invalid user");
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id,
                                        @CookieValue(name = "user-id", required = false) String userId) {
        logger.info("[GET /users/{id}] Cookie: " + userId);
        // TODO: Find out how to make sure the empty cookie request is only for signup
        // Only return user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || id == Integer.parseInt(userId))) {
            logger.info("[GET /users/{id}] Successfully accessed data by user: " + userRepository.findById(Integer.parseInt(userId)).getName());
            return ResponseEntity.ok(userRepository.findById(id));
        } else {
            logger.warn("[GET /users/{id}] Attempted access from invalid user");
            return ResponseEntity.ok(null);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Response<String>> setUser(@RequestBody User user,
                                                    @CookieValue(name = "user-id", required = false) String userId) {
        User createdUser;

        logger.info("[POST /users] Cookie: " + userId);
        logger.info("[POST /users] User: " + user);
        Response<String> response = new Response<>();
        // Able to create user if not logged in (for signup) or admin
        if (userId == null || (isValidUserId(userId) && isAdmin(userId))) {
            if (user == null) {
                logger.warn("[POST /users] No user provided");
                response.put("message", "No user provided");
            } else if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.warn("[POST /users] User: " + user.getEmail() + " already exists");
                response.put("message", "User already exists");
            } else {
                userRepository.save(user);
                createdUser = userRepository.findByEmail(user.getEmail());
                logger.info("[POST /users] User created: " + user);

                ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(createdUser.getId()))
                        .maxAge(60)
                        .build();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(HttpHeaders.SET_COOKIE, springCookie.toString());

                response.put("message", "User created");

                return ResponseEntity.ok().headers(responseHeaders).body(response);
            }
        } else {
            logger.warn("[POST /users] Attempted access from invalid user");
            response.put("message", "User creation not allowed");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestParam String email, @RequestParam String password) {
        Response<String> response = new Response<>();

        User foundUser = userRepository.findByEmail(email);
        if (foundUser != null && foundUser.getPassword().equals(password)) {
            ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(foundUser.getId()))
                    .maxAge(60)
                    .build();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.SET_COOKIE, springCookie.toString());

            response.put("message", "success");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(response);
        } else {
            response.put("message", "failure");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout() {
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
    public ResponseEntity<Response<String>> changeUser(@RequestBody User user,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();

        logger.info("[PUT /users] Cookie: " + userId);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || user.getId() == Integer.parseInt(userId))) {
            if (user == null) {
                logger.warn("[PUT /users] User not provided");
                response.put("message", "No user provided");
            } else {
                userRepository.save(user);
                logger.info("[PUT /users] User " + user.getName() + " modified by " + userRepository.findById(Integer.parseInt(userId)).getEmail());
                response.put("message", "User modified");
            }
        } else {
            logger.warn("[PUT /users] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/earnings/{earningsId}")
    String assignEarningsToUser(@PathVariable int userId, @PathVariable int earningsId) {
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        Earnings earnings = earningsRepository.findById(earningsId);
        if (user == null || earnings == null) {
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
    public ResponseEntity<Response<String>> deleteUser(@PathVariable int id,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();
        if (isValidUserId(userId) && (isAdmin(userId) || id == Integer.parseInt(userId))) {
            userRepository.deleteById(id);
            logger.info("[DELETE /users] Entry for userId " + id + " deleted by userId " + userId);
            response.put("message", "User deleted");
        } else {
            logger.warn("[DELETE /users] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/networth/{netWorthId}")
    public ResponseEntity<Response<String>> attachNetWorthToUser(@PathVariable int userId, @PathVariable int netWorthId) {
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        NetWorth netWorth = netWorthRepository.findById(netWorthId);
        if (user == null || netWorth == null) {
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
        if (user == null || expenses == null) {
            response.put("message", "Failed to assign expenses");
        } else {
            expenses.setUser(user);
            user.setExpenses(expenses);
            userRepository.save(user);
            response.put("message", "Expenses assigned");
        }
        return response.toString();
    }

    @PostMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<Response<String>> attachGroupsToUser(@PathVariable int userId, @PathVariable int groupId) {
        Response<String> response = new Response<>();
        User user = userRepository.findById(userId);
        Groups group = groupsRepository.findById(groupId);
        if (user == null || group == null) {
            response.put("message", "Failed to assign group");
        } else {
            group.addUser(user);
            user.addGroups(group);
            userRepository.save(user);
            response.put("message", "Expenses assigned");
        }
        return ResponseEntity.ok(response);
    }

    private boolean isValidId(String userId) {
        return userId != null && !userId.isEmpty();
    }

    private boolean isValidUserId(String userId) {
        return isValidId(userId) && userRepository.findById(Integer.parseInt(userId)) != null;
    }

    private boolean isAdmin(String userId) {
        return userRepository.findById(Integer.parseInt(userId)).getRole().equals("admin");
    }
}
