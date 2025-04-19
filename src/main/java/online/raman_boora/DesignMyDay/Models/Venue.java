package online.raman_boora.DesignMyDay.Models;

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
@Document(collection = "venues")
public class Venue {

    @Id
    private String venueId;

    @NotBlank(message = "Venue name cannot be blank")
    @Indexed(unique = true)
    private String venueName;

    @NotBlank(message = "Venue address cannot be blank")
    private String venueAddress;

    @DBRef
    private List<Service> services = new ArrayList<>();

    @DBRef
    private List<Images> images = new ArrayList<>();
}