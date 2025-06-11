package faang.school.projectservice.repository.adapter.team;

import faang.school.projectservice.exception.TeamNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter {
    private final TeamRepository teamRepository;

    public List<Team> getTeamsByProjectId(Long projectId) {
        if (projectId == null) {
            throw new IllegalArgumentException("projectId must not be null");
        }
        return teamRepository.findByProjectId(projectId);
    }

    public List<Team> getTeamsByProjectIds(Collection<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            throw new IllegalArgumentException("projectIds must not be null");
        }
        return teamRepository.findAllByProjectIdIn(projectIds);
    }

    public Team getById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }
}
