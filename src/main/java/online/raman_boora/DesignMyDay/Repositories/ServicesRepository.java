package online.raman_boora.DesignMyDay.Repositories;

import online.raman_boora.DesignMyDay.Models.Service;
import online.raman_boora.DesignMyDay.Models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.Optional;
@Repository
public interface ServicesRepository extends MongoRepository<Service,String> {
}



