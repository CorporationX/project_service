package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/teams")
public class TeamController {

    private final TeamService service;

    @PostMapping("/team/create")
    public TeamDto createTeam(@RequestBody @Valid TeamDto dto) {
        return service.createTeamByOwner(dto);
    }

    @PostMapping("/add")
    public TeamMemberDto addMembers(@RequestBody @Valid TeamMemberDto dto) {
        return service.addMembersInTeam(dto);
    }
}
