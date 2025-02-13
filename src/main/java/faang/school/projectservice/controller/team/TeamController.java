package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.service.team.TeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/team")
@Tag(name = "Контроллер для управления командами")
public class TeamController {

    private final TeamService teamService;

    @PostMapping
    public TeamDto createTeam(@RequestBody TeamDto teamDto) {
        return teamService.createTeam(teamDto);
    }
}
