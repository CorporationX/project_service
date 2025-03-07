package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.model.VacancyStatus;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    private final TeamMemberRepository teamMemberRepository;

    public void validateCreatingVacancy(Vacancy vacancy) {
        if (!checkRoleOfCreatedBy(vacancy)) {
            throw new DataValidationException("Only user with owner or manager role can create vacancy");
        }
    }

    public void validateUpdatingVacancy(Vacancy vacancy) {
        if (!checkRoleOfUpdatedBy(vacancy)) {
            throw new DataValidationException("Only user with owner or manager role can update vacancy");
        }

        if (vacancy.getStatus() == VacancyStatus.CLOSED) {
            if (!checkCountOfAcceptedCandidates(vacancy)) {
                throw new DataValidationException("There is not enough number of candidates");
            }

            if (!checkCountOfWaitingCandidates(vacancy)) {
                throw new DataValidationException("There are candidates waiting for response. " +
                        "You should accept or reject them");
            }
        }
    }

    private boolean checkRoleOfCreatedBy(Vacancy vacancy) {
        TeamMember createdBy = teamMemberRepository.findByUserIdAndProjectId(
                vacancy.getCreatedBy(),
                vacancy.getProject().getId());

        for (TeamRole teamRole : createdBy.getRoles()) {
            if (teamRole == TeamRole.OWNER || teamRole == TeamRole.MANAGER) {
                return true;
            }
        }

        return false;
    }

    private boolean checkRoleOfUpdatedBy(Vacancy vacancy) {
        TeamMember teamMemberUpdatedBy = teamMemberRepository.findByUserIdAndProjectId(
                vacancy.getUpdatedBy(),
                vacancy.getProject().getId());

        for (TeamRole teamRole : teamMemberUpdatedBy.getRoles()) {
            if (teamRole == TeamRole.OWNER || teamRole == TeamRole.MANAGER) {
                return true;
            }
        }

        return false;
    }

    private boolean checkCountOfAcceptedCandidates(Vacancy vacancy) {
        return vacancy.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .count() == vacancy.getCount();
    }

    private boolean checkCountOfWaitingCandidates(Vacancy vacancy) {
        return vacancy.getCandidates()
                .stream()
                .noneMatch(candidate -> candidate.getCandidateStatus() == CandidateStatus.WAITING_RESPONSE);
    }
}
