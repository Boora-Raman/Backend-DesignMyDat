package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.Models.Booking;
import online.raman_boora.DesignMyDay.Services.BookingServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    @Autowired
    private BookingServices bookingServices;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getBookingsByUserId(@PathVariable String userId) {
        logger.info("Fetching bookings for user ID: {}", userId);
        List<Booking> bookings = bookingServices.getBookingsByUserId(userId);
        return ResponseEntity.ok(bookings);
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @RequestBody Map<String, Object> bookingData,
            @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Creating booking for user: {}", userDetails.getUsername());
        try {
            String email = userDetails.getUsername();  // ✅ Corrected
            String venueId = (String) bookingData.get("venueId");
            String bookingDate = (String) bookingData.get("bookingDate");
            List<String> vendorIds = (List<String>) bookingData.get("vendorIds");
            List<String> carterIds = (List<String>) bookingData.get("carterIds");

            Booking savedBooking = bookingServices.createBooking(email, venueId, bookingDate, vendorIds, carterIds);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBooking);
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/venue/{venueId}")
    public ResponseEntity<List<Booking>> getBookingsByVenueId(@PathVariable String venueId) {
        logger.info("Fetching bookings for venue ID: {}", venueId);
        List<Booking> bookings = bookingServices.getBookingsByVenueId(venueId);
        return ResponseEntity.ok(bookings);
    }

    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        logger.info("Cancelling booking with ID: {}", bookingId);
        boolean cancelled = bookingServices.cancelBooking(bookingId);
        if (cancelled) {
            return ResponseEntity.ok().build();
        }
        logger.warn("Booking with ID '{}' not found", bookingId);
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<Booking>> getAllBookings() {
        logger.info("Fetching all bookings");
        List<Booking> bookings = bookingServices.getAllBookings();
        return ResponseEntity.ok(bookings);
    }
}
