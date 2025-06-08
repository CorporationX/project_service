package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamAvatarService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/teamPhoto")
@RequiredArgsConstructor
public class TeamAvatarController {

    private final TeamAvatarService teamAvatarService;

    @PostMapping("user/{userId}")

    public ResponseEntity<String> addTeamAvatar(@NotNull @PathVariable Long userId,
                                                @NotNull @RequestParam("Avatar") MultipartFile file) throws Exception {
        return teamAvatarService.addTeamAvatar(userId, file);
    }

    @DeleteMapping("user/{userId}")
    public ResponseEntity<String> removeTeamAvatar(@NotNull @PathVariable Long userId) throws Exception {
        return teamAvatarService.removeTeamAvatar(userId);

    }
}
