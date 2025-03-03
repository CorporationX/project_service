package faang.school.projectservice.controller;

import faang.school.projectservice.s3.S3Service;
import faang.school.projectservice.s3.S3ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@AllArgsConstructor
@RestController
@RequestMapping("api/v1")
public class S3Controller {
    private final S3Service s3Service;

    @PostMapping("/users/{id}/vacancy-cover")
    public ResponseEntity<String> uploadFile(@PathVariable("id") Long userId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            String keyName = file.getOriginalFilename();
            InputStream resizedImage = S3ServiceImpl.resizeImage(file, 512, "jpg");
            long contentLength = resizedImage.available();

            s3Service.uploadToS3(keyName, resizedImage, contentLength, file);
            return ResponseEntity.ok("File uploaded successfully: " + keyName);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("File upload failed: " + e.getMessage());
        }
    }
}
