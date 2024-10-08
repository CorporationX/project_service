package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;

    public Optional<TeamMember> checkParticipationUserInTeams(Long userId, List<Team> teams) {
        return teams.stream()
                .map(team -> checkParticipationUserInTeam(userId, team))
                .flatMap(Optional::stream)
                .findFirst();
    }

    public Optional<TeamMember> checkParticipationUserInTeam(Long userId, Team team) {
        return team.getTeamMembers().stream()
                .filter(teamMember -> teamMember.getUserId().equals(userId))
                .findFirst();
    }
}
