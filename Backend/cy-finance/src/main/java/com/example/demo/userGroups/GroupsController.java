package com.example.demo.userGroups;

import com.example.demo.users.UserController;
import com.example.demo.users.UserRepository;
import com.example.demo.util.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/groups")
public class GroupsController {

    private final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    GroupsRepository groupsRepository;

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Groups>> getGroups() {
        logger.info("[GET /groups]");
        return ResponseEntity.ok(groupsRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Groups> getGroupsById(@PathVariable int id) {
        logger.info("[GET /users/" + id + "]");
        return ResponseEntity.ok(groupsRepository.findById(id));
    }

    @PostMapping
    public ResponseEntity<Response<String>> addGroup(@RequestBody Groups group) {
        logger.info("[POST /groups] Adding group: " + group);
        Response<String> response = new Response<>();
        if (groupsRepository.findByName(group.getName()) == null) {
            groupsRepository.save(group);
            logger.info("[POST /groups] Successfully added group");
            response.put("message", "success");
        } else {
            logger.info("[POST /groups] Group already exists");
            response.put("message", "A group with that name already exists");
        }
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<Response<String>> editGroup(@RequestBody Groups group) {
        logger.info("[PUT /groups] Changing group: " + group);
        Response<String> response = new Response<>();
        groupsRepository.save(group);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<String>> deleteGroup(@PathVariable int id) {
        logger.info("[DELETE /{id}");
        Response<String> response = new Response<>();
        groupsRepository.deleteById(id);
        response.put("message", "success");
        return ResponseEntity.ok(response);
    }

}
