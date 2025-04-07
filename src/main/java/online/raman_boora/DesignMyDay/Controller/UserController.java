package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Models.Booking;
import online.raman_boora.DesignMyDay.Models.Venue;
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

    // Get all users
    @GetMapping("/users")
    public List<Users> getUsers() {
        logger.info("Fetching all users");
        return userServices.getUsers();
    }

    // Signup endpoint
    @PostMapping("/signup")
    public ResponseEntity<String> saveUser(@RequestBody Users user) {
        logger.info("Signup request for user: {}", user.getName());
        String result = userServices.saveUser(user);
        return ResponseEntity.ok(result);
    }

    // Login endpoint
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

    // Get user by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<Users> getUserByUserId(@PathVariable String userId) {
        logger.info("Fetching user by ID: {}", userId);
        Optional<Users> user = userServices.getUserByUserId(userId);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get user by name
    @GetMapping("/users/name/{name}")
    public ResponseEntity<Users> getUserByName(@PathVariable String name) {
        logger.info("Fetching user by name: {}", name);
        Optional<Users> user = userServices.getUserByname(name);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // User Dashboard - Display all user details
    @GetMapping("/dashboard/{userId}")
    public ResponseEntity<Map<String, Object>> getUserDashboard(@PathVariable String userId) {
        logger.info("Fetching dashboard for user: {}", userId);
        Optional<Users> userOptional = userServices.getUserByUserId(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Users user = userOptional.get();
        Map<String, Object> dashboardData = new HashMap<>();

        dashboardData.put("user", user);
        dashboardData.put("bookings", user.getBookings());
        dashboardData.put("savedVenues", user.getSavedVenues());

        return ResponseEntity.ok(dashboardData);
    }
}
