package online.raman_boora.DesignMyDay.Models;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
//@Document(collection = "images")
public class Images {
    @Id
    String imgid;
    String imgName;
}