package faang.school.projectservice.service.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
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
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamMemberService {
    private final TeamMemberRepository teamMemberRepository;
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
        Long callerId = userContext.getUserId();
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
        TeamMember toUpdate = teamMemberRepository.findByIdOrThrow(teamMemberId);
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
        TeamMember toUpdate = teamMemberRepository.findByIdOrThrow(teamMemberId);
        TeamMemberUtil.validateCallerOwnsAccount(toUpdate, userContext.getUserId());
        toUpdate.setNickname(nickname);

        TeamMember withUpdatedNickname = teamMemberRepository.save(toUpdate);
        log.info("Updated nickname({}) for member: ID = {}", nickname, withUpdatedNickname.getId());
        return withUpdatedNickname;
    }

    @Transactional
    public void removeFromProject(Long projectId, List<Long> userIds) {
        Project toBeRemovedFrom = projectRepository.findByIdThrowing(projectId);
        TeamMemberUtil.validateProjectOwner(toBeRemovedFrom, userContext.getUserId());
        List<TeamMember> membersToRemove = userServiceClient.getUsersByIds(userIds).stream()
                .map(UserDto::getId)
                .map(userId -> teamMemberRepository.findByUserIdAndProjectId(userId, projectId))
                .toList();
        teamMemberRepository.deleteAll(membersToRemove);
        projectRepository.save(toBeRemovedFrom);
        log.info("Removed users(IDs = {}) from project: ID = {}", userIds, toBeRemovedFrom.getId());
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getProjectMembersFiltered(Long projectId, String nickname, TeamRole role) {
        Project target = projectRepository.getProjectByIdOrThrow(projectId);
        Stream<TeamMember> stream = target.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream());
        if (Objects.nonNull(role)) {
            return stream
                    .filter(roleFilter(role))
                    .toList();
        } else if (Objects.nonNull(nickname)) {
            return stream
                    .filter(nicknameFilter(nickname))
                    .toList();
        }
        return stream.toList();
    }

    @Transactional(readOnly = true)
    public TeamMember getMemberById(Long memberId) {
        return teamMemberRepository.findByIdOrThrow(memberId);
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
        return member -> member.getNickname().contains(filter);
    }

    private Predicate<TeamMember> roleFilter(TeamRole filter) {
        return member -> member.getRoles().contains(filter);
    }
}
