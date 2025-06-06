package faang.school.projectservice.controller.avatar;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.service.avatar.TeamAvatarService;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Objects;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/v1/teams/avatars/")
public class TeamAvatarController {
    private final TeamAvatarService teamAvatarService;
    private static final Long MAX_AVATAR_SIZE = 5L * 1024L * 1024L; // 5 MB

    @PostMapping("{teamId}/create")
    public TeamDto createAvatar(@NotNull @Positive @PathVariable Long teamId,
                                @RequestBody MultipartFile avatar) {
        log.info("Start method createAvatar in TeamAvatarController " +
                "with avatar: {} and teamId: {}", avatar, teamId);
        validateAvatar(avatar);
        return teamAvatarService.createAvatar(avatar, teamId);
    }

    @GetMapping("teamId/{teamId}")
    public InputStream getTeamAvatar(@NotNull @Positive @PathVariable Long teamId) {
        log.info("Start method getTeamAvatar in TeamAvatarController with teamId: {}", teamId);
        return teamAvatarService.getTeamAvatar(teamId);
    }

    @DeleteMapping("team/{teamId}/user/{userId}")
    public ResponseEntity<String> deleteAvatar(@NotNull @Positive @PathVariable Long teamId,
                                             @NotNull @Positive @PathVariable Long userId) {
        log.info("Start method deleteAvatar in TeamAvatarController with teamId: {}", teamId);
        teamAvatarService.deleteAvatar(teamId, userId);
        return ResponseEntity.ok("Avatar image deleted successfully!");
    }

    private void validateAvatar(MultipartFile avatar) {
        if (Objects.isNull(avatar) || avatar.isEmpty()) {
            log.error("Empty or null avatar file!");
            throw new DataValidationException("Avatar file cannot be null or empty!");
        } else if (avatar.getSize() > MAX_AVATAR_SIZE) {
            log.error("Avatar file size exceeds the maximum limit of 5 MB!");
            throw new DataValidationException("Avatar file size exceeds the maximum limit of 5 MB!");
        } else if (Objects.isNull(avatar.getContentType()) || !avatar.getContentType().endsWith("image/jpeg") &&
                !avatar.getContentType().endsWith("image/png")) {
            log.error("Invalid avatar file type: {}", avatar.getContentType());
            throw new DataValidationException("Invalid avatar file type! Only JPEG and PNG are allowed.");
        }
        log.info("Successfully validated avatar file: {}", avatar.getOriginalFilename());
    }
}
