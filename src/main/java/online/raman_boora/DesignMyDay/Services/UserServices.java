package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Repositories.UserRepository;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserServices {

@Autowired
JwtService jwtService;

    @Autowired
    UserRepository UserRepository;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServices(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }
public String validate(Users user)
{
    Authentication authenticate =  authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
if(authenticate.isAuthenticated())
    return jwtService.generateToken(user);
else
    return "failure";
}

    public String saveUser(Users user)
    {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        UserRepository.save(user);
        return "User Saved";
    }

    public List<Users> getUsers()
    {
       return UserRepository.findAll();
//        return "User Saved";
    }

    public Optional<Users> getUserByUserId(String UserId)
    {
      return   UserRepository.findById(UserId);
//        return "User Saved";
    }

    public Optional<Users> getUserByname(String name) {

        return UserRepository.findByname(name);
    }
}
