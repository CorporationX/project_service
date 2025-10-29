package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.CandidateCreateDto;
import faang.school.projectservice.exeption.ForbiddenException;
import faang.school.projectservice.exeption.IllegalStateException;
import faang.school.projectservice.exeption.IllegalArgumentException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class VacancyValidator {
    private static final Set<TeamRole> ALLOWED_ROLES = Set.of(
            TeamRole.OWNER,
            TeamRole.MANAGER
    );

    public static void validateRole(@NotNull TeamMember author) {
        if (author == null) {
            throw new ForbiddenException("User is not a member of this project");
        }

        if (Collections.disjoint(author.getRoles(), ALLOWED_ROLES)) {
            throw new ForbiddenException("Only OWNER or MANAGER can modify vacancies");
        }
    }

    public static void validateAddCandidatesToClosedVacancy(Vacancy vacancy) {
        if (VacancyStatus.CLOSED.equals(vacancy.getStatus())) {
            throw new IllegalStateException("Cannot add candidates to a closed vacancy");
        }
    }

    public static void validateCandidateIsAlreadyProjectMember(Project project,
                                                               Candidate candidate) {
        boolean isMember = project.getTeams().stream()
                .filter(Objects::nonNull)
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(Objects::nonNull)
                .anyMatch(member -> Objects.equals(member.getUserId(),candidate.getUserId()));

        if (isMember) {
            throw new IllegalArgumentException("Candidate is already a project member");
        }
    }

    public static void validateCandidateAlreadyAddedThisVacancy(Vacancy vacancy,
                                                                CandidateCreateDto candidateCreateDto) {
        boolean isCandidate = vacancy.getCandidates().stream()
                .filter(Objects::nonNull)
                .anyMatch(candidate -> Objects.equals(candidate.getUserId(), candidateCreateDto.userId()));

        if (isCandidate) {
            throw new IllegalArgumentException("Candidate already added to this vacancy");
        }
    }

    public static void validateCandidateStatusNotNull(CandidateStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
    }

    public static void validateCandidateNotInCurrentStatus(Vacancy vacancy, Candidate candidate, CandidateStatus status) {
        boolean hasSameStatus = vacancy.getCandidates().stream()
                .anyMatch(c -> Objects.equals(status, c.getCandidateStatus()) && Objects.equals(c.getId(), candidate.getId()));

        if (hasSameStatus) {
            throw new IllegalStateException("Candidate is already " + status.name().toLowerCase());
        }
    }

    public static void validateCanCloseVacancy(Vacancy vacancy) {
        if (vacancy.getAcceptedCandidates().size() < vacancy.getCount()) {
            throw new IllegalStateException("Not enough candidates to close vacancy");
        }
    }

    public static void validateVacancyIsClose(Vacancy vacancy) {
        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            throw new IllegalStateException("Vacancy has already been closed");
        }
    }

    public static void validateVacancyCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Vacancy count must be positive");
        }
        if (count > 100) { // или другое разумное ограничение
            throw new IllegalArgumentException("Vacancy count is too large");
        }
    }

    public static void validateVacancyHasProject(Vacancy vacancy) {
        if (vacancy.getProject() == null) {
            throw new IllegalStateException("Vacancy project is null");
        }
    }
}
