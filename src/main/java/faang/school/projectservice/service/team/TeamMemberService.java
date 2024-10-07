package faang.school.projectservice.service.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.util.TeamMemberUtil;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberJpaRepository teamMemberRepository;
    private final TeamRepository teamRepository;
    private final UserServiceClient userServiceClient;
    private final ProjectRepository projectRepository;
    private final UserContext userContext;

    @Transactional(readOnly = true)
    public List<TeamMember> getMembersForTeam(Long teamId) {
        Team target = teamRepository.findById(teamId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No team found for ID = %s", teamId)));
        return target.getTeamMembers();
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getMembersForUser(Long userId) {
        UserDto validated = userServiceClient.getUser(userId);
        return teamMemberRepository.findByUserId(validated.getId());
    }

    @Transactional
    public List<TeamMember> addToTeam(Long teamId, TeamRole role, List<Long> userIds) {
        Team toReceiveMembers = teamRepository.findById(teamId).orElseThrow(
                () -> new EntityNotFoundException(String.format("No team found for ID = %s", teamId)));
        Project teamProject = toReceiveMembers.getProject();
        long callerId = userContext.getUserId();
        TeamMember caller = teamMemberRepository.findByUserIdAndProjectId(callerId, teamProject.getId());
        TeamMemberUtil.validateProjectOwnerOrTeamLead(caller, callerId, teamProject);

        List<TeamMember> verifiedMembers = userServiceClient.getUsersByIds(userIds).stream()
                .map(dto -> createIfNotExistent(dto, teamProject.getId(), toReceiveMembers, role))
                .toList();
        teamRepository.save(toReceiveMembers);
        log.info("Added members to team(ID={})", toReceiveMembers.getId());
        return teamMemberRepository.saveAll(verifiedMembers);
    }

    @Transactional
    public TeamMember updateMemberRoles(Long teamMemberId, List<TeamRole> newRoles) {
        TeamMember toUpdate = teamMemberRepository.findById(teamMemberId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", teamMemberId)));
        Project teamProject = toUpdate.getTeam().getProject();

        TeamMember caller = teamMemberRepository.findByUserIdAndProjectId(userContext.getUserId(), teamProject.getId());
        TeamMemberUtil.validateTeamLead(caller);
        TeamMemberUtil.validateNewRolesNotEmpty(newRoles);
        toUpdate.setRoles(newRoles);

        TeamMember withUpdatedRoles = teamMemberRepository.save(toUpdate);
        log.info("Updated roles for member: ID = {}", withUpdatedRoles.getId());
        return withUpdatedRoles;
    }

    @Transactional
    public TeamMember updateMemberNickname(Long teamMemberId, String nickname) {
        TeamMember toUpdate = teamMemberRepository.findById(teamMemberId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", teamMemberId)));
        TeamMemberUtil.validateCallerOwnsAccount(toUpdate, userContext.getUserId());
        toUpdate.setNickname(nickname);

        TeamMember withUpdatedNickname = teamMemberRepository.save(toUpdate);
        log.info("Updated nickname({}) for member: ID = {}", nickname, withUpdatedNickname.getId());
        return withUpdatedNickname;
    }

    @Transactional
    public void removeFromProject(Long projectId, List<Long> userIds) {
        Project toBeRemovedFrom = projectRepository.getByIdOrThrow(projectId);
        TeamMemberUtil.validateProjectOwner(toBeRemovedFrom, userContext.getUserId());
        List<TeamMember> membersToRemove = userServiceClient.getUsersByIds(userIds).stream()
                .map(UserDto::getId)
                .map(userId -> {
                    TeamMember tm = teamMemberRepository.findByUserIdAndProjectId(userId, projectId);
                    if (Objects.isNull(tm)) {
                        throw new EntityNotFoundException(String.format("User with ID %s no registered for this project", userId));
                    }
                    return tm;
                })
                .toList();
        teamMemberRepository.deleteAll(membersToRemove);
        projectRepository.save(toBeRemovedFrom);
        log.info("Removed users(IDs = {}) from project: ID = {}", userIds, toBeRemovedFrom.getId());
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getProjectMembersFiltered(Long projectId, String nickname, TeamRole role) {
        Project target = projectRepository.getByIdOrThrow(projectId);
        return target.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(nicknameFilter(nickname))
                .filter(roleFilter(role))
                .toList();
    }

    @Transactional(readOnly = true)
    public TeamMember getMemberById(Long memberId) {
        return teamMemberRepository.findById(memberId).orElseThrow(() ->
                new EntityNotFoundException(String.format("Team member doesn't exist by id: %s", memberId)));
    }

    private TeamMember createIfNotExistent(UserDto userData, Long projectId, Team target, TeamRole role) {
        TeamMember member = teamMemberRepository.findByUserIdAndProjectId(userData.getId(), projectId);
        TeamMemberUtil.validateUserNotInProject(member, projectId);
        return TeamMember.builder()
                .userId(userData.getId())
                .nickname(userData.getUsername())
                .team(target)
                .roles(List.of(role))
                .build();
    }

    private Predicate<TeamMember> nicknameFilter(String filter) {
        return member -> Objects.isNull(filter) || member.getNickname().contains(filter);
    }

    private Predicate<TeamMember> roleFilter(TeamRole filter) {
        return member -> Objects.isNull(filter) || member.getRoles().contains(filter);
    }
}
