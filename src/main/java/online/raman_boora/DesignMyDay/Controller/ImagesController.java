package online.raman_boora.DesignMyDay.Controller;

import online.raman_boora.DesignMyDay.configurations.FileStorageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
public class ImagesController {

    @Autowired
    private FileStorageConfig fileStorageConfig;

    @GetMapping("/{imgName}")
    public ResponseEntity<Resource> getImage(@PathVariable String imgName) throws IOException {
        Path userFilePath = Paths.get(fileStorageConfig.getUserUploadDir(), imgName);
        Path venueFilePath = Paths.get(fileStorageConfig.getVenueUploadDir(), imgName);
        Resource resource = null;

        if (Files.exists(userFilePath)) {
            resource = new UrlResource(userFilePath.toUri());
        } else if (Files.exists(venueFilePath)) {
            resource = new UrlResource(venueFilePath.toUri());
        }

        if (resource != null && resource.exists()) {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
}