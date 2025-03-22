package online.raman_boora.DesignMyDay.Models;

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
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String password;

}
