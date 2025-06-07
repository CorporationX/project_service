package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.InvalidOperationException;
import faang.school.projectservice.exception.PermissionDeniedException;
import faang.school.projectservice.model.Candidate;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.CandidateRepository;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class VacancyValidator {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final CandidateRepository candidateRepository;

    public void validateProjectExists(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new EntityNotFoundException(String.format("Project with ID %d not found", projectId));
        }
    }

    public void validateTeamRole(TeamRole position) {
        if (position == null) {
            throw new IllegalArgumentException("Не указана позиция, на которую открыта вакансия");
        }
    }

    public void validateUserHasCreateRights(Long userId, Long projectId) {
        teamMemberRepository.findByUserIdAndProjectId(userId, projectId)
                .ifPresentOrElse(
                        member -> {
                            if (!member.getRoles().contains(TeamRole.OWNER) &&
                                    !member.getRoles().contains(TeamRole.MANAGER)) {
                                throw new PermissionDeniedException("User doesn't have required role");
                            }
                        },
                        () -> {
                            throw new PermissionDeniedException("User is not a team member");
                        }
                );
    }

    public boolean checkRoleUpdatingUser(Long id) {
        TeamMember updatingUser = teamMemberRepository.findByUserId(id).stream()
                .findFirst()
                .orElseThrow(() -> new DataValidationException("Вы не можете обновлять вакансии"));
        boolean hasRequiredRole = updatingUser.getRoles().stream()
                .anyMatch(role -> role == TeamRole.OWNER || role == TeamRole.MANAGER);

        if (!hasRequiredRole) {
            throw new DataValidationException("Обновлять вакансии могут только OWNER или MANAGER");
        }
        return hasRequiredRole;
    }

    public void validateCandidateSelectionForClosing(Vacancy vacancy, List<Long> selectedIds) {
        if (vacancy.getStatus() != VacancyStatus.OPEN) {
            throw new InvalidOperationException("Вакансия должна быть OPEN для закрытия");
        }

        if (selectedIds.size() != vacancy.getCount()) {
            throw new DataValidationException(
                    String.format("Требуется %d кандидатов, но выбрано %d", vacancy.getCount(), selectedIds.size()));
        }

        Set<Long> validCandidateIds = vacancy.getCandidates().stream()
                .map(Candidate::getId)
                .collect(Collectors.toSet());

        List<Long> invalidIds = selectedIds.stream()
                .filter(id -> !validCandidateIds.contains(id))
                .toList();

        if (!invalidIds.isEmpty()) {
            throw new DataValidationException(String.format("Недействительные candidateIds: %s", invalidIds));
        }

        selectedIds.forEach(candidateId -> {
            Candidate candidate = candidateRepository.findById(candidateId)
                    .orElseThrow(() -> new EntityNotFoundException("Кандидат не найден"));

            if (candidate.getCandidateStatus() != CandidateStatus.ACCEPTED) {
                throw new DataValidationException(
                        String.format("Кандидат %d имеет недопустимый статус: %s",
                                candidateId, candidate.getCandidateStatus()));
            }
        });
    }
}