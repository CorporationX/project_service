package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
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

    private final TeamMemberService service;

    @PostMapping("/add")
    public TeamMemberDto publishCampaign(@RequestBody @Valid TeamMemberDto dto) {
        return service.addMembersInTeam(dto);
    }
}
