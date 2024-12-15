package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.service.team.TeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/teams")
@Slf4j
public class TeamController {
    private final TeamService teamService;

    @PostMapping(value = "/users/{userId}")
    public TeamDto createTeam(@RequestBody TeamDto teamDto, @PathVariable Long userId) {
        log.info("Received a request to create a team");
        return teamService.createTeam(teamDto, userId);
    }
}
