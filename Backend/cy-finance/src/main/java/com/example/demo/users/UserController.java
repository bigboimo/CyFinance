package com.example.demo.users;

import com.example.demo.assets.AssetsRepository;
import com.example.demo.earnings.Earnings;
import com.example.demo.earnings.EarningsRepository;
import com.example.demo.expenses.Expenses;
import com.example.demo.expenses.ExpensesRepository;
import com.example.demo.assets.Assets;
import com.example.demo.liabilities.Liabilities;
import com.example.demo.liabilities.LiabilitiesRepository;
import com.example.demo.receipts.Receipts;
import com.example.demo.receipts.ReceiptsRepository;
import com.example.demo.userGroups.Groups;
import com.example.demo.userGroups.GroupsRepository;
import com.example.demo.util.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.*;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Value("${file-storage-directory}")
    private String directory;

    @Autowired
    EarningsRepository earningsRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    AssetsRepository assetsRepository;

    @Autowired
    LiabilitiesRepository liabilitiesRepository;

    @Autowired
    ExpensesRepository expensesRepository;

    @Autowired
    GroupsRepository groupsRepository;

    @Autowired
    ReceiptsRepository receiptsRepository;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers(
            @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[GET /users] ";

        logger.info(endpointString + "Cookie: " + userId);
        if (isValidUserId(userId) && isAdmin(userId)) {
            logger.info(endpointString + "Successfully accessed data by user: " + userRepository.findByEmail(userId).getName());
            return ResponseEntity.ok(userRepository.findAll());
        } else {
            if (isValidUserId(userId))
                logger.info(endpointString + "Role: " + userRepository.findByEmail(userId).getRole());
            logger.warn(endpointString + "Attempted access from invalid user");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable String id,
                                        @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[GET /users/{id}] ";

        logger.info(endpointString + "Cookie: " + userId);
        // TODO: Find out how to make sure the empty cookie request is only for signup
        // Only return user if the cookie is set and the user is either an admin or the requested user
        if (!isValidUserId(id)) {
            logger.warn(endpointString + "Attempted access for invalid user: " + id);
            return ResponseEntity.badRequest().body(null);
        } else if (isValidUserId(userId) && (isAdmin(userId) || id.equals(userId))) {
            logger.info(endpointString + "Successfully accessed data by user: " + userId);
            return ResponseEntity.ok(userRepository.findByEmail(id));
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PostMapping("/users")
    public ResponseEntity<Response<String>> setUser(@RequestBody User user,
                                                    @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[POST /users] ";

        User createdUser;

        logger.info(endpointString + "Cookie: " + userId);
        logger.info(endpointString + "User: " + user);
        Response<String> response = new Response<>();
        // Able to create user if not logged in (for signup) or admin
        if (userId == null || (isValidUserId(userId) && isAdmin(userId))) {
            if (user == null) {
                logger.warn(endpointString + "No user provided");
                response.put("message", "No user provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.warn(endpointString + "User: " + user.getEmail() + " already exists");
                response.put("message", "User already exists");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                userRepository.save(user);
                createdUser = userRepository.findByEmail(user.getEmail());
                logger.info(endpointString + "User created: " + createdUser);

                ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(createdUser.getEmail()))
                        .maxAge(60)
                        .build();
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.set(HttpHeaders.SET_COOKIE, springCookie.toString());

                response.put("message", "User created");

                return ResponseEntity.created(URI.create("/users/" + createdUser.getEmail())).headers(responseHeaders).body(response);
            }
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User creation not allowed");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Response<String>> login(@RequestParam String email, @RequestParam String password) {
        String endpointString = "[POST /login] ";

        Response<String> response = new Response<>();

        User foundUser = userRepository.findByEmail(email);
        if (foundUser != null && BCrypt.checkpw(password, foundUser.getPassword())) {
            ResponseCookie springCookie = ResponseCookie.from("user-id", String.valueOf(foundUser.getEmail()))
                    .maxAge(60)
                    .build();
            HttpHeaders responseHeaders = new HttpHeaders();
            responseHeaders.set(HttpHeaders.SET_COOKIE, springCookie.toString());

            logger.info(endpointString + "Login successful for user: " + email);
            response.put("message", "success");

            return ResponseEntity.ok()
                    .headers(responseHeaders)
                    .body(response);
        } else {
            logger.warn(endpointString + "Login failure for user: " + email);
            response.put("message", "failure");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Response<String>> logout() {
        String endpointString = "[POST /logout] ";

        Response<String> response = new Response<>();
        response.put("message", "Successfully logged out");
        logger.info(endpointString + "User logged out");
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
        String endpointString = "[PUT /users] ";

        Response<String> response = new Response<>();

        logger.info(endpointString + "Cookie: " + userId);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (user == null) {
            logger.warn(endpointString + "User not provided");
            response.put("message", "No user provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || user.getEmail().equals(userId))) {
            User originalUser = userRepository.findByEmail(user.getEmail());
            if (originalUser == null) {
                logger.warn(endpointString + "User not provided");
                response.put("message", "No user provided");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
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
            logger.info(endpointString + "User " + user.getName() + " modified by " + userId);
            response.put("message", "User modified");
            return ResponseEntity.ok(response);
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PutMapping("/users/{userEmail}/assettotal/{newTotal}")
    public ResponseEntity<Response<String>> changeUserAssetTotal(@PathVariable String userEmail,
                                                       @PathVariable int newTotal,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[PUT /users/" + userEmail + "/assettotal/" + newTotal + "] ";

        Response<String> response = new Response<>();

        logger.info(endpointString + "Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (user == null) {
            logger.warn(endpointString + "User not provided");
            response.put("message", "No user provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            user.setAssetsTotal(newTotal);
            userRepository.save(user);
            logger.info(endpointString + "User " + user.getName() + " modified by " + userId);
            response.put("message", "User modified");
            return ResponseEntity.ok(response);
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PutMapping("/users/{userEmail}/liabilitiestotal/{newTotal}")
    public ResponseEntity<Response<String>> changeUserLiabilitiesTotal(@PathVariable String userEmail,
                                                                 @PathVariable int newTotal,
                                                                 @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[PUT /users/" + userEmail + "/liabilitiestotal/" + newTotal + "] ";

        Response<String> response = new Response<>();

        logger.info(endpointString + "Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (user == null) {
            logger.warn(endpointString + "User not provided");
            response.put("message", "No user provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            user.setLiabilitiesTotal(newTotal);
            userRepository.save(user);
            logger.info(endpointString + "User " + user.getName() + " modified by " + userId);
            response.put("message", "User modified");
            return ResponseEntity.ok(response);
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PutMapping("/users/{userEmail}/networth/{newTotal}")
    public ResponseEntity<Response<String>> changeUserNetWorth(@PathVariable String userEmail,
                                                                       @PathVariable int newTotal,
                                                                       @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[PUT /users/" + userEmail + "/networth/" + newTotal + "] ";

        Response<String> response = new Response<>();

        logger.info(endpointString + "Cookie: " + userId);
        User user = userRepository.findByEmail(userEmail);
        // Only edit user if the cookie is set and the user is either an admin or the requested user
        if (user == null) {
            logger.warn(endpointString + "User not provided");
            response.put("message", "No user provided");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
                user.setNetWorth(newTotal);
                userRepository.save(user);
                logger.info(endpointString + "User " + user.getName() + " modified by " + userId);
                response.put("message", "User modified");
                return ResponseEntity.ok(response);
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Response<String>> deleteUser(@PathVariable String id,
                                                       @CookieValue(name = "user-id", required = false) String userId) {
        String endpointString = "[DELETE /users/" + id + "] ";

        Response<String> response = new Response<>();
        if (!isValidUserId(id)) {
            logger.warn(endpointString + "Invalid user provided: " + id);
            response.put("message", "Invalid user provided");
            return ResponseEntity.badRequest().body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || id.equals(userId))) {
            userRepository.deleteByEmail(id);
            logger.info(endpointString + "Entry for user deleted by userId " + userId);
            response.put("message", "User deleted");
            return ResponseEntity.ok(response);
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @PostMapping("/users/{userId}/assets")
    public ResponseEntity<Response<String>> addUserAssets(@PathVariable String userId,
                                                          @RequestBody Assets assets) {
        String endpointString = "[POST /users/" + userId + "/assets] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            logger.info(endpointString + "User not found");
            response.put("message", "Failed to assign assets");
            return ResponseEntity.badRequest().body(response);
        } else {
            assets.setUser(user);
            assetsRepository.save(assets);
            user.addAssets(assets);
            user.setNetWorth(user.getNetWorth() + assets.getAmount());
            userRepository.save(user);
            logger.info(endpointString + "New asset assigned to user");
            response.put("message", "Assets assigned to user");
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/users/{userId}/assets")
    public ResponseEntity<Response<String>> editUserAssets(@PathVariable String userId,
                                                          @RequestBody Assets assets) {
        String endpointString = "[PUT /users/" + userId + "/assets] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Assets originalAssets = assetsRepository.findAssetsById(assets.getId());
        if (user == null || (!userId.equals(originalAssets.getUser().getEmail()))) {
            logger.info(endpointString + "Failed to edit asset on user");
            response.put("message", "Failed to edit assets");
            return ResponseEntity.badRequest().body(response);
        } else {
            user.setNetWorth(user.getNetWorth() - originalAssets.getAmount() + assets.getAmount());
            assets.setUser(user);
            assetsRepository.save(assets);
            userRepository.save(user);
            logger.info(endpointString + "Assets updated for user");
            response.put("message", "Assets updated");
            return ResponseEntity.ok(response);
        }
    }

    @DeleteMapping("/users/{userId}/assets/{assetsId}")
    public ResponseEntity<Response<String>> removeUserAssets(@PathVariable String userId, @PathVariable int assetsId) {
        String endpointString = "[DELETE /users/" + userId + "/assets/" + assetsId + "] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Assets assets = assetsRepository.findAssetsById(assetsId);
        if (user == null || assets == null) {
            logger.warn(endpointString + "Failed to delete asset for user");
            response.put("message", "Failed to delete assets");
            return ResponseEntity.badRequest().body(response);
        } else {
            assetsRepository.deleteAssetsById(assetsId);
            user.removeAssets(assets);
            user.setNetWorth(user.getNetWorth() - assets.getAmount());
            userRepository.save(user);
            logger.info(endpointString + "Asset deleted for user");
            response.put("message", "Assets deleted");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/users/{userId}/liabilities")
    public ResponseEntity<Response<String>> addUserLiabilities(@PathVariable String userId,
                                                               @RequestBody Liabilities liabilities) {
        String endpointString = "[POST /users/" + userId + "/liabilities] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            logger.warn(endpointString + "Failed to assign liability for user");
            response.put("message", "Failed to assign liabilities");
            return ResponseEntity.badRequest().body(response);
        } else {
            liabilities.setUser(user);
            liabilitiesRepository.save(liabilities);
            user.addLiabilities(liabilities);
            user.setNetWorth(user.getNetWorth() - liabilities.getAmount());
            userRepository.save(user);
            logger.info(endpointString + "Liability successfully assigned for user");
            response.put("message", "Liabilities assigned to user");
            return ResponseEntity.ok(response);
        }
    }

    @PutMapping("/users/{userId}/liabilities")
    public ResponseEntity<Response<String>> editUserLiabilities(@PathVariable String userId,
                                                           @RequestBody Liabilities liabilities) {
        String endpointString = "[PUT /users/" + userId + "/liabilities] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Liabilities originalLiabilities = liabilitiesRepository.findLiabilitiesById(liabilities.getId());
        if (user == null || (!userId.equals(originalLiabilities.getUser().getEmail()))) {
            logger.warn(endpointString + "Failed to edit liability for user");
            response.put("message", "Failed to edit liabilities");
            return ResponseEntity.badRequest().body(response);
        } else {
            user.setNetWorth(user.getNetWorth() + originalLiabilities.getAmount() - liabilities.getAmount());
            liabilities.setUser(user);
            liabilitiesRepository.save(liabilities);
            userRepository.save(user);
            logger.info(endpointString + "Liability successfully updated for user");
            response.put("message", "Liabilities updated");
            return ResponseEntity.ok(response);
        }
    }
    @DeleteMapping("/users/{userId}/liabilities/{liabilitiesId}")
    public ResponseEntity<Response<String>> removeUserLiabilities(@PathVariable String userId,
                                                                  @PathVariable int liabilitiesId) {
        String endpointString = "[DELETE /users/" + userId + "/liabilities/" +liabilitiesId + "] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Liabilities liabilities = liabilitiesRepository.findLiabilitiesById(liabilitiesId);
        if (user == null || liabilities == null) {
            logger.warn(endpointString + "Liability failed to delete for user");
            response.put("message", "Failed to delete liabilities");
            return ResponseEntity.badRequest().body(response);
        } else {
            user.removeLiabilities(liabilities);
            user.setNetWorth(user.getNetWorth() + liabilities.getAmount());
            liabilitiesRepository.deleteLiabilitiesById(liabilitiesId);
            userRepository.save(user);
            logger.info(endpointString + "Liability successfully deleted for user");
            response.put("message", "Liabilities deleted");
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/users/{userId}/earnings/{earningsId}")
    public ResponseEntity<?> attachEarningsToUser(@PathVariable String userId, @PathVariable int earningsId) {
        String endpointString = "[POST /users/" + userId + "/earnings/" + earningsId + "] ";

        // Assuming userRepository and earningsRepository have been appropriately autowired
        User user = userRepository.findByEmail(userId);
        if (user == null) {
            // Handle case where the user is not found
            return ResponseEntity.badRequest().body("User not found with ID: " + userId);
        }

        Earnings earnings = earningsRepository.findById(earningsId);
        if (earnings == null) {
            // Handle case where the earnings are not found
            return ResponseEntity.badRequest().body("Earnings not found with ID: " + earningsId);
        }

        // Ensure the user's earnings collection is initialized
        if (user.getEarnings() == null) {
            user.setEarnings(new HashSet<>());
        }

        // Add the earnings to the user's collection of earnings and set the back reference
        earnings.setUser(user);
        user.getEarnings().add(earnings);

        // Assuming there's cascading or explicit saving necessary
        earningsRepository.save(earnings); // Save the earnings entity to update its user reference

        return ResponseEntity.ok("Earnings assigned successfully to user.");
    }




    @PostMapping("/users/{userId}/expenses/{expensesId}")
    public ResponseEntity<?> attachExpensesToUser(@PathVariable String userId, @PathVariable int expensesId) {
        String endpointString = "[POST /users/" + userId + "/expenses/" + expensesId + "] ";

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
        String endpointString = "[POST /users/" + userId + "/groups/" + groupId + "] ";

        Response<String> response = new Response<>();
        User user = userRepository.findByEmail(userId);
        Groups group = groupsRepository.findById(groupId);
        if (user == null || group == null) {
            logger.warn(endpointString + "Failed to assign group for user");
            response.put("message", "Failed to assign group");
        } else {
            group.addUser(user);
            user.addGroups(group);
            userRepository.save(user);
            logger.info(endpointString + "Successfully assigned group for user");
            response.put("message", "Group assigned");
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("/users/{userEmail}/profilepicture")
    public ResponseEntity<byte[]> getProfilePicture(@PathVariable String userEmail,
                                                    @CookieValue(name = "user-id", required = false) String userId) throws IOException {
        String endpointString = "[GET /users/{userEmail}/profilepicture] ";

        logger.info(endpointString + "Cookie: " + userId);
        // Only return image if the cookie is set and the user is either an admin or the requested user
        if (!isValidUserId(userEmail)) {
            return ResponseEntity.badRequest().body(null);
        }
        if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            logger.info(endpointString + "Successfully accessed data by user: " + userId);

            User user = userRepository.findByEmail(userEmail);
            File imageFile = new File(user.getProfilePictureFile());
            return ResponseEntity.ok(Files.readAllBytes(imageFile.toPath()));
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }
    }

    @PutMapping("/users/{userEmail}/profilepicture")
    public ResponseEntity<Response<String>> sendProfilePicture(@PathVariable String userEmail,
                                                               @RequestParam("image") MultipartFile imageFile,
                                                    @CookieValue(name = "user-id", required = false) String userId) throws IOException {
        String endpointString = "[PUT /users/{userEmail}/profilepicture] ";
        Response<String> response = new Response<>();

        logger.info(endpointString + "Cookie: " + userId);
        // Only set image if the cookie is set and the user is either an admin or the requested user
        if (!isValidUserId(userEmail)) {
            response.put("message", "Invalid user provided");
            return ResponseEntity.badRequest().body(response);
        } else if (imageFile == null || imageFile.getOriginalFilename() == null) {
            response.put("message", "No image provided");
            return ResponseEntity.badRequest().body(response);
        } else if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
            try {
                File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
                imageFile.transferTo(destinationFile);  // save file to disk
                logger.info(endpointString + imageFile.getSize() + " | " + imageFile.hashCode());

                User user = userRepository.findByEmail(userEmail);
                user.setProfilePictureFile(destinationFile.getAbsolutePath());
                userRepository.save(user);

                response.put("message", "File uploaded successfully: " + destinationFile.getAbsolutePath());
                return ResponseEntity.created(new URI("/users/" + userEmail + "/profilepicture")).body(response);
            } catch (IOException e) {
                response.put("message", "Failed to upload file: " + e.getMessage());
                return ResponseEntity.internalServerError().body(response);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        } else {
            logger.warn(endpointString + "Attempted access from invalid user");
            response.put("message", "User not allowed to perform this action");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
    }

    @GetMapping("/users/{userEmail}/receipts")
    public ResponseEntity<JSONObject> getReceipts(@PathVariable String userEmail,
                                                      @CookieValue(name = "user-id", required = false) String userId) throws IOException {
        String endpointString = "[GET /users/{userEmail}/receipts] ";
        JSONObject response = new JSONObject();

        logger.info(endpointString + "Cookie: " + userId);
        try {
            // Only return image if the cookie is set and the user is either an admin or the requested user
            if (!isValidUserId(userEmail)) {
                response.put("message", "Invalid user provided");
                return ResponseEntity.badRequest().body(new JSONObject(response.toString()));
            }
            if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
                logger.info(endpointString + "Successfully accessed data by user: " + userId);

                // Get receipt data
                User user = userRepository.findByEmail(userEmail);
                Set<Receipts> receipts = user.getReceipts();
                response.put("receiptsData", new JSONArray());
                JSONArray fileObjects;
                int i = 0;
                for(Receipts receipt : receipts) {
                    File imageFile = new File(receipt.getPath());

                    // Assemble receipt and metadata
                    fileObjects = response.getJSONArray("receiptsData");
                    fileObjects.put(i, new JSONObject().put("metadata", receipts)
                                            .put("file", Files.readAllBytes(imageFile.toPath())));
                    response.put("receiptsData", fileObjects);
                    i++;
                }
                response.put("numReceipts", i);

                // Respond
                return ResponseEntity.ok(response);
            } else {
                logger.warn(endpointString + "Attempted access from invalid user");
                response.put("message", "User not allowed to perform this action");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/users/{userEmail}/receipts/{receiptId}")
    public ResponseEntity<JSONObject> getReceiptsById(@PathVariable String userEmail,
                                                      @PathVariable int receiptId,
                                                      @CookieValue(name = "user-id", required = false) String userId) throws IOException {
        String endpointString = "[GET /users/{userEmail}/receipts/{receiptId}] ";
        JSONObject response = new JSONObject();

        logger.info(endpointString + "Cookie: " + userId);
        try {
            // Only return image if the cookie is set and the user is either an admin or the requested user
            if (!isValidUserId(userEmail)) {
                response.put("message", "Invalid user provided");
                return ResponseEntity.badRequest().body(new JSONObject(response.toString()));
            }
            if (isValidUserId(userId) && (isAdmin(userId) || userEmail.equals(userId))) {
                logger.info(endpointString + "Successfully accessed data by user: " + userId);

                // Get receipt data
                User user = userRepository.findByEmail(userEmail);
                Receipts receipts = receiptsRepository.findById(receiptId);
                // If receipt's user is not equal to the provided user
                if (!receipts.getUser().getEmail().equals(user.getEmail())) {
                    response.put("message", "Receipt not assigned to user");
                    return ResponseEntity.badRequest().body(new JSONObject(response.toString()));
                }
                File imageFile = new File(receipts.getPath());

                // Assemble receipt and metadata
                response.put("metadata", receipts);
                response.put("file", Files.readAllBytes(imageFile.toPath()));

                // Respond
                return ResponseEntity.ok(response);
            } else {
                logger.warn(endpointString + "Attempted access from invalid user");
                response.put("message", "User not allowed to perform this action");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/users/{userEmail}/receipts/{label}")
    public ResponseEntity<JSONObject> addReceipts(@PathVariable String userEmail,
                                                  @PathVariable String label,
                                                  @RequestParam("image") MultipartFile imageFile,
                                                  @CookieValue(name = "user-id", required = false) String userId) throws IOException, JSONException {
        String endpointString = "[POST /users/{userEmail}/receipts/{label}] ";
        JSONObject response = new JSONObject();

        logger.info(endpointString + "Cookie: " + userId);

        // Authentication and authorization check
        try {
            if (!isValidUserId(userId) || (!isAdmin(userId) && !userEmail.equals(userId))) {
                response.put("message", "Unauthorized access or invalid user.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }

            // Check if the uploaded image file is empty or has an unsupported content type
            if (imageFile.isEmpty() || !isSupportedContentType(imageFile.getContentType())) {
                response.put("message", "Invalid file or unsupported format.");
                return ResponseEntity.badRequest().body(response);
            }

            // Prepare the directory for saving the uploaded file
            File uploadDir = new File(directory);
            if (!uploadDir.exists() && !uploadDir.mkdirs()) {
                response.put("message", "Failed to create upload directory.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            // Generate a unique filename and save the file
            String filename = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
            File savedFile = new File(uploadDir, filename);
            imageFile.transferTo(savedFile);

            // Create and save the receipt record in the database
            User user = userRepository.findByEmail(userEmail);
            Receipts receipt = new Receipts();
            receipt.setUser(user);
            receipt.setLabel(label);
            receipt.setPath(savedFile.getAbsolutePath());
            receipt.setUploadedAt(new Date());
            receiptsRepository.save(receipt);

            // Prepare and return the response
            response.put("message", "Receipt successfully uploaded.");
            response.put("receiptId", receipt.getId());
            response.put("path", receipt.getPath());
            response.put("uploadedAt", receipt.getUploadedAt().toString());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error uploading receipt", e);
            response.put("message", "Error uploading receipt.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    private boolean isSupportedContentType(String contentType) {
        return contentType != null && (contentType.equals("image/jpeg") || contentType.equals("image/png"));
    }



    @PutMapping("/users/{userEmail}/receipts/{label}")
    public ResponseEntity<JSONObject> editReceipts(@PathVariable String userEmail,
                                                   @PathVariable String label,
                                                   @RequestParam("image") MultipartFile imageFile,
                                                   @CookieValue(name = "user-id", required = false) String userId) throws IOException, JSONException {
        String endpointString = "[PUT /users/{userEmail}/receipts/{label}] ";
        JSONObject response = new JSONObject();

        logger.info(endpointString + "Cookie: " + userId);

        // Check for authorization and valid user credentials
        if (!isValidUserId(userId) || (!isAdmin(userId) && !userEmail.equals(userId))) {
            response.put("message", "Unauthorized access or invalid user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Validate the content type of the uploaded image file
        if (imageFile.isEmpty() || !isSupportedContentType(imageFile.getContentType())) {
            response.put("message", "Invalid file type. Only JPEG and PNG are allowed.");
            return ResponseEntity.badRequest().body(response);
        }

        // Retrieve the receipt by label and user email
        Optional<Receipts> receiptOpt = receiptsRepository.findByLabelAndUserEmail(label, userEmail);
        if (receiptOpt.isEmpty()) {
            response.put("message", "Receipt not found.");
            return ResponseEntity.notFound().build();
        }
        Receipts receipt = receiptOpt.get();

        // Ensure the upload directory exists or create it if it does not
        File uploadDir = new File(directory);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            response.put("message", "Failed to create directory for uploads.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        // Generate a unique filename for the new file and save the file
        String newFilename = UUID.randomUUID().toString() + "-" + imageFile.getOriginalFilename();
        File newFile = new File(uploadDir, newFilename);
        imageFile.transferTo(newFile);

        // Check if there's an existing file and attempt to delete it if present
        if (receipt.getPath() != null) {
            File oldFile = new File(receipt.getPath());
            if (oldFile.exists()) {
                boolean deleteSuccess = oldFile.delete();
                if (!deleteSuccess) {
                    logger.error("Failed to delete old file at " + oldFile.getAbsolutePath());
                    response.put("message", "Failed to delete old file.");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
                }
            }
        }

        // Update the receipt details in the database
        receipt.setPath(newFile.getAbsolutePath());
        receipt.setUploadedAt(new Date());
        receiptsRepository.save(receipt);

        // Prepare and send the successful response
        response.put("message", "Receipt updated successfully.");
        response.put("label", receipt.getLabel());
        response.put("path", receipt.getPath());
        response.put("uploadedAt", receipt.getUploadedAt().toString());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/users/{userEmail}/receipts/{receiptId}")
    public ResponseEntity<JSONObject> deleteReceipts(@PathVariable String userEmail,
                                                     @PathVariable int receiptId,
                                                     @CookieValue(name = "user-id", required = false) String userId) throws IOException, JSONException {
        String endpointString = "[DELETE /users/{userEmail}/receipts/{receiptId}] ";
        JSONObject response = new JSONObject();

        logger.info(endpointString + "Cookie: " + userId);

        // Authorization checks: ensure the user is logged in, is the user in question or an admin
        if (!isValidUserId(userEmail) || !isValidUserId(userId) || (!isAdmin(userId) && !userEmail.equals(userId))) {
            response.put("message", "Unauthorized access or invalid user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Retrieve the receipt by ID
        Optional<Receipts> receiptOpt = receiptsRepository.findById((long) receiptId);
        if (receiptOpt.isEmpty()) {
            response.put("message", "Receipt not found.");
            return ResponseEntity.notFound().build();
        }

        Receipts receipt = receiptOpt.get();
        // Additional security check to confirm receipt belongs to the user
        if (!receipt.getUser().getEmail().equals(userEmail)) {
            response.put("message", "Receipt does not belong to user.");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }

        // Attempt to delete the file associated with the receipt
        File file = new File(receipt.getPath());
        if (file.exists() && !file.delete()) {
            logger.error("Failed to delete the file: " + receipt.getPath());
            response.put("message", "Failed to delete the file.");
            return ResponseEntity.internalServerError().body(response);
        }

        // Delete the receipt record from the database
        receiptsRepository.delete(receipt);
        response.put("message", "Receipt deleted successfully.");
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
