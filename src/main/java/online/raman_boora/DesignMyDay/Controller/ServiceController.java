package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Services.ServiceServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

    @Autowired
    private ServiceServices serviceServices;

    @GetMapping
    public ResponseEntity<List<Service>> getAllServices() {
        logger.info("Fetching all services");
        List<Service> services = serviceServices.getAllServices();
        return ResponseEntity.ok(services);
    }

    @PostMapping
    public ResponseEntity<Service> createService(@RequestBody Service service) {
        logger.info("Creating new service: {}", service.getServiceName());
        Service savedService = serviceServices.saveService(service);
        return ResponseEntity.status(201).body(savedService);
    }
}