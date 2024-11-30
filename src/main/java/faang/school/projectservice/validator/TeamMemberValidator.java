package faang.school.projectservice.validator;

import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TeamMemberValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateTeamMemberExistsById(Long userId) {
        if(!teamMemberRepository.existsById(userId)) {
            throw new EntityNotFoundException(String.format("Team member not found, id: %d", userId));
        }
    }
}
