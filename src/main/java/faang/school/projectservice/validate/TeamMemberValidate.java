package faang.school.projectservice.validate;

import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TeamMemberValidate {

    private final TeamMemberRepository teamMemberRepository;

    public TeamMember validateTeamMemberByUserIdAndByTeamId(Long userId, Long teamId) {
        return teamMemberRepository.findByUserIdAndTeamId(userId, teamId)
                .orElseThrow(() -> {
                    log.error("User with id {} can not upload avatar for team with id {}", userId, teamId);
                    return new EntityNotFoundException("User can not upload/delete avatar for team");
                });
    }
}
