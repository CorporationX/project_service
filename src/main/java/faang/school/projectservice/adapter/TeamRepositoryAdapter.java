package faang.school.projectservice.adapter;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter {
    private final TeamRepository teamRepository;

    public Team save(Team team) {
        return teamRepository.save(team);
    }
}
