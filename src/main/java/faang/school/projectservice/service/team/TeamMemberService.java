package faang.school.projectservice.service.team;

import faang.school.projectservice.client.UserServiceClient;
import faang.school.projectservice.dto.client.UserDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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

    @Transactional
    public List<TeamMember> addToTeam(Long teamId, TeamRole role, List<Long> userIds) {
        Team toReceiveMembers = teamRepository.findById(teamId).orElseThrow(
                () -> new EntityNotFoundException("Team with id " + teamId + " not found"));
        Project teamProject = toReceiveMembers.getProject();
        TeamMember caller = teamMemberRepository.findByUserIdAndProjectId(null, teamProject.getId())
                .orElseThrow(() -> new IllegalStateException("Caller not registered for this project"));
        validateCallerIsTeamLead(caller);
        List<TeamMember> verifiedMembers = getMockedUsers(userIds)
//        List<TeamMember> verifiedMembers = userServiceClient.getUsersByIds(userIds)
                .stream()
                .map(dto -> verifyMember(dto, teamProject.getId(), toReceiveMembers, role))
                .toList();
        teamRepository.save(toReceiveMembers);
        log.info("Added members to team(ID={})", toReceiveMembers.getId());
        return teamMemberRepository.saveAll(verifiedMembers);
    }

    @Transactional
    public TeamMember updateMemberRoles(Long teamMemberId, List<TeamRole> roles) {
        TeamMember toUpdate = teamMemberRepository.findByIdOrThrow(teamMemberId);
        Project contributingTo = toUpdate.getTeam().getProject();
        // x-user-id
        TeamMember caller = teamMemberRepository.findByUserIdAndProjectId(null, contributingTo.getId()).orElseThrow(
                () -> {
                    String message = String.format("No member found for user ID = %s and project ID = %s", null, contributingTo.getId());
                    return new IllegalStateException(message);
                });
        validateCallerIsTeamLead(caller);
        if (roles.isEmpty()) {
            throw new IllegalArgumentException("Team member must have at least one role");
        }
        List<TeamRole> updatedRoles = toUpdate.getRoles().stream()
                        .filter(roles::contains)
                        .toList();
        toUpdate.setRoles(updatedRoles);
        return teamMemberRepository.save(toUpdate);
    }

    @Transactional
    public TeamMember updateMemberNickname(Long teamMemberId, String nickname) {
        TeamMember toUpdate = teamMemberRepository.findByIdOrThrow(teamMemberId);
        if (!toUpdate.getUserId().equals(null)) {
            throw new SecurityException("Calling user is not a member to be updated");
        }
        toUpdate.setNickname(nickname);
        return teamMemberRepository.save(toUpdate);
    }

    @Transactional
    public void removeFromProject(Long projectId, List<Long> userIds) {
        TeamMember caller = teamMemberRepository.findByUserIdAndProjectId(null, projectId).orElseThrow(
                () -> new IllegalStateException("Calling user is not registered for this project"));
        validateCallerIsTeamLead(caller);
        List<TeamMember> membersToRemove = userIds.stream()
                .map(userId -> teamMemberRepository.findByUserIdAndProjectId(projectId, userId)
                        .orElseThrow(() -> new EntityNotFoundException("Team member with id " + userId + " not found")))
                .toList();
        teamMemberRepository.deleteAll(membersToRemove);
    }

    @Transactional(readOnly = true)
    public List<TeamMember> getFilteredMembers(Long projectId, String nickname, TeamRole role) {
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
    public List<TeamMember> getMembersWithSameUser(Long userId) {
        UserDto validated = userServiceClient.getUser(userId);
        return teamMemberRepository.findByUserId(validated.getId());
    }

    private TeamMember verifyMember(UserDto userData, Long projectId, Team target, TeamRole role) {
        Optional<TeamMember> optional = teamMemberRepository.findByUserIdAndProjectId(userData.getId(), projectId);
        if (optional.isEmpty()) {
            return TeamMember.builder()
                    .userId(userData.getId())
                    .nickname(userData.getUsername())
                    .team(target)
                    .roles(List.of(role))
                    .build();
        }
        TeamMember existing = optional.get();
        if (existing.getRoles().contains(role)) {
            throw new IllegalStateException("Team member with user ID" + userData.getId() + "already has this role on the project.");
        }
        existing.getRoles().add(role);
        return existing;
    }

    private Predicate<TeamMember> nicknameFilter(String filter) {
        return member -> member.getNickname().equals(filter);
    }

    private Predicate<TeamMember> roleFilter(TeamRole filter) {
        return member -> member.getRoles().contains(filter);
    }

    private void validateCallerIsTeamLead(TeamMember caller) {
        if (!caller.getRoles().contains(TeamRole.TEAM_LEAD)) {
            throw new SecurityException("Caller does not have team lead privileges");
        }
    }

    private List<UserDto> getMockedUsers(List<Long> userIds) {
        return userIds.stream()
                .map(l -> UserDto.builder()
                        .id(l)
                        .username("member_fuck_knows_who_" + l)
                        .email(l + "@fuck-you.com")
                        .build())
                .toList();
    }
}
