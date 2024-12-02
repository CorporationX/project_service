package faang.school.projectservice.service;

import faang.school.projectservice.exception.ResourceNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class TeamService {
    private final TeamRepository teamRepository;

    public void saveTeam(Team team) {
        teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.warn("Team with ID {} not found", teamId);
                    return new ResourceNotFoundException("Team", "id", teamId);
                });
    }
}
