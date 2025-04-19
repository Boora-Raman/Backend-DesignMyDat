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
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/venues")
public class VenueController {

    private static final Logger logger = LoggerFactory.getLogger(VenueController.class);

    @Autowired
    private VenueServices venueServices;

    @GetMapping
    public ResponseEntity<List<Venue>> getAllVenues() {
        logger.info("Fetching all venues");
        List<Venue> venues = venueServices.getAllVenues();
        return ResponseEntity.ok(venues);
    }

    @PostMapping
    public ResponseEntity<String> createVenue(
            @Valid @RequestPart("venue") Venue venue,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        logger.info("Creating new venue: {}", venue.getVenueName());
        String result = venueServices.saveVenue(venue, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<Map<String, Object>> getVenueById(@PathVariable String venueId) {
        logger.info("Fetching venue by ID: {}", venueId);
        Optional<Venue> venue = venueServices.getVenueById(venueId);
        if (venue.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("venue", venue.get());
            response.put("actions", Map.of(
                    "addService", "/venues/" + venueId + "/services",
                    "editServices", "/venues/" + venueId + "/services"
            ));
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/name/{venueName}")
    public ResponseEntity<Venue> getVenuesByName(@PathVariable String venueName) {
        logger.info("Fetching venues by name: {}", venueName);
        Optional<Venue> venuesByName = venueServices.getVenuesByName(venueName);
        return venuesByName.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{venueId}")
    public ResponseEntity<Venue> updateVenue(
            @PathVariable String venueId,
            @Valid @RequestPart("venue") Venue venue,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        logger.info("Updating venue with ID: {}", venueId);
        Optional<Venue> updatedVenue = venueServices.updateVenue(venueId, venue, images);
        return updatedVenue.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

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