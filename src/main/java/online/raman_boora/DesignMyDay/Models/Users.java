package online.raman_boora.DesignMyDay.Models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "users")
public class Users {

    @Id
    private String userId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email should be valid")
    @Indexed(unique = true) // Ensures email uniqueness in MongoDB
    private String email;

    @NotBlank(message = "Password cannot be blank")
    private String password; // Consider encrypting this in production

    @DBRef
    private List<Booking> bookings = new ArrayList<>(); // All bookings made by the user

    @DBRef
    private List<Venue> savedVenues = new ArrayList<>(); // User can save customized or preferred venues
}