package faang.school.projectservice.controller;

import faang.school.projectservice.dto.team.CreateMembersDto;
import faang.school.projectservice.dto.team.TeamDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.mapper.TeamMapper;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.team.TeamMemberService;
import faang.school.projectservice.service.team.TeamService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TeamController {
    private final TeamMemberService teamMemberService;
    private final TeamMemberMapper memberMapper;
    private final TeamService teamService;
    private final TeamMapper teamMapper;

    @GetMapping("/teams/{team-id}")
    public List<TeamMemberDto> getMembersForTeam(@PathVariable("team-id") Long teamId) {
        List<TeamMember> inTeam = teamMemberService.getMembersForTeam(teamId);
        return memberMapper.toDtoList(inTeam);
    }

    @GetMapping("/members")
    public List<TeamMemberDto> teamMembersForUser(@RequestParam("user-id") Long userId) {
        List<TeamMember> sameUserMembers = teamMemberService.getMembersForUser(userId);
        return memberMapper.toDtoList(sameUserMembers);
    }

    @PostMapping("/teams")
    @ResponseStatus(HttpStatus.CREATED)
    public TeamDto createTeamForProject(@RequestParam("project-id") Long projectId) {
        Team created = teamService.createTeam(projectId);
        return teamMapper.toDto(created);
    }

    @PutMapping("/teams/{team-id}")
    public List<TeamMemberDto> addMembersToTeam(@PathVariable("team-id") Long teamId,
                                                @RequestBody(required = false) @Valid CreateMembersDto dto) {
        List<TeamMember> added = teamMemberService.addToTeam(teamId, dto.getRole(), dto.getUserIds());
        return memberMapper.toDtoList(added);
    }

    @PutMapping("/members/{member-id}")
    public TeamMemberDto updateMemberNickname(@PathVariable("member-id") Long memberId,
                                              @RequestParam @Valid @NotBlank String nickname) {
        TeamMember updatedMember = teamMemberService.updateMemberNickname(memberId, nickname);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/members/{member-id}/roles-update")
    public TeamMemberDto updateMemberRoles(@PathVariable("member-id") Long memberId,
                                           @RequestParam List<TeamRole> roles) {
        TeamMember updatedMember = teamMemberService.updateMemberRoles(memberId, roles);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/projects/{project-id}/remove-members")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeMembersFromProject(@PathVariable("project-id") Long projectId,
                                         @RequestParam List<Long> userIds) {
        teamMemberService.removeFromProject(projectId, userIds);
    }

    @PostMapping("/projects/{project-id}/members")
    public List<TeamMemberDto> getTeamMembersForProject(@PathVariable("project-id") Long projectId,
                                                        @RequestParam(required = false) String nickname,
                                                        @RequestParam(required = false) TeamRole role) {
        List<TeamMember> filteredMembers = teamMemberService.getProjectMembersFiltered(projectId, nickname, role);
        return memberMapper.toDtoList(filteredMembers);
    }

    @GetMapping("/members/{memberId}")
    public TeamMemberDto getMember(@PathVariable("memberId") Long memberId) {
        TeamMember found = teamMemberService.getMemberById(memberId);
        return memberMapper.toDto(found);
    }
}
