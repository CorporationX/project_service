package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.service.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/teams")
@RequiredArgsConstructor
public class TeamController {
    private final TeamMemberService teamMemberService;

    @PostMapping
    public TeamMemberDto addMember(@RequestBody TeamMemberDto teamMemberDto,
                                                   @RequestParam Long projectId,
                                                   @RequestParam Long requesterId) {
        return teamMemberService.addMember(teamMemberDto, projectId, requesterId);
    }

    @PutMapping
    public TeamMemberDto updateMember(@RequestBody TeamMemberDto teamMemberDto,
                                                      @RequestParam Long requesterId,
                                                      @RequestParam Long projectId) {
        return teamMemberService.updateMember(teamMemberDto, requesterId, projectId);
    }

    @DeleteMapping("/{memberId}")
    public void removeMember(@PathVariable Long memberId,
                                             @RequestParam Long requesterId,
                                             @RequestParam Long projectId) {
        teamMemberService.removeMember(memberId, requesterId, projectId);
    }

    @GetMapping("/project/{projectId}")
    public List<TeamMemberDto> getProjectMembers(@PathVariable Long projectId,
                                                                 @RequestParam(required = false) String role,
                                                                 @RequestParam(required = false) String name) {
        return teamMemberService.getProjectMembers(projectId, role, name);
    }

    @GetMapping
    public List<TeamMemberDto> getAllMembers() {
        return teamMemberService.getAllMembers();
    }

    @GetMapping("/{memberId}/{projectId}")
    public TeamMemberDto getMemberById(@PathVariable Long memberId,
                                                       @PathVariable Long projectId) {
        return teamMemberService.getMemberById(memberId, projectId);
    }
}
