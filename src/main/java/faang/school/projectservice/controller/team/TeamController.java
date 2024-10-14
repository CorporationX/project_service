package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.service.teammember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/team-members")
@RequiredArgsConstructor
public class TeamController {

    private final TeamMemberService teamMemberService;

    @PostMapping
    public TeamMemberDto createTeamMember(@RequestBody AddTeamMemberDto addTeamMemberDto) {
        return teamMemberService.addTeamMember(addTeamMemberDto);
    }

    @PutMapping
    public TeamMemberDto updateTeamMember(@RequestBody TeamMemberDto teamMemberDto) {
        return teamMemberService.updateTeamMember(teamMemberDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamMember(@RequestParam Long ownerId, @PathVariable Long id) {
        teamMemberService.removeTeamMember(ownerId, id);
    }

    @GetMapping
    public List<TeamMemberDto> getTeamMembers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String teamName
    ) {
        TeamMemberFilterDto filterDto = TeamMemberFilterDto.builder()
                .role(role)
                .teamName(teamName)
                .build();
        
        return teamMemberService.findTeamMembers(filterDto);
    }

    @GetMapping("/all")
    public List<TeamMemberDto> getAllTeamMembers() {
        return teamMemberService.getAllTeamMembers();
    }

    @GetMapping("/{id}")
    public TeamMemberDto getTeamMemberById(@PathVariable Long id) {
        return teamMemberService.getTeamMemberById(id);
    }
}
