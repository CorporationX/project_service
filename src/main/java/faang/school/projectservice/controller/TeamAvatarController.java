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
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(teamAvatarService.addTeamAvatar(userId, file));            //Зачем делать дто для ключа. Если стрингой можно передать. Вроде говорили же что для небольшого объекта дто лучше не создавать.
    }

    @DeleteMapping("user/{userId}")
    public ResponseEntity<String> removeTeamAvatar(@NotNull @PathVariable Long userId) {
        teamAvatarService.removeTeamAvatar(userId);

        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
