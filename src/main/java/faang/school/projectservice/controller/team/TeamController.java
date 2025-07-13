package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.service.team.TeamService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vi/team")
@Tag(name = "Контроллер для управления командами")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/{authorId}")
    public TeamDto createTeam(@PathVariable Long authorId, @RequestBody TeamDto teamDto) {
        return teamService.createTeam(authorId, teamDto);
    }
}
