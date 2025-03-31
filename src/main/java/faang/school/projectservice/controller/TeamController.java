package faang.school.projectservice.controller;

import faang.school.projectservice.service.team.TeamAvatarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
@Slf4j
public class TeamController {
    private final TeamAvatarService teamAvatarService;

    @PostMapping("/{teamId}/upload/avatar")
    public void uploadAvatar(@PathVariable Long teamId,
                             @RequestHeader(name = "x-user-id") Long userId,
                             @RequestParam("file") MultipartFile file) {
        teamAvatarService.uploadAvatar(teamId, file, userId);
    }

    @GetMapping("/{teamId}/avatar")
    public ResponseEntity<InputStreamResource> getAvatar(@PathVariable Long teamId) {
        InputStream inputStream = teamAvatarService.getAvatar(teamId);
        InputStreamResource resource = new InputStreamResource(inputStream);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    @DeleteMapping("/{teamId}/delete/avatar")
    public void deleteAvatar(@PathVariable Long teamId, @RequestHeader(name = "x-user-id") Long userId) {
        teamAvatarService.deleteAvatar(teamId, userId);
    }
}
