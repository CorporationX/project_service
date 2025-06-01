package faang.school.projectservice.service.adapter;

import faang.school.projectservice.exception.TeamNotFoundException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import faang.school.projectservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamServiceAdapter implements TeamService {
    private final TeamRepository teamRepository;

    @Override
    public Team getById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }
}
