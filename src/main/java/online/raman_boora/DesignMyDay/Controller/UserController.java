package online.raman_boora.DesignMyDay.Controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Services.UserServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@RestController
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserServices userServices;

    @Autowired
    private HttpServletResponse httpServletResponse;

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
        String token = userServices.validate(user);

        if (token == null) {
            return ResponseEntity.status(401).body(Collections.singletonMap("error", "Invalid credentials"));
        }

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("message", "Login successful");

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
