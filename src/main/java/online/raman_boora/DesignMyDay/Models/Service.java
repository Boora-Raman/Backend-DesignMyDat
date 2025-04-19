package online.raman_boora.DesignMyDay.Models;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "services")
public class Service {

    @Id
    private String serviceId;

    private String serviceName;
    private Double servicePrice;
    private String serviceDescription;
}