package faang.school.projectservice.validate;

import faang.school.projectservice.model.Team;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamValidate {

    private final TeamRepository teamRepository;

    public Team validateTeamById(Long teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> {
                    log.error("Team with id {} not found", teamId);
                    return new EntityNotFoundException("Team not found");
                });
    }
}
