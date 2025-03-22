package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Services.UserServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

//@RequestMapping("/users")
@RestController
public class UserController {

    @Autowired
    UserServices userServices;
    @GetMapping("/users")
    public List<Users> getUsers()
    {
        return userServices.getUsers();
    }


    @PostMapping("/signup")
    public String saveUser(@RequestBody Users user)
    {
return    userServices.saveUser(user);
    }

    @PostMapping("/login")
    public String login(@RequestBody Users user)
    {
        return userServices.validate(user);
    }

@GetMapping("/user/{UserId}")
    public Users getUserByUserId(@PathVariable  String UserId)
    {
        Optional<Users> userDetails = userServices.getUserByUserId(UserId);
            return userDetails.get();
    }

    @GetMapping("/users/{name}")
    public Users getUserByname(@PathVariable  String name)
    {
        Optional<Users> userDetails = userServices.getUserByname(name);
//        if(user.isPresent())
//            return user.get();

        return userDetails.get();

    }

}
