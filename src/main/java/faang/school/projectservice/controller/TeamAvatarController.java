package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamAvatarService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/team-photo")
@RequiredArgsConstructor
public class TeamAvatarController {

    private final TeamAvatarService teamAvatarService;

    @PostMapping("/user/{userId}")
    public ResponseEntity<String> addTeamAvatar(@NotNull @PathVariable Long userId,
                                                @NotNull @RequestParam("Avatar") MultipartFile file) {
        String result = "Avatar was added, avatar key is: " + teamAvatarService.addTeamAvatar(userId, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("user/{userId}")
    public ResponseEntity<String> removeTeamAvatar(@NotNull @PathVariable Long userId) {
        String result = "Avatar with key: " + teamAvatarService.removeTeamAvatar(userId) + "was deleted";

        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(result);
    }
}
