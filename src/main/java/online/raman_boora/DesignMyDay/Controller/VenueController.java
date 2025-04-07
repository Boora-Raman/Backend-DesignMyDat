package online.raman_boora.DesignMyDay.Controller;

import jakarta.validation.Valid;
import online.raman_boora.DesignMyDay.Models.Venue;
import online.raman_boora.DesignMyDay.Services.VenueServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/venues") // Base path for venue-related endpoints
public class VenueController {

    private static final Logger logger = LoggerFactory.getLogger(VenueController.class);

    @Autowired
    private VenueServices venueServices;

    // Get all venues
    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        logger.info("Fetching all venues");
        List<Venue> venues = venueServices.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    // Create a new venue
    @PostMapping
    public ResponseEntity<String> createVenue(@Valid @RequestBody Venue venue) {
        logger.info("Creating new venue: {}", venue.getVenueName());
        String result = venueServices.saveVenue(venue);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    // Get venue by ID
    @GetMapping("/{venueId}")
    public ResponseEntity<Venue> getVenueById(@PathVariable String venueId) {
        logger.info("Fetching venue by ID: {}", venueId);
        Optional<Venue> venue = venueServices.getVenueById(venueId);
        return venue.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Get venues by name
    @GetMapping("/name/{venueName}")
    public ResponseEntity<Venue> getVenuesByName(@PathVariable String venueName) {
        logger.info("Fetching venues by name: {}", venueName);
        Optional<Venue> venuesByName = venueServices.getVenuesByName(venueName);

        if (venuesByName.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(venuesByName.get());
    }

    // Update a venue
    @PutMapping("/{venueId}")
    public ResponseEntity<Venue> updateVenue(@PathVariable String venueId, @Valid @RequestBody Venue venue) {
        logger.info("Updating venue with ID: {}", venueId);
        Optional<Venue> updatedVenue = venueServices.updateVenue(venueId, venue);
        return updatedVenue.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a venue
    @DeleteMapping("/{venueId}")
    public ResponseEntity<Map<String, String>> deleteVenue(@PathVariable String venueId) {
        logger.info("Deleting venue with ID: {}", venueId);
        boolean deleted = venueServices.deleteVenue(venueId);
        if (deleted) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Venue deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Venue not found"));
    }

    // Get services for a specific venue
    @GetMapping("/{venueId}/services")
    public ResponseEntity<Map<String, Object>> getVenueServices(@PathVariable String venueId) {
        logger.info("Fetching services for venue ID: {}", venueId);
        Optional<Venue> venueOptional = venueServices.getVenueById(venueId);

        if (venueOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Venue venue = venueOptional.get();
        Map<String, Object> response = new HashMap<>();
        response.put("venueId", venue.getVenueId());
        response.put("venueName", venue.getVenueName());
        response.put("services", venue.getServices());

        return ResponseEntity.ok(response);
    }

    // Search venues by address (partial match)
    @GetMapping("/search/address")
    public ResponseEntity<List<Venue>> searchVenuesByAddress(@RequestParam String address) {
        logger.info("Searching venues by address: {}", address);
        List<Venue> venues = venueServices.searchVenuesByAddress(address);
        if (venues.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(venues);
    }
}