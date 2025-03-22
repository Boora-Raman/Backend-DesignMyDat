package online.raman_boora.DesignMyDay.configurations;


import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;
@Component
public class CustomUserDetailsService implements UserDetailsService {

//    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Users> userDetails = userRepository.findByname(username);
        if (userDetails.isEmpty()) {
//            logger.error("User Not Found: {}", username);
            throw new UsernameNotFoundException("User Not Found");
        }
        return new CustomUserDetails(userDetails.get());
    }
}

