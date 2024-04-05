package com.example.demo.users;

import com.example.demo.assets.AssetsRepository;
import com.example.demo.earnings.Earnings;
import com.example.demo.earnings.EarningsRepository;
import com.example.demo.expenses.Expenses;
import com.example.demo.expenses.ExpensesRepository;
import com.example.demo.assets.Assets;
import com.example.demo.userGroups.Groups;
import com.example.demo.userGroups.GroupsRepository;
import com.example.demo.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    EarningsRepository earningsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetsRepository assetsRepository;

    @Autowired
    ExpensesRepository expensesRepository;

    @Autowired
    GroupsRepository groupsRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(
            @CookieValue(name = "user-id", required = false) String userId) {
        logger.info("[GET /users] Cookie: " + userId);
        if (isValidUserId(userId) && isAdmin(userId)) {
            logger.info("[GET /users] Successfully accessed data by user: " + userRepository.findByEmail(userId).getName());
            return ResponseEntity.ok(userRepository.findAll());
        } else {
            if (isValidUserId(userId))
                logger.info("[GET /users] Role: " + userRepository.findByEmail(userId).getRole());
            logger.warn("[GET /users] Attempted access from invalid user");
            return ResponseEntity.ok(null);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id,
                                        @CookieValue(name = "user-id", required = false) String userId) {
        logger.info("[GET /users/{id}] Cookie: " + userId);
        // TODO: Find out how to make sure the empty cookie request is only for signup
        // Only return user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || id.equals(userId))) {
            logger.info("[GET /users/{id}] Successfully accessed data by user: " + userId);
            return ResponseEntity.ok(userRepository.findByEmail(id));
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
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                userRepository.save(user);
                createdUser = userRepository.findByEmail(user.getEmail());
                logger.info("[POST /users] User created: " + user);

                ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(createdUser.getEmail()))
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
        if (foundUser != null && BCrypt.checkpw(password, foundUser.getPassword())) {
            ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(foundUser.getEmail()))
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
        if (isValidUserId(userId) && (isAdmin(userId) || user.getEmail().equals(userId))) {
            if (user == null) {
                logger.warn("[PUT /users] User not provided");
                response.put("message", "No user provided");
            } else {
                User originalUser = userRepository.findByEmail(user.getEmail());
                // TODO: Check if user cookie is admin before allowing change to admin role
                // Check fields
                if (user.getName() == null) user.setName(originalUser.getName());
                if (user.getRole() == null) user.setRole(originalUser.getRole());
                if (user.getPassword() == null) {
                    user.setPassword(originalUser.getPassword());
                } else {
                    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                }
                user.setLiabilitiesTotal(originalUser.getLiabilitiesTotal());
                user.setAssetsTotal(originalUser.getAssetsTotal());
                user.setNetWorth(originalUser.getNetWorth());
                userRepository.save(user);
                logger.info("[PUT /users] User " + user.getName() + " modified by " + userId);
                response.put("message", "User modified");
            }
        } else {
            logger.warn("[PUT /users] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userEmail}/assettotal/{newTotal}")
    public ResponseEntity<Response<String>> changeUserAssetTotal(@PathVariable String userEmail,
                                                       @PathVariable int newTotal,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();

        logger.info("[PUT /users/{userEmail}/assettotal/{newTotal}] Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            if (user == null) {
                logger.warn("[PUT /users/{userEmail}/assettotal/{newTotal}] User not provided");
                response.put("message", "No user provided");
            } else {
                user.setAssetsTotal(newTotal);
                userRepository.save(user);
                logger.info("[PUT /users/{userEmail}/assettotal/{newTotal}] User " + user.getName() + " modified by " + userId);
                response.put("message", "User modified");
            }
        } else {
            logger.warn("[PUT /users/{userEmail}/assettotal/{newTotal}] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userEmail}/liabilitiestotal/{newTotal}")
    public ResponseEntity<Response<String>> changeUserLiabilitiesTotal(@PathVariable String userEmail,
                                                                 @PathVariable int newTotal,
                                                                 @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();

        logger.info("[PUT /users/{userEmail}/liabilitiestotal/{newTotal}] Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            if (user == null) {
                logger.warn("[PUT /users/{userEmail}/liabilitiestotal/{newTotal}] User not provided");
                response.put("message", "No user provided");
            } else {
                user.setLiabilitiesTotal(newTotal);
                userRepository.save(user);
                logger.info("[PUT /users/{userEmail}/liabilitiestotal/{newTotal}] User " + user.getName() + " modified by " + userId);
                response.put("message", "User modified");
            }
        } else {
            logger.warn("[PUT /users/{userEmail}/liabilitiestotal/{newTotal}] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping("/users/{userEmail}/networth/{newTotal}")
    public ResponseEntity<Response<String>> changeUserNetWorth(@PathVariable String userEmail,
                                                                       @PathVariable int newTotal,
                                                                       @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();

        logger.info("[PUT /users/{userEmail}/networth/{newTotal}] Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            if (user == null) {
                logger.warn("[PUT /users/{userEmail}/networth/{newTotal}] User not provided");
                response.put("message", "No user provided");
            } else {
                user.setNetWorth(newTotal);
                userRepository.save(user);
                logger.info("[PUT /users/{userEmail}/networth/{newTotal}] User " + user.getName() + " modified by " + userId);
                response.put("message", "User modified");
            }
        } else {
            logger.warn("[PUT /users/{userEmail}/networth/{newTotal}] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Response<String>> deleteUser(@PathVariable String id,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        Response<String> response = new Response<>();
        if (isValidUserId(userId) && (isAdmin(userId) || id.equals(userId))) {
            userRepository.deleteByEmail(id);
            logger.info("[DELETE /users] Entry for userId " + id + " deleted by userId " + userId);
            response.put("message", "User deleted");
        } else {
            logger.warn("[DELETE /users] Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
        }
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users/{userId}/assets")
    public ResponseEntity<Response<String>> addUserAssets(@PathVariable String userId, @RequestBody Assets assets) {
        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            response.put("message", "Failed to assign asset");
            return ResponseEntity.ok(response);
        } else {
            assets.setUser(user);
            assetsRepository.save(assets);
            user.addAsset(assets);
            user.setNetWorth(user.getNetWorth() + assets.getAmount());
            userRepository.save(user);
            response.put("message", "Asset assigned to user");
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/users/{userId}/assets")
    public ResponseEntity<Response<String>> editUserAssets(@PathVariable String userId,
                                                          @RequestBody Assets assets) {
        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Assets originalAsset = assetsRepository.findAssetsById(assets.getId());
        if (user == null || (!userId.equals(originalAsset.getUser().getEmail()))) {
            response.put("message", "Failed to edit asset");
            return ResponseEntity.ok(response);
        } else {
            user.setNetWorth(user.getNetWorth() - originalAsset.getAmount() + assets.getAmount());
            assets.setUser(user);
            assetsRepository.save(assets);
            userRepository.save(user);
            response.put("message", "Asset updated");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/users/{userId}/assets/{assetId}")
    public ResponseEntity<Response<String>> removeUserAssets(@PathVariable String userId, @PathVariable int assetId) {
        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Assets asset = assetsRepository.findAssetsById(assetId);
        if (user == null || asset == null) {
            response.put("message", "Failed to delete asset");
            return ResponseEntity.ok(response);
        } else {
            assetsRepository.deleteAssetsById(assetId);
            user.removeAsset(asset);
            user.setNetWorth(user.getNetWorth() - asset.getAmount());
            userRepository.save(user);
            response.put("message", "Asset deleted");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/users/{userId}/earnings/{earningsId}")
    public ResponseEntity<?> assignEarningsToUser(@PathVariable String userId, @PathVariable Long earningsId) {
        // Attempt to retrieve a User entity by their email. The findByEmail method is expected to
        // search the database for a User with the specified email (userId in the path variable).
        User user = userRepository.findByEmail(userId);

        // Check if the user was not found. If the user variable is null, it means there is no
        // user in the database with the provided email. In such a case, prepare an error response.
        if (user == null) {
            Map<String, String> errorResponse = new HashMap<>();
            // Populate the error response with a message indicating the user was not found.
            errorResponse.put("message", "User not found with email: " + userId);
            // Return a ResponseEntity with a 400 Bad Request status, including the error message.
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Attempt to retrieve an Earnings entity by its ID. The findById method returns an Optional,
        // which will be empty if no Earnings entity matches the provided ID. The use of orElseThrow
        // here means that if the Earnings entity is not found, a ResponseStatusException will be
        // thrown automatically, which Spring will handle by returning a 404 Not Found response.
        Earnings earnings = earningsRepository.findById(earningsId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Earnings not found with id: " + earningsId));

        // Associate the found User entity with the Earnings entity. This operation links the Earnings
        // to the User, establishing the many-to-one relationship from Earnings to User.
        earnings.setUser(user);

        // Save the updated Earnings entity to the database. This persist operation includes saving
        // the modification made to the Earnings entity (setting the user). As a result of this save,
        // the association between the User and Earnings entities will be reflected in the database.
        earningsRepository.save(earnings);

        // Prepare a success response indicating the earnings have been successfully assigned to the user.
        Map<String, String> successResponse = new HashMap<>();
        successResponse.put("message", "Earnings assigned to user successfully.");

        // Return a ResponseEntity with a 200 OK status, including the success message.
        return ResponseEntity.ok(successResponse);
    }



    @PostMapping("/users/{userId}/expenses/{expensesId}")
    public ResponseEntity<?> attachExpensesToUser(@PathVariable String userId, @PathVariable int expensesId) {
        // Assuming userRepository and expensesRepository have been appropriately autowired
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            // Handle case where the user is not found
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        Expenses expenses = expensesRepository.findById(expensesId);
        if (expenses == null) {
            // Handle case where the expenses are not found
            return ResponseEntity.badRequest().body("Expenses not found with ID: " + expensesId);
        }

        // Ensure the user's expenses collection is initialized
        if (user.getExpenses() == null) {
            user.setExpenses(new HashSet<>());
        }

        // Add the expense to the user's collection of expenses and set the back reference
        expenses.setUser(user);
        user.getExpenses().add(expenses);

        // Assuming there's cascading or explicit saving necessary
        expensesRepository.save(expenses); // Save the expenses entity to update its user reference

        return ResponseEntity.ok("Expenses assigned successfully to user.");
    }



    @PostMapping("/users/{userId}/groups/{groupId}")
    public ResponseEntity<Response<String>> attachGroupsToUser(@PathVariable String userId, @PathVariable int groupId) {
        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
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
        return isValidId(userId) && userRepository.findByEmail(userId) != null;
    }

    private boolean isAdmin(String userId) {
        return userRepository.findByEmail(userId).getRole().equals("admin");
    }
}
