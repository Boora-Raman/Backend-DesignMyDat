package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Images;
import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Models.Venue;
import online.raman_boora.DesignMyDay.Repositories.ImagesRepository;
import online.raman_boora.DesignMyDay.Repositories.ServiceRepository;
import online.raman_boora.DesignMyDay.Repositories.UserRepository;
import online.raman_boora.DesignMyDay.Repositories.VenueRepository;
import online.raman_boora.DesignMyDay.configurations.FileStorageConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
public class VenueServices {

    private static final Logger logger = LoggerFactory.getLogger(VenueServices.class);

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageConfig fileStorageConfig;

    public List<Venue> getAllVenues() {
        logger.info("Retrieving all venues from the database");
        List<Venue> venues = venueRepository.findAll();
        logger.debug("Found {} venues", venues.size());
        return venues;
    }

    @Transactional
    public String saveVenue(Venue venue, List<MultipartFile> images, String userId) {
        logger.info("Saving new venue: {} for user name: {}", venue.getVenueName(), userId);
        try {
            if (venueRepository.findByVenueName(venue.getVenueName()).isPresent()) {
                logger.warn("Venue with name '{}' already exists", venue.getVenueName());
                throw new IllegalArgumentException("A venue with this name already exists");
            }
            if (venue.getVenuePrice() == null || venue.getVenuePrice() < 0) {
                logger.error("Invalid venue price: {}", venue.getVenuePrice());
                throw new IllegalArgumentException("Venue price must be non-negative");
            }

            if (images != null && !images.isEmpty()) {
                logger.info("Processing {} images for venue: {}", images.size(), venue.getVenueName());
                List<Images> savedImages = imageService.saveVenueImages(images);
                for (Images image : savedImages) {
                    imagesRepository.save(image);
                    logger.debug("Saved image: {} with ID: {}", image.getImgName(), image.getImgid());
                }
                venue.setImages(savedImages);
            } else {
                logger.info("No images provided for venue: {}", venue.getVenueName());
            }

            venueRepository.save(venue);
            Optional<Users> user = userRepository.findByName(userId);
            if (user.isPresent()) {
                Users u = user.get();
                if (u.getVenues() == null) {
                    u.setVenues(new ArrayList<>());
                }
                u.getVenues().add(venue);
                userRepository.save(u);
                logger.info("Venue '{}' associated with user name: {}", venue.getVenueName(), userId);
            } else {
                logger.warn("User with name '{}' not found, venue saved without user association", userId);
            }

            logger.info("Venue '{}' saved successfully with ID: {}", venue.getVenueName(), venue.getVenueId());
            return "Venue created successfully with ID: " + venue.getVenueId();
        } catch (Exception e) {
            logger.error("Error saving venue: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create venue: " + e.getMessage());
        }
    }

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

    public Optional<Venue> getVenuesByName(String venueName) {
        logger.info("Fetching venues with name: {}", venueName);
        Optional<Venue> venue = venueRepository.findByVenueName(venueName);
        if (venue.isEmpty()) {
            logger.warn("No venues found with name: {}", venueName);
        } else {
            logger.debug("Found venue with name '{}'", venueName);
        }
        return venue;
    }

    @Transactional
    public Optional<Venue> updateVenue(String venueId, Venue updatedVenue, List<MultipartFile> images) {
        logger.info("Updating venue with ID: {}", venueId);
        Optional<Venue> existingVenue = venueRepository.findById(venueId);

        if (existingVenue.isEmpty()) {
            logger.warn("Venue with ID '{}' not found for update", venueId);
            return Optional.empty();
        }

        Venue venue = existingVenue.get();

        if (updatedVenue.getVenueName() != null && !updatedVenue.getVenueName().isBlank()) {
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
        if (updatedVenue.getVenuePrice() != null) {
            if (updatedVenue.getVenuePrice() < 0) {
                logger.error("Invalid venue price: {}", updatedVenue.getVenuePrice());
                throw new IllegalArgumentException("Venue price must be non-negative");
            }
            venue.setVenuePrice(updatedVenue.getVenuePrice());
        }
        if (updatedVenue.getServices() != null) {
            venue.setServices(updatedVenue.getServices());
        }

        if (images != null && !images.isEmpty()) {
            try {
                logger.info("Processing {} images for venue update: {}", images.size(), venue.getVenueName());
                List<Images> savedImages = imageService.saveVenueImages(images);
                for (Images image : savedImages) {
                    imagesRepository.save(image);
                    logger.debug("Saved image: {} with ID: {}", image.getImgName(), image.getImgid());
                }
                venue.getImages().addAll(savedImages);
            } catch (Exception e) {
                logger.error("Error saving images for venue ID '{}': {}", venueId, e.getMessage());
                throw new RuntimeException("Failed to save images: " + e.getMessage());
            }
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

    @Transactional
    public boolean deleteVenue(String venueId) {
        logger.info("Deleting venue with ID: {}", venueId);
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isEmpty()) {
            logger.warn("Venue with ID '{}' not found for deletion", venueId);
            return false;
        }

        try {
            Venue v = venue.get();
            if (v.getServices() != null) {
                v.getServices().clear();
                venueRepository.save(v);
            }
            if (v.getImages() != null) {
                for (Images image : v.getImages()) {
                    imagesRepository.deleteById(image.getImgid());
                    Path filePath = Paths.get(fileStorageConfig.getVenueUploadDir(), image.getImgName());
                    Files.deleteIfExists(filePath);
                }
            }
            venueRepository.deleteById(venueId);
            logger.info("Venue with ID '{}' deleted successfully", venueId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting venue with ID '{}': {}", venueId, e.getMessage());
            throw new RuntimeException("Failed to delete venue: " + e.getMessage());
        }
    }

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

    public List<Venue> getVenuesByUserId(String userId) {
        logger.info("Retrieving venues for user name: {}", userId);
        Optional<Users> user = userRepository.findByName(userId);
        if (user.isEmpty()) {
            logger.warn("User with name '{}' not found", userId);
            return new ArrayList<>();
        }

        Users userData = user.get();
        List<Venue> venues = userData.getVenues();
        if (venues == null) {
            logger.warn("Venues list is null for user name: {}. Initializing empty list.", userId);
            venues = new ArrayList<>();
        }

        logger.debug("Found {} venues for user name: {}", venues.size(), userId);
        return new ArrayList<>(venues);
    }

    @Transactional
    public void addServicesToVenue(String venueId, List<String> serviceIds) {
        logger.info("Adding {} services to venue ID: {}", serviceIds.size(), venueId);
        Optional<Venue> venueOpt = venueRepository.findById(venueId);
        if (venueOpt.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            throw new IllegalArgumentException("Venue not found");
        }

        Venue venue = venueOpt.get();
        if (venue.getServices() == null) {
            venue.setServices(new ArrayList<>());
        }

        for (String serviceId : serviceIds) {
            Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
            if (serviceOpt.isPresent()) {
                Service service = serviceOpt.get();
                if (!venue.getServices().stream().anyMatch(s -> s.getServiceId().equals(serviceId))) {
                    venue.getServices().add(service);
                    logger.debug("Added service '{}' to venue '{}'", service.getServiceName(), venue.getVenueName());
                }
            } else {
                logger.warn("Service with ID '{}' not found", serviceId);
            }
        }

        venueRepository.save(venue);
        logger.info("Services added to venue ID: {}", venueId);
    }

    @Transactional
    public void removeServiceFromVenue(String venueId, String serviceId) {
        logger.info("Removing service ID: {} from venue ID: {}", serviceId, venueId);
        Optional<Venue> venueOpt = venueRepository.findById(venueId);
        if (venueOpt.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            throw new IllegalArgumentException("Venue not found");
        }

        Venue venue = venueOpt.get();
        if (venue.getServices() != null) {
            venue.getServices().removeIf(service -> service.getServiceId().equals(serviceId));
            venueRepository.save(venue);
            logger.info("Service ID: {} removed from venue ID: {}", serviceId, venueId);
        } else {
            logger.warn("No services found for venue ID: {}", venueId);
        }
    }
}