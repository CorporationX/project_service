package faang.school.projectservice.controller.team;


import faang.school.projectservice.model.dto.team.CreateTeamDto;
import faang.school.projectservice.model.dto.team.TeamDto;
import faang.school.projectservice.service.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/teams")
@RequiredArgsConstructor
@Validated
public class TeamController {
    private final TeamService teamService;

    @PostMapping("/new")
    public TeamDto createTeam(@RequestBody @NotNull @Validated CreateTeamDto createTeamDto) {
        return teamService.create(createTeamDto);
    }
}
