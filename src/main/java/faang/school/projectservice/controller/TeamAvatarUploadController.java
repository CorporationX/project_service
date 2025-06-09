package faang.school.projectservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import faang.school.projectservice.service.TeamAvatarUploadServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/avatar/team")
@RequiredArgsConstructor
public class TeamAvatarUploadController {
    private final TeamAvatarUploadServiceImpl teamAvatarUploadService;

    @Value("${team-avatar-file.maxSize}")
    private Long avatarMaxSizeValue;
    
    @PostMapping("/{teamId}")
    public ResponseEntity<String> upload(@PathVariable long teamId, @RequestParam MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("The file is not attached or it is not an image.");
        }

        if (file.getSize() > avatarMaxSizeValue) {
            throw new IllegalArgumentException("The file is too large.");
        }

        teamAvatarUploadService.uploadFile(teamId, file);

        return ResponseEntity.ok("File uploaded successfully");
    }

    @GetMapping(path = "/{teamId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadImage(@PathVariable long teamId) {
        byte[] imageBytes = null;
        try {
            imageBytes = teamAvatarUploadService.downloadFile(teamId).readAllBytes();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> delete(@PathVariable long teamId) {
        teamAvatarUploadService.deleteFile(teamId);
        return ResponseEntity.ok("File uploaded successfully");
    }
}
