package online.raman_boora.DesignMyDay.Controller;

import jakarta.validation.Valid;
import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Services.ServicesClassService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    ServicesClassService servicesClassService;

    @GetMapping("/{venueId}")
    public ResponseEntity<List<Service>> getVenueServices(@PathVariable String venueId) {
        logger.info("Fetching services for venue ID: {}", venueId);
        List<Service> services = servicesClassService.getServicesByVenueId(venueId);
        return ResponseEntity.ok(services);
    }

    @PostMapping("/{venueId}")
    public ResponseEntity<String> addService(
            @PathVariable String venueId,
            @Valid @RequestBody Service service) {
        logger.info("Adding service to venue ID: {}", venueId);
        String result = servicesClassService.addServiceToVenue(venueId, service);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PutMapping("/{venueId}/{serviceId}")
    public ResponseEntity<Service> updateService(
            @PathVariable String venueId,
            @PathVariable String serviceId,
            @Valid @RequestBody Service service) {
        logger.info("Updating service ID: {} for venue ID: {}", serviceId, venueId);
        Optional<Service> updatedService = servicesClassService.updateService(venueId, serviceId, service);
        return updatedService.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{venueId}/{serviceId}")
    public ResponseEntity<Map<String, String>> deleteService(
            @PathVariable String venueId,
            @PathVariable String serviceId) {
        logger.info("Deleting service ID: {} from venue ID: {}", serviceId, venueId);
        boolean deleted = servicesClassService.deleteService(venueId, serviceId);
        if (deleted) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Service deleted successfully"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Collections.singletonMap("error", "Service not found"));
    }
}