package faang.school.projectservice.service.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {
    private final TeamRepository teamRepository;


    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team doesn't exist by id: " + teamId));
    }

    public Map<Boolean, TeamMember> checkParticipationUserInTeams(Long userId, List<Team> teams) {
        return teams.stream()
                .map(team -> checkParticipationUserInTeam(userId, team))
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, replacement) -> replacement
                ));
    }


    public Map<Boolean, TeamMember> checkParticipationUserInTeam(Long userId, Team team) {
        return team.getTeamMembers().stream()
                .collect(Collectors.toMap(
                        teamMember -> teamMember.getUserId().equals(userId),
                        teamMember -> teamMember
                ));
    }
}
