package online.raman_boora.DesignMyDay.Services;

import online.raman_boora.DesignMyDay.Models.Images;
import online.raman_boora.DesignMyDay.Models.Users;
import online.raman_boora.DesignMyDay.Repositories.ImagesRepository;
import online.raman_boora.DesignMyDay.Repositories.UserRepository;
import online.raman_boora.DesignMyDay.configurations.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class UserServices {

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository UserRepository;

    @Autowired
    FileStorageConfig fileStorageConfig;

    @Autowired
    ImagesRepository imagesRepository;

    private final AuthenticationManager authenticationManager;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public UserServices(AuthenticationManager authenticationManager, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.authenticationManager = authenticationManager;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public String validate(Users user) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword()));
        if (authenticate.isAuthenticated()) {
            return jwtService.generateToken(user);
        }
        return null;
    }

    public String saveUser(Users user, MultipartFile image) throws IOException {
        Images img = null;
        if (image != null && !image.isEmpty()) {
            if (!isValidImage(image)) {
                throw new IllegalArgumentException("Invalid image type or size. Only JPEG/PNG allowed, max 5MB.");
            }

            String uniqueFileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path filePath = Paths.get(fileStorageConfig.getUserUploadDir(), uniqueFileName);
            try {
                Files.write(filePath, image.getBytes());
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image: " + e.getMessage(), e);
            }

            img = new Images();
            img.setImgid(UUID.randomUUID().toString());
            img.setImgName(uniqueFileName);
            imagesRepository.save(img);
        }

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setImages(img);
        UserRepository.save(user);
        return "User Saved";
    }

    public List<Users> getUsers() {
        return UserRepository.findAll();
    }

    public Optional<Users> getUserByUserId(String UserId) {
        return UserRepository.findById(UserId);
    }

    public Optional<Users> getUserByname(String name) {
        return UserRepository.findByName(name);
    }

    private boolean isValidImage(MultipartFile image) {
        String contentType = image.getContentType();
        long maxSize = 5 * 1024 * 1024; // 5MB
        return contentType != null &&
                (contentType.equals("image/jpeg") || contentType.equals("image/png")) &&
                image.getSize() <= maxSize;
    }
}