package faang.school.projectservice.repository.adapter;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamRepositoryAdapter {
    private final TeamRepository teamRepository;

    public Team getById(Long id) {
        return teamRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Team not found with id: " + id));
    }
}
