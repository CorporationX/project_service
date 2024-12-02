package faang.school.projectservice.service;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.teamMember.CreateTeamMemberDto;
import faang.school.projectservice.dto.teamMember.ResponseTeamMemberDto;
import faang.school.projectservice.dto.teamMember.UpdateTeamMemberDto;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.mapper.TeamMemberMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamMemberActions;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamMemberService {

    private final TeamMemberRepository teamMemberRepository;
    private final TeamService teamService;
    private final ProjectService projectService;
    private final StageService stageService;
    private final TeamMemberMapper teamMemberMapper;
    private final TeamMemberJpaRepository teamMemberJpaRepository;
    private final UserServiceClient userServiceClient;
    private final UserContext userContext;

    @Transactional
    public ResponseTeamMemberDto addTeamMember(CreateTeamMemberDto teamMemberDto,
                                               long teamId) {
        long creatorId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        Project project = team.getProject();
        ensureHasAccess(creatorId, List.of(TeamRole.OWNER, TeamRole.TEAMLEAD), TeamMemberActions.ADD_MEMBER, project.getId());

        TeamMember teamMember = createOrUpdateRolesTeamMember(teamMemberDto, team, project);
        return teamMemberMapper.toResponseDto(teamMember);
    }

    @Transactional
    public ResponseTeamMemberDto updateTeamMember(UpdateTeamMemberDto teamMemberDto,
                                                  long teamId,
                                                  long memberId) {
        long updaterId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        Project project = team.getProject();
        TeamMember memberToUpdate = teamMemberRepository.findById(memberId);

        if (hasAccess(updaterId, List.of(TeamRole.TEAMLEAD), project.getId())) {
            updateRolesIfPresent(teamMemberDto, memberToUpdate);
            updateStagesIfPresent(teamMemberDto, memberToUpdate);
        } else if (updaterId == memberToUpdate.getUserId()) {
            updateSelfSurname(updaterId, teamMemberDto);
        }
        teamMemberRepository.save(memberToUpdate);
        return teamMemberMapper.toResponseDto(memberToUpdate);
    }

    @Transactional
    public void deleteTeamMember(long memberId, long teamId) {
        long deleterId = userContext.getUserId();
        Team team = teamService.getTeamById(teamId);
        Project project = team.getProject();

        ensureHasAccess(deleterId, List.of(TeamRole.OWNER), TeamMemberActions.REMOVE_MEMBER, project.getId());
        TeamMember teamMember = teamMemberRepository.findById(memberId);

        removeTeamMemberFromTeam(team, teamMember);
        teamMemberRepository.delete(teamMember);
    }

    public List<ResponseTeamMemberDto> getFilteredTeamMembers(String name, TeamRole role, long projectId) {
        List<TeamMember> teamMembers = teamMemberRepository.findAllMembersByProjectId(projectId);
        List<Long> userIds = getUserIdsFromTeams(teamMembers);
        List<UserDto> users = getUsersByIds(userIds);

        List<TeamMember> filteredMembers = filterTeamMembers(teamMembers, users, name, role);

        return teamMemberMapper.toResponseDto(filteredMembers);
    }

    public List<ResponseTeamMemberDto> getTeamMembersByTeamId(long teamId) {
        Team team = teamService.getTeamById(teamId);
        return teamMemberMapper.toResponseDto(team.getTeamMembers());
    }

    public boolean curatorHasNoAccess(Long curatorId) {
        TeamMember teamMember = teamMemberRepository.findById(curatorId);
        return !teamMember.isCurator();
    }

    @Transactional
    public void addTeamMemberToTeam(Team team, TeamMember teamMember) {
        List<TeamMember> updatedTeamMembers = new ArrayList<>(team.getTeamMembers());
        updatedTeamMembers.add(teamMember);
        team.setTeamMembers(updatedTeamMembers);
        teamMemberRepository.updateTeamMembers(team.getId(), updatedTeamMembers);
    }

    @Transactional
    public void removeTeamMemberFromTeam(Team team, TeamMember teamMember) {
        List<TeamMember> updatedTeamMembers = new ArrayList<>(team.getTeamMembers());
        updatedTeamMembers.remove(teamMember);
        team.setTeamMembers(updatedTeamMembers);
        teamMemberRepository.updateTeamMembers(team.getId(), updatedTeamMembers);
    }

    private boolean hasAccess(Long userId, List<TeamRole> requiredRoles, Long projectId) {
        TeamMember teamMember = findTeamMemberByUserAndProjectId(userId, projectId);
        return requiredRoles.stream()
                .anyMatch(teamMember::hasRole);
    }

    private void ensureHasAccess(Long userId, List<TeamRole> requiredRoles,
                                 TeamMemberActions action, Long projectId) {
        if (!hasAccess(userId, requiredRoles, projectId)) {
            log.warn("Member with ID {} has no access to {}", userId, action.getDescription());
            throw new EntityNotFoundException(String.format(
                    "Member with ID %d has no access to %s", userId, action.getDescription())
            );
        }
    }

    private boolean isUserAlreadyInProject(Long memberId, Project project) {
        return project.getTeams().stream()
                .anyMatch(team -> team.getTeamMembers().stream()
                        .anyMatch(teamMember -> teamMember.isSameMember(memberId))
                );
    }

    private TeamMember createOrUpdateRolesTeamMember(CreateTeamMemberDto teamMemberDto, Team team, Project project) {
        TeamMember teamMember = teamMemberMapper.toEntity(teamMemberDto);
        if (isUserAlreadyInProject(teamMember.getId(), project)) {
            TeamMember existingTeamMember = teamMemberRepository.findById(teamMember.getId());
            existingTeamMember.setRoles(teamMember.getRoles());
            return existingTeamMember;
        }
        teamMember.setTeam(team);
        addTeamMemberToTeam(team, teamMember);
        teamMemberRepository.save(teamMember);
        return teamMember;
    }

    private TeamMember findTeamMemberByUserAndProjectId(Long memberId, Long projectId) {
        TeamMember teamMember = teamMemberRepository.findByUserIdAndProjectId(memberId, projectId);

        if(teamMember == null){
            log.error("Team member with user ID {} does not exist", memberId);
            throw  new EntityNotFoundException(String.format("Team member with user ID %d does not exist", memberId));
        }

        return teamMember;
    }

    private void updateRolesIfPresent(UpdateTeamMemberDto teamMemberDto, TeamMember teamMember) {
        if (teamMemberDto.roles() != null) {
            teamMember.setRoles(teamMemberDto.roles());
        }
    }

    private void updateStagesIfPresent(UpdateTeamMemberDto teamMemberDto, TeamMember teamMember) {
        if (teamMemberDto.stageIds() != null) {
            List<Stage> stages = stageService.getStagesByIds(teamMemberDto.stageIds());
            teamMember.setStages(stages);
        }
    }

    private void updateSelfSurname(long updaterId, UpdateTeamMemberDto teamMemberDto) {
        UserDto user = userServiceClient.getUser(updaterId);
        user.setUsername(teamMemberDto.username());
    }

    private List<Long> getUserIdsFromTeams(List<TeamMember> teamMembers) {
        return teamMembers.stream()
                .map(TeamMember::getId)
                .toList();
    }

    private List<UserDto> getUsersByIds(List<Long> userIds) {
        return userServiceClient.getUsersByIds(userIds);
    }

    private UserDto findUserById(List<UserDto> users, Long userId) {
        return users.stream()
                .filter(user -> user.isSameUser(userId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    private List<TeamMember> filterTeamMembers(List<TeamMember> teamMembers, List<UserDto> users, String name, TeamRole role) {
        return teamMembers.stream()
                .filter(member -> {
                    UserDto userDto = findUserById(users, member.getId());
                    return userDto.containsUsername(name) && member.hasRole(role);
                })
                .toList();
    }

    @Transactional
    public TeamMember getTeamMember(long teamMemberId) {
        return teamMemberRepository.findById(teamMemberId);
    }

    public TeamMember getProjectMember(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        if (teamMember == null) {
            log.error("TeamMember not found for userId={} and projectId={}", userId , projectId);
            throw new EntityNotFoundException("TeamMember not found");
        }
        return teamMember;
    }

    public boolean teamMemberInProjectNotExists(long userId, long projectId) {
        TeamMember teamMember = teamMemberJpaRepository.findByUserIdAndProjectId(userId, projectId);
        return teamMember == null;
    }
}
