package faang.school.projectservice.controller;

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

import faang.school.projectservice.service.TeamAvatarServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/avatar/team")
@RequiredArgsConstructor
public class TeamAvatarController {
    private final TeamAvatarServiceImpl teamAvatarService;

    @PostMapping("/{teamId}")
    public void upload(@PathVariable long teamId, @RequestParam MultipartFile file) {
        teamAvatarService.uploadFile(teamId, file);
    }

    @GetMapping(path = "/{teamId}", produces = "application/octet-stream")
    public ResponseEntity<byte[]> downloadImage(@PathVariable long teamId) {
        byte[] imageBytes = null;
        String contentType = "";
        try {
            imageBytes = teamAvatarService.downloadFile(teamId).readAllBytes();
            contentType = teamAvatarService.getAvatarContentType(teamId);
        } catch (Exception e) {
            log.error("Error downloading image: {}", e.getMessage());
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<String> delete(@PathVariable long teamId) {
        teamAvatarService.deleteFile(teamId);
        return ResponseEntity.ok("File deleted successfully");
    }
}
