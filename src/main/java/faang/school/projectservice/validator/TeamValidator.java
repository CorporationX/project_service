package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DeniedInAccessException;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class TeamValidator {

    private final TeamRepository teamRepository;

    public Team verifyTeamExistence(long teamId) {
        Optional<Team> teamOptional = teamRepository.findById(teamId);
        if (teamOptional.isEmpty()) {
            String errMessage = "Could not find team by ID: " + teamId;
            log.error(errMessage);
            throw new EntityNotFoundException(errMessage);
        }
        return teamOptional.get();
    }

    public void verifyUserExistenceInTeam(long userId, Team team) {
        boolean isUserTeamMember = team.getTeamMembers().stream()
                .map(TeamMember::getUserId)
                .anyMatch(teamMemberId -> teamMemberId == userId);
        if (!isUserTeamMember) {
            throw new DeniedInAccessException(userId);
        }
    }
}
