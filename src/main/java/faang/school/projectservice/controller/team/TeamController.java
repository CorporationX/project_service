package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberFilterDto;
import faang.school.projectservice.dto.teammember.UpdateTeamMemberDto;
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

    @PutMapping("/{id}")
    public TeamMemberDto updateTeamMember(@RequestParam Long id, @RequestBody UpdateTeamMemberDto teamMemberDto) {
        return teamMemberService.updateTeamMember(id, teamMemberDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTeamMember(@RequestParam Long ownerId, @PathVariable Long id) {
        teamMemberService.removeTeamMember(ownerId, id);
    }

    @GetMapping
    public List<TeamMemberDto> getTeamMembers(
        @RequestParam(required = false) String role,
        @RequestParam(required = false) String projectName
    ) {
        TeamMemberFilterDto filterDto = TeamMemberFilterDto.builder()
                .role(role)
                .projectName(projectName)
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
