package faang.school.projectservice.controller.team;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamMemberServiceImpl;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
@RequiredArgsConstructor
@Validated
public class TeamController {

    private final TeamMemberServiceImpl teamMemberService;

    @PostMapping("/{id}/teamMembers")
    public TeamMemberDto addTeamMember(@Positive @PathVariable long id,
                                       @RequestBody TeamMemberDto teamMemberDto) {
        return teamMemberService.addTeamMember(id, teamMemberDto);
    }

    @PutMapping("/teamMembers/{id}")
    public TeamMemberDto updateTeamMember(@Positive @PathVariable long id,
                                          @RequestBody TeamMemberDto teamMemberDto) {
        return teamMemberService.updateTeamMember(id, teamMemberDto);
    }

    @DeleteMapping("/teamMembers/{id}")
    public void deleteTeamMember(@Positive @PathVariable long id) {
        teamMemberService.deleteTeamMember(id);
    }

    @GetMapping("/teamMembers/filter")
    public List<TeamMemberDto> getTeamMembersByFilter(@RequestBody TeamFilterDto filters) {
        return teamMemberService.getTeamMembersByFilter(filters);
    }

    @GetMapping("/teamMembers/all")
    public List<TeamMemberDto> getAllTeamMembers() {
        return teamMemberService.getAllTeamMembers();
    }

    @GetMapping("/teamMembers/{id}")
    public TeamMemberDto getTeamMemberById(@Positive @PathVariable long id) {
        return teamMemberService.getTeamMemberById(id);
    }
}
