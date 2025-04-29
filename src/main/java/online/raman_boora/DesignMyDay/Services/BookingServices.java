package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Booking;
import online.raman_boora.DesignMyDay.Models.Carter;
import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Models.Vendor;
import online.raman_boora.DesignMyDay.Models.Venue;
import online.raman_boora.DesignMyDay.Repositories.BookingRepository;
import online.raman_boora.DesignMyDay.Repositories.CarterRepository;
import online.raman_boora.DesignMyDay.Repositories.UserRepository;
import online.raman_boora.DesignMyDay.Repositories.VendorRepository;
import online.raman_boora.DesignMyDay.Repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingServices {

    private static final Logger logger = LoggerFactory.getLogger(BookingServices.class);

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VenueRepository venueRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private CarterRepository carterRepository;

    @Transactional
    public Booking createBooking(String name, String venueId, String bookingDate, List<String> vendorIds, List<String> carterIds) {
        logger.info("Creating booking for user name: {} and venue ID: {}", name, venueId);
        try {
            Optional<Users> userOpt = userRepository.findByName(name);
            if (userOpt.isEmpty()) {
                logger.warn("User with name '{}' not found", name);
                throw new IllegalArgumentException("User not found");
            }

            Optional<Venue> venueOpt = venueRepository.findById(venueId);
            if (venueOpt.isEmpty()) {
                logger.warn("Venue with ID '{}' not found", venueId);
                throw new IllegalArgumentException("Venue not found");
            }

            Users user = userOpt.get();
            Venue venue = venueOpt.get();

            List<Vendor> vendors = new ArrayList<>();
            double totalPrice = venue.getVenuePrice() != 0? venue.getVenuePrice() : 0.0;
            if (vendorIds != null && !vendorIds.isEmpty()) {
                for (String vendorId : vendorIds) {
                    Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
                    if (vendorOpt.isPresent()) {
                        Vendor vendor = vendorOpt.get();
                        vendors.add(vendor);
                        totalPrice += vendor.getPrice() != null ? vendor.getPrice() : 0.0;
                    } else {
                        logger.warn("Vendor with ID '{}' not found", vendorId);
                    }
                }
            }

            List<Carter> carters = new ArrayList<>();
            if (carterIds != null && !carterIds.isEmpty()) {
                for (String carterId : carterIds) {
                    Optional<Carter> carterOpt = carterRepository.findById(carterId);
                    if (carterOpt.isPresent()) {
                        Carter carter = carterOpt.get();
                        carters.add(carter);
                        totalPrice += carter.getPrice() != null ? carter.getPrice() : 0.0;
                    } else {
                        logger.warn("Carter with ID '{}' not found", carterId);
                    }
                }
            }

            Booking booking = new Booking();
            booking.setBookingId(UUID.randomUUID().toString());
            booking.setVenue(venue);
            booking.setVendors(vendors);
            booking.setCarters(carters);
            booking.setBookingDate(new SimpleDateFormat("yyyy-MM-dd").parse(bookingDate));
            booking.setStatus("Pending");
            booking.setTotalPrice(totalPrice);

            Booking savedBooking = bookingRepository.save(booking);

            user.getBookings().add(savedBooking);
            userRepository.save(user);

            logger.info("Booking created successfully with ID: {}", savedBooking.getBookingId());
            return savedBooking;
        } catch (Exception e) {
            logger.error("Error creating booking: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create booking: " + e.getMessage());
        }
    }

    public List<Booking> getBookingsByUserId(String userId) {
        logger.info("Fetching bookings for user ID: {}", userId);
        Optional<Users> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            logger.warn("User with ID '{}' not found", userId);
            return new ArrayList<>();
        }
        Users user = userOpt.get();
        List<Booking> bookings = user.getBookings();
        logger.debug("Found {} bookings for user ID: {}", bookings.size(), userId);
        return bookings;
    }

    public List<Booking> getBookingsByVenueId(String venueId) {
        logger.info("Fetching bookings for venue ID: {}", venueId);
        Optional<Venue> venueOpt = venueRepository.findById(venueId);
        if (venueOpt.isEmpty()) {
            logger.warn("Venue with ID '{}' not found", venueId);
            return new ArrayList<>();
        }
        List<Booking> bookings = bookingRepository.findByVenue(venueOpt.get());
        logger.debug("Found {} bookings for venue ID: {}", bookings.size(), venueId);
        return bookings;
    }

    @Transactional
    public boolean cancelBooking(String bookingId) {
        logger.info("Cancelling booking with ID: {}", bookingId);
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            logger.warn("Booking with ID '{}' not found", bookingId);
            return false;
        }

        Booking booking = bookingOpt.get();
        booking.setStatus("Cancelled");
        bookingRepository.save(booking);

        logger.info("Booking with ID '{}' cancelled successfully", bookingId);
        return true;
    }

    public List<Booking> getAllBookings() {
        logger.info("Fetching all bookings");
        List<Booking> bookings = bookingRepository.findAll();
        logger.debug("Found {} bookings", bookings.size());
        return bookings;
    }
}