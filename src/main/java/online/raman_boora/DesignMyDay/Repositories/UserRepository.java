package online.raman_boora.DesignMyDay.Repositories;

import online.raman_boora.DesignMyDay.Models.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
@Repository
public interface UserRepository extends MongoRepository<Users,String> {


   public Optional<Users> findByUserId(String UserId);

    public Optional<Users> findByname(String name);

}