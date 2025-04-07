package online.raman_boora.DesignMyDay.Repositories;

import online.raman_boora.DesignMyDay.Models.Venue;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VenueRepository extends MongoRepository<Venue, String> {
    Optional<Venue> findByVenueName(String venueName); // Find venue by exact name
    List<Venue> findByVenueNameContainingIgnoreCase(String venueName); // Find venues by partial name match
    List<Venue> findByVenueAddressContainingIgnoreCase(String address); // Find venues by partial address match
}