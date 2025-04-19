package online.raman_boora.DesignMyDay.Models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "services")
public class Service {

    @Id
    private String serviceId;

    @NotBlank(message = "Service name cannot be blank")
    private String serviceName;

    @NotBlank(message = "Service description cannot be blank")
    private String serviceDescription;

    @Min(value = 0, message = "Service price cannot be negative")
    private double servicePrice;
}