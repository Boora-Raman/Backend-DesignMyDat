package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Venue;
import online.raman_boora.DesignMyDay.Repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VenueServices {

    private static final Logger logger = LoggerFactory.getLogger(VenueServices.class);

    @Autowired
    private VenueRepository venueRepository;

    // Get all venues
    public List<Venue> getAllVenues() {
        logger.info("Retrieving all venues from the database");
        List<Venue> venues = venueRepository.findAll();
        logger.debug("Found {} venues", venues.size());
        return venues;
    }

    // Save a new venue
    public String saveVenue(Venue venue) {
        logger.info("Saving new venue: {}", venue.getVenueName());
        try {
            // Check if a venue with the same name already exists (since venueName is unique)
            if (venueRepository.findByVenueName(venue.getVenueName()).isPresent()) {
                logger.warn("Venue with name '{}' already exists", venue.getVenueName());
                throw new IllegalArgumentException("A venue with this name already exists");
            }
            venueRepository.save(venue);
            logger.info("Venue '{}' saved successfully", venue.getVenueName());
            return "Venue created successfully with ID: " + venue.getVenueId();
        } catch (Exception e) {
            logger.error("Error saving venue: {}", e.getMessage());
            throw new RuntimeException("Failed to create venue: " + e.getMessage());
        }
    }

    // Get venue by ID
    public Optional<Venue> getVenueById(String venueId) {
        logger.info("Fetching venue with ID: {}", venueId);
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isEmpty()) {
            logger.warn("No venue found with ID: {}", venueId);
        } else {
            logger.debug("Found venue: {}", venue.get().getVenueName());
        }
        return venue;
    }

    // Get venues by name
//    public List<Venue> getVenuesByName(String venueName) {
//        logger.info("Fetching venues with name: {}", venueName);
//        Optional<Venue> venues = venueRepository.findByVenueName(venueName);
//        if (venues.isEmpty()) {
//            logger.warn("No venues found with name: {}", venueName);
//        } else {
//            logger.debug("Found {} venues with name '{}'", venues.si, venueName);
//        }
//        return venues;
//    }

    // Update an existing venue
    public Optional<Venue> updateVenue(String venueId, Venue updatedVenue) {
        logger.info("Updating venue with ID: {}", venueId);
        Optional<Venue> existingVenue = venueRepository.findById(venueId);

        if (existingVenue.isEmpty()) {
            logger.warn("Venue with ID '{}' not found for update", venueId);
            return Optional.empty();
        }

        Venue venue = existingVenue.get();
        // Update fields only if they are provided in the request
        if (updatedVenue.getVenueName() != null && !updatedVenue.getVenueName().isBlank()) {
            // Check if the new name is already taken by another venue
            Optional<Venue> venueWithSameName = venueRepository.findByVenueName(updatedVenue.getVenueName());
            if (venueWithSameName.isPresent() && !venueWithSameName.get().getVenueId().equals(venueId)) {
                logger.warn("Another venue already exists with name: {}", updatedVenue.getVenueName());
                throw new IllegalArgumentException("Another venue already exists with this name");
            }
            venue.setVenueName(updatedVenue.getVenueName());
        }
        if (updatedVenue.getVenueAddress() != null && !updatedVenue.getVenueAddress().isBlank()) {
            venue.setVenueAddress(updatedVenue.getVenueAddress());
        }
        if (updatedVenue.getServices() != null) {
            venue.setServices(updatedVenue.getServices());
        }

        try {
            venueRepository.save(venue);
            logger.info("Venue with ID '{}' updated successfully", venueId);
            return Optional.of(venue);
        } catch (Exception e) {
            logger.error("Error updating venue with ID '{}': {}", venueId, e.getMessage());
            throw new RuntimeException("Failed to update venue: " + e.getMessage());
        }
    }

    // Delete a venue
    public boolean deleteVenue(String venueId) {
        logger.info("Deleting venue with ID: {}", venueId);
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isEmpty()) {
            logger.warn("Venue with ID '{}' not found for deletion", venueId);
            return false;
        }

        try {
            venueRepository.deleteById(venueId);
            logger.info("Venue with ID '{}' deleted successfully", venueId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting venue with ID '{}': {}", venueId, e.getMessage());
            throw new RuntimeException("Failed to delete venue: " + e.getMessage());
        }
    }

    // Search venues by address (partial match)
    public List<Venue> searchVenuesByAddress(String address) {
        logger.info("Searching venues with address containing: {}", address);
        List<Venue> venues = venueRepository.findByVenueAddressContainingIgnoreCase(address);
        if (venues.isEmpty()) {
            logger.warn("No venues found with address containing: {}", address);
        } else {
            logger.debug("Found {} venues with address containing '{}'", venues.size(), address);
        }
        return venues;
    }

    public Optional<Venue> getVenuesByName(String venueName) {
    return venueRepository.findByVenueName(venueName);
    }
}