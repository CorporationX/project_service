package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Validated
public class TeamController {

    private final TeamMemberService teamMemberService;

    @PostMapping("/{id}/members")
    public TeamMemberDto addTeamMember(@Positive @PathVariable long teamId,
                                       @RequestBody TeamMemberDto teamMemberDto) {
        return teamMemberService.addTeamMember(teamId, teamMemberDto);
    }

    @PutMapping("/members/{id}")
    public TeamMemberDto updateTeamMember(@Positive @PathVariable long teamId,
                                          @RequestBody TeamMemberDto teamMemberDto) {
        return teamMemberService.updateTeamMember(teamId, teamMemberDto);
    }

    @DeleteMapping("/members/{id}")
    public void deleteTeamMember(@Positive @PathVariable long userId, @Positive @PathVariable long teamId) {
        teamMemberService.deleteTeamMember(userId, teamId);
    }

    @GetMapping("/members/filter")
    public List<TeamMemberDto> getTeamMembersByFilter(@Positive @PathVariable long teamId, TeamFilterDto filters) {
        return teamMemberService.getTeamMembersByFilter(teamId, filters);
    }

    @GetMapping("/members/all")
    public Page<TeamMemberDto> getAllTeamMembers(@PageableDefault Pageable pageable) {
        return teamMemberService.getAllTeamMembers(pageable);
    }

    @GetMapping("/members/{id}")
    public TeamMemberDto getTeamMemberById(@Positive @PathVariable long teamId) {
        return teamMemberService.getTeamMemberById(teamId);
    }
}
