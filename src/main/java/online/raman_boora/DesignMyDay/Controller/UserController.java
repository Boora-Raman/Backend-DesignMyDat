package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@CrossOrigin
@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServices userServices;

    @GetMapping("/users")
    public List<Users> getUsers() {
        logger.info("Request received to fetch all users");
        return userServices.getUsers();
    }

    @PostMapping("/signup")
    public ResponseEntity<String> saveUser(@RequestBody Users user) {
        logger.info("Signup request received: {}", user);
        String result = userServices.saveUser(user);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Users user) {
        logger.info("Login request received: username={}", user.getName());
        String token = userServices.validate(user);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        logger.info("Login successful, token generated for username={}", user.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Users> getUserByUserId(@PathVariable String userId) {
        logger.info("Request received to fetch user by ID: {}", userId);
        Optional<Users> userDetails = userServices.getUserByUserId(userId);
        return userDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/users/{name}")
    public ResponseEntity<Users> getUserByName(@PathVariable String name) {
        logger.info("Request received to fetch user by name: {}", name);
        Optional<Users> userDetails = userServices.getUserByname(name);
        return userDetails.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}