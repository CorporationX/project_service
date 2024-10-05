package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
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

    @PostMapping("/{teamId}/members")
    public TeamMemberDto addTeamMember(@Positive @PathVariable long teamId,
                                       @RequestBody TeamMemberCreateDto teamMemberCreateDto) {
        return teamMemberService.addTeamMember(teamId, teamMemberCreateDto);
    }

    @PutMapping("/{teamId}/members/{teamMemberId}")
    public TeamMemberDto updateTeamMember(@Positive @PathVariable long teamId,
                                          @Positive @PathVariable long teamMemberId,
                                          @RequestBody TeamMemberUpdateDto teamMemberUpdateDto) {
        return teamMemberService.updateTeamMember(teamId, teamMemberId, teamMemberUpdateDto);
    }

    @DeleteMapping("/members/{teamMemberId}")
    public void deleteTeamMember(@Positive @PathVariable long teamMemberId) {
        teamMemberService.deleteTeamMember(teamMemberId);
    }

    @GetMapping("/{teamId}/members/filter")
    public List<TeamMemberDto> getTeamMembersByFilter(@Positive @PathVariable long teamId, TeamFilterDto filters) {
        return teamMemberService.getTeamMembersByFilter(teamId, filters);
    }

    @GetMapping("/members/all")
    public Page<TeamMemberDto> getAllTeamMembers(@PageableDefault Pageable pageable) {
        return teamMemberService.getAllTeamMembers(pageable);
    }

    @GetMapping("/members/{teamMemberId}")
    public TeamMemberDto getTeamMemberById(@Positive @PathVariable long teamMemberId) {
        return teamMemberService.getTeamMemberById(teamMemberId);
    }
}
