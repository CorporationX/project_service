package faang.school.projectservice.controller;

import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/{id}/avatar")
    public void uploadAvatar(@PathVariable long id, @RequestParam MultipartFile file) {
        teamService.uploadAvatar(id, file);
    }

    @DeleteMapping("/{id}/avatar")
    public void deleteAvatar(@PathVariable long id) {
        teamService.deleteAvatar(id);
    }
}
