package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Repositories.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@org.springframework.stereotype.Service
public class ServiceServices {

    private static final Logger logger = LoggerFactory.getLogger(ServiceServices.class);

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Service> getAllServices() {
        logger.info("Fetching all services");
        List<Service> services = serviceRepository.findAll();
        logger.debug("Found {} services", services.size());
        return services;
    }

    public Service saveService(Service service) {
        logger.info("Saving service: {}", service.getServiceName());
        if (service.getServicePrice() == null || service.getServicePrice() < 0) {
            logger.error("Invalid service price: {}", service.getServicePrice());
            throw new IllegalArgumentException("Service price must be non-negative");
        }
        if (service.getServiceName() == null || service.getServiceName().trim().isEmpty()) {
            logger.error("Service name is empty");
            throw new IllegalArgumentException("Service name cannot be empty");
        }
        try {
            Service savedService = serviceRepository.save(service);
            logger.info("Service '{}' saved with ID: {}", savedService.getServiceName(), savedService.getServiceId());
            return savedService;
        } catch (Exception e) {
            logger.error("Error saving service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to save service: " + e.getMessage());
        }
    }
}