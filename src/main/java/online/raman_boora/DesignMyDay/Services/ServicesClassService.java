package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Models.Venue;
import online.raman_boora.DesignMyDay.Repositories.ServicesRepository;
import online.raman_boora.DesignMyDay.Repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@org.springframework.stereotype.Service
public class ServicesClassService {

    private static final Logger logger = LoggerFactory.getLogger(ServicesClassService.class);

    @Autowired
    private ServicesRepository servicesRepository;

    @Autowired
    private VenueRepository venueRepository;

    public List<Service> getServicesByVenueId(String venueId) {
        logger.info("Fetching services for venue ID: {}", venueId);
        Optional<Venue> venue = venueRepository.findById(venueId);
        if (venue.isEmpty()) {
            logger.warn("No venue found with ID: {}", venueId);
            return Collections.emptyList();
        }
        List<Service> services = venue.get().getServices();
        logger.debug("Found {} services for venue ID: {}", services.size(), venueId);
        return services;
    }

    @Transactional
    public String addServiceToVenue(String venueId, Service service) {
        logger.info("Adding service to venue ID: {}", venueId);
        Optional<Venue> venueOptional = venueRepository.findById(venueId);
        if (venueOptional.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            return "Venue not found";
        }

        try {
            Venue venue = venueOptional.get();
            service.setServiceId(UUID.randomUUID().toString());
            venue.getServices().add(service);

            servicesRepository.save(service);
            venueRepository.save(venue);

            logger.info("Service '{}' added successfully to venue ID: {}",
                    service.getServiceName(), venueId);
            return "Service added successfully with ID: " + service.getServiceId();
        } catch (Exception e) {
            logger.error("Error adding service to venue ID '{}': {}", venueId, e.getMessage());
            throw new RuntimeException("Failed to add service: " + e.getMessage());
        }
    }

    @Transactional
    public Optional<Service> updateService(String venueId, String serviceId, Service service) {
        logger.info("Updating service ID: {} for venue ID: {}", serviceId, venueId);
        Optional<Venue> venueOptional = venueRepository.findById(venueId);
        if (venueOptional.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            return Optional.empty();
        }

        Venue venue = venueOptional.get();
        Optional<Service> existingService = venue.getServices().stream()
                .filter(s -> s.getServiceId().equals(serviceId))
                .findFirst();

        if (existingService.isEmpty()) {
            logger.warn("Service with ID '{}' not found in venue ID: {}", serviceId, venueId);
            return Optional.empty();
        }

        try {
            Service updatedService = existingService.get();
            updatedService.setServiceName(service.getServiceName());
            updatedService.setServiceDescription(service.getServiceDescription());
            updatedService.setServicePrice(service.getServicePrice());

            servicesRepository.save(updatedService);
            venueRepository.save(venue);

            logger.info("Service ID '{}' updated successfully for venue ID: {}",
                    serviceId, venueId);
            return Optional.of(updatedService);
        } catch (Exception e) {
            logger.error("Error updating service ID '{}' for venue ID '{}': {}",
                    serviceId, venueId, e.getMessage());
            throw new RuntimeException("Failed to update service: " + e.getMessage());
        }
    }

    @Transactional
    public boolean deleteService(String venueId, String serviceId) {
        logger.info("Deleting service ID: {} from venue ID: {}", serviceId, venueId);
        Optional<Venue> venueOptional = venueRepository.findById(venueId);
        if (venueOptional.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            return false;
        }

        Venue venue = venueOptional.get();
        boolean removed = venue.getServices().removeIf(s -> s.getServiceId().equals(serviceId));
        if (!removed) {
            logger.warn("Service with ID '{}' not found in venue ID: {}", serviceId, venueId);
            return false;
        }

        try {
            venueRepository.save(venue);
            servicesRepository.deleteById(serviceId);

            logger.info("Service ID '{}' deleted successfully from venue ID: {}",
                    serviceId, venueId);
            return true;
        } catch (Exception e) {
            logger.error("Error deleting service ID '{}' from venue ID '{}': {}",
                    serviceId, venueId, e.getMessage());
            throw new RuntimeException("Failed to delete service: " + e.getMessage());
        }
    }
}