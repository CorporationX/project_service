package faang.school.projectservice.controller;

import faang.school.projectservice.dto.client.TeamResourceDto;
import faang.school.projectservice.service.TeamResourceService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/teams")
@RequiredArgsConstructor
public class TeamResourceController {

    private final TeamResourceService teamResourceService;

    @PostMapping("/{teamId}/avatar")
    public ResponseEntity<TeamResourceDto> uploadAvatar(@PathVariable @Min(1) Long teamId,
                                                        @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(teamResourceService.uploadAvatar(teamId, file));
    }

    @DeleteMapping("/{teamId}/avatar")
    public ResponseEntity<Void> deleteAvatar(@PathVariable @Min(1) Long teamId, @RequestParam @Min(1) Long userId) {
        teamResourceService.deleteAvatar(teamId, userId);
        return ResponseEntity.noContent().build();
    }
}
