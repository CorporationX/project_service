package faang.school.projectservice.repository.adapter.team;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter {
    private final TeamRepository teamRepository;

    public List<Team> getTeamsByProjectId(Long projectId) {
        return teamRepository.findByProject_Id(projectId);
    }
}
