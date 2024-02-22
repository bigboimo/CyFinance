package com.example.demo.users;

import com.example.demo.util.Response;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
public class UserController {

    Response<User> users = new Response<>();

    @GetMapping("/users")
    public String getUsers(){
        return users.toString();
    }

    @GetMapping("/users/{email}")
    public String getUser(@PathVariable String email){
        Response<String> response = new Response<>();
        if (email == null || users.get(email) == null) {
            response.put("message", "User not found");
            return response.toString();
        } else {
            return users.get(email).toString();
        }
    }

    @PostMapping("/users")
    public String setUser(@RequestBody User user){
        Response<String> response = new Response<>();
        users.put(user.getEmail(), user);
        response.put("message", "User created");
        return response.toString();
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password){
        Response<String> response = new Response<>();
        if (email.equals("test@email.com") && password.equals("password")){
            response.put("message", "success");
            return response.toString();
        } else {
            response.put("message", "failure");
            return response.toString();
        }
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String email){
        Response<String> response = new Response<>();
        if (email == null || users.get(email) == null) {
            response.put("message", "User not found");
            return response.toString();
        } else {
            response.put("message", "Successfully logged out");
            return response.toString();
        }
    }

    @PutMapping("/users")
    public String changeUser(@RequestBody User user){
        Response<String> response = new Response<>();
        users.put(user.getEmail(), user);
        response.put("message", "User modified");
        return response.toString();
    }

    @DeleteMapping("/users/{email}")
    public String deleteUser(@PathVariable String email){
        Response<String> response = new Response<>();
        users.remove(email);
        response.put("message", "User deleted");
        return response.toString();
    }
}
