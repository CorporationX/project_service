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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Team management", description = "Operations related to changing team members, and getting info about project members")
public class TeamController {
    private final TeamMemberService teamMemberService;
    private final TeamService teamService;
    private final TeamMemberMapper memberMapper;
    private final TeamMapper teamMapper;

    @PostMapping("/teams")
    @Operation(summary = "Create a new team for project", description = "Creates a new team, registered for a specific project")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created a team for the provided project ID")})
    public TeamDto createTeamForProject(@Parameter(description = "Unique project ID", required = true)
                                        @RequestParam Long projectId,
                                        @Parameter(description = "Optional info in case if team is instantiated with members at creation")
                                        @RequestBody(required = false) CreateMembersDto dto) {
        Team created = teamService.createTeam(projectId, dto.getRole(), dto.getUserIds());
        return teamMapper.toDto(created);
    }

    @PutMapping("/teams/{teamId}")
    @Operation(summary = "Adds members to an existing team", description = "Creates members based on user ID and assigns them to a team")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully created members assigned for a particular team")})
    public List<TeamMemberDto> addMembersToTeam(@Parameter(description = "Unique team ID")
                                                @PathVariable Long teamId,
                                                @Parameter(required = true, description = "User IDs indicating users to be added to a team")
                                                @RequestBody CreateMembersDto dto) {
        List<TeamMember> added = teamMemberService.addToTeam(teamId, dto.getRole(), dto.getUserIds());
        return memberMapper.toDtoList(added);
    }

    @PutMapping("/members/{memberId}")
    @Operation(summary = "Update nickname a team member", description = "Updates nickname of a single member in a team, based on the unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saves and return updated user info")})
    public TeamMemberDto updateMemberNickname(@Parameter(description = "Unique team member ID")
                                          @PathVariable Long memberId,
                                          @Parameter(description = "A new member nickname")
                                          @RequestParam String nickname) {
        TeamMember updatedMember = teamMemberService.updateMemberNickname(memberId, nickname);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/members/{memberId}")
    @Operation(summary = "Update infoof a team member", description = "Updates info about a single member in a team, based on the unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saves and return updated user info")})
    public TeamMemberDto updateMemberRoles(@Parameter(description = "Unique team member ID")
                                              @PathVariable Long memberId,
                                              @Parameter(description = "Info to be updated")
                                              @RequestParam List<TeamRole> roles) {
        TeamMember updatedMember = teamMemberService.updateMemberRoles(memberId, roles);
        return memberMapper.toDto(updatedMember);
    }

    @PutMapping("/projects/{projectId}/removeMembers")
    @Operation(summary = "Get all team members for provided user", description = "Returns all team members registered for the provided user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided user ID")})
    public void removeMembersFromProject(@PathVariable Long projectId,
                                         @RequestBody List<Long> memberIds) {
        teamMemberService.removeFromProject(projectId, memberIds);
    }

    @PostMapping("/projects/{projectId}/members")
    @Operation(summary = "Get all team members for provided user", description = "Returns all team members registered for the provided user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided user ID")})
    public List<TeamMemberDto> getTeamMembersForProject(@PathVariable Long projectId,
                                                        @RequestParam(required = false) String nickname,
                                                        @RequestParam(required = false) TeamRole role) {
        List<TeamMember> filteredMembers = teamMemberService.getFilteredMembers(projectId, nickname, role);
        return memberMapper.toDtoList(filteredMembers);
    }

    @GetMapping("/members")
    @Operation(summary = "Get all team members for provided user", description = "Returns all team members registered for the provided user ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Successfully returned members for provided user ID")})
    public List<TeamMemberDto> teamMembersForUser(@Parameter(description = "Unique user ID")
                                                  @RequestParam Long userId) {
        List<TeamMember> sameUserMembers = teamMemberService.getMembersWithSameUser(userId);
        return memberMapper.toDtoList(sameUserMembers);
    }
}
