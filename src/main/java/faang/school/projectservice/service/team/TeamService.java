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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final UserServiceClient userServiceClient;

    @Transactional
    public Team createTeam(Long projectId, TeamRole role, List<Long> userIds) {
        Project project = projectRepository.getProjectByIdOrThrow(projectId);
        Team team = new Team();
        team.setProject(project);
        if (Objects.nonNull(userIds) && !userIds.isEmpty()) {
            teamRepository.save(team);
            List<TeamMember> members = getMockedUsers(userIds).stream()
//            List<TeamMember> members = userServiceClient.getUsersByIds(userIds).stream()
                    .map(dto -> TeamMember.builder()
                            .userId(dto.getId())
                            .team(team)
                            .nickname(dto.getUsername())
                            .roles(List.of(role))
                            .build())
                    .toList();
            List<TeamMember> saved = teamMemberRepository.saveAll(members);
            team.setTeamMembers(saved);
        }

        project.getTeams().add(team);

        projectRepository.save(project);
        log.info("Created team(teamID = {}) for project(projectID = {})", team.getId(), projectId);
        return teamRepository.save(team);
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
