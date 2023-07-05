package faang.school.projectservice.service;

import faang.school.projectservice.jpa.TeamMemberJpaRepository;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamMemberService {
    private final TeamMemberJpaRepository teamMemberRepository;

    public TeamMember getTeamMember(Long userId, Long projectId) {
        var teamMembers = getTeams(userId);
        var team = teamMembers.stream()
                .filter(teamEntity -> projectId.equals(teamEntity.getProject().getId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User is not a member of the project with id: " + projectId));

        return team.getTeamMembers().stream().filter(teamMember1 -> userId.equals(teamMember1.getUserId()))
                .findFirst()
                .orElseThrow(
                        () -> new RuntimeException("User is not a member of the project with id: " + projectId)
                );
    }

    public List<Team> getTeams(Long userId) {
        var teams = teamMemberRepository.findByUserId(userId).stream()
                .map(TeamMember::getTeam)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(teams)) {
            throw new RuntimeException("User is not a member of any team");
        }

        return teams;
    }

    @Transactional
    public TeamMember create(TeamMember teamMember) {
        return teamMemberRepository.save(teamMember);
    }
}
