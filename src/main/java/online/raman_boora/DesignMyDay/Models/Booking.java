package online.raman_boora.DesignMyDay.Models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {

    @Id
    private String bookingId;

    @NotNull(message = "Venue cannot be null")
    @DBRef
    private Venue venue; // Venue that was booked

    @NotNull(message = "Services cannot be null")
    @DBRef
    private List<Service> services = new ArrayList<>(); // Services selected during booking

    @NotNull(message = "Booking date cannot be null")
    private Date bookingDate;

    @NotBlank(message = "Status cannot be blank")
    private String status; // e.g., "Confirmed", "Cancelled", "Pending"
}