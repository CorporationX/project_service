package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.service.teamMember.TeamMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor
@Component
public class VacancyValidator {

    private final TeamMemberService teamMemberService;

    public void validateTutorRole(Long id, Long projectId) {
        if (id == null || projectId == null) {
            throw new DataValidationException("id and projectId must not be null");
        }
        TeamMember teamMember = teamMemberService.getTeamMemberByIdAndProjectId(id, projectId);

        if (teamMember == null) {
            throw new DataValidationException("user %d not found".formatted(id));
        }

        if (!(teamMember.getRoles().contains(TeamRole.OWNER) || teamMember.getRoles().contains(TeamRole.MANAGER))) {
            throw new DataValidationException("%d user does not have permission to add a vacancy"
                    .formatted(id));
        }
    }

    public void validateCandidatesCount(Vacancy vacancy) {
        if (vacancy == null || vacancy.getCandidates() == null || vacancy.getCount() == null) {
            throw new DataValidationException("vacancy must not be null");
        }

        long countCandidate = vacancy.getCandidates().stream()
                .filter(candidate ->
                        getTeamRole(candidate.getUserId(), vacancy.getProject().getId()).contains(vacancy.getPosition())
                )
                .count();

        if (countCandidate < vacancy.getCount()) {
            throw new DataValidationException("There are not enough candidates. Candidate count: %d"
                    .formatted(vacancy.getCandidates().size()));
        }

        if (countCandidate > vacancy.getCount()) {
            throw new DataValidationException(("There are too many candidates. Make a final choice. " +
                    "Candidate count: %d").formatted(vacancy.getCandidates().size()));
        }
    }

    public void validateVacancyStatus(Vacancy vacancy) {
        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            throw new DataValidationException("vacancy id %d has already been closed".formatted(vacancy.getId()));
        }
    }

    public void validateCandidates(Vacancy vacancy, List<Candidate> candidates) {
        List<Long> candidatesUserIds = candidates.stream()
                .map(Candidate::getUserId)
                .toList();

        List<Long> candidatesInProject = vacancy.getProject().getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream()
                        .map(TeamMember::getUserId)
                )
                .filter(candidatesUserIds::contains)
                .toList();

        if (!candidatesInProject.isEmpty()) {
            throw new DataValidationException("Candidate already exists in this project: " + candidatesInProject);
        }
    }

    private List<TeamRole> getTeamRole(Long userId, Long projectID) {
        TeamMember teamMember = teamMemberService.getTeamMemberByIdAndProjectId(userId, projectID);
        return teamMember.getRoles();
    }
}
