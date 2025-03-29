package online.raman_boora.DesignMyDay.Models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.lang.NonNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
@Data
@AllArgsConstructor
@NoArgsConstructor

@Document
public class Users {

    @Id
    private String UserId;
    @NonNull
    @JsonProperty("username")
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String password;

}

//bhairanpur godown delivery
//manish rawat 2 packet
//9+gst