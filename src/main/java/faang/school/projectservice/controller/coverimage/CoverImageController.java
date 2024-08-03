package faang.school.projectservice.controller.coverimage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import faang.school.projectservice.service.coverimage.CoverImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/cover-image")
public class CoverImageController {
    private final CoverImageService coverImageService;

    @PostMapping("/{projectId}")
    public MultipartFile create(@PathVariable Long projectId, @RequestParam("file") MultipartFile file) {
        return coverImageService.create(projectId, file);
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> download(@PathVariable String key) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return ResponseEntity
                    .status(200)
                    .headers(headers)
                    .body(coverImageService.downloadImage(key).readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
