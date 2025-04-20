package faang.school.projectservice.controller;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.team.TeamCreateDto;
import faang.school.projectservice.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/teams")
public class TeamController {
    private final TeamService service;
    private final UserContext userContext;

    @PostMapping("/avatar/{id}")
    public void upload(@NotNull @RequestBody MultipartFile file, @PathVariable Long id) {
        service.upload(file, id);
    }

    @DeleteMapping("/avatar/{id}")
    public void delete(@PathVariable Long id) {
        service.deleteAvatar(id, userContext.getUserId());
    }

    @PostMapping
    public void addTeamOnProject(@Valid @RequestBody TeamCreateDto teamDto) {
        service.addTeamOnProject(teamDto, userContext.getUserId());
    }
}
