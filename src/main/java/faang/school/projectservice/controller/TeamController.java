package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team/")
public class TeamController {
    private final TeamService teamService;

    @PostMapping("")
    public TeamDto createTeam(@RequestBody TeamDto teamDto){
        return teamService.createTeam(teamDto);
    }
}
