package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exceptions.DataValidationException;
import faang.school.projectservice.exceptions.NotFoundException;
import faang.school.projectservice.model.CandidateStatus;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.Vacancy;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {

    private final ProjectRepository projectRepository;
    private final VacancyRepository vacancyRepository;

    public void validateVacancyName(VacancyDto vacancy) {
        if (vacancy.getName() == null) {
            throw new DataValidationException("name of vacancy doesn't exist");
        } else if (vacancy.getName().isBlank()) {
            throw new DataValidationException("name of vacancy is blank");
        }
    }

    public void validateCountOfVacancy(VacancyDto vacancy) {
        if (vacancy.getCount() <= 0) {
            throw new DataValidationException("count of vacancy is wrong");
        }
    }

    public void checkExistsById(long id) {
        if (!projectRepository.existsById(id)) {
            throw new DataValidationException("Project with Id doesn't exist");
        }
    }

    public void validateCountOfCandidate(Vacancy vacancyForUpdate) {
        if (vacancyForUpdate.getCandidates().size() < vacancyForUpdate.getCount()) {
            throw new DataValidationException("Vacancy can't be closed");
        }
    }

    public void checkExistsVacancy(VacancyDto vacancy) {
        if (vacancy.getId() == null) {
            throw new NotFoundException("Vacancy doesn't have id");
        } else if (vacancyRepository.existsById(vacancy.getId())) {
            throw new DataValidationException("Vacancy with id" + vacancy.getId() + "doesn't exist");
        }
    }

    public void validateTeamMember(TeamMember teamMember) {
        if (!teamMember.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("Team member with Id " + teamMember.getId() + " is not " + TeamRole.OWNER);
        }
    }

    public void checkAcceptedCandidatesCount(Vacancy vacancy) {
        long countOfAcceptedCandidates = vacancy.getCandidates()
                .stream()
                .filter(candidate -> candidate.getCandidateStatus() == CandidateStatus.ACCEPTED)
                .count();

        if (countOfAcceptedCandidates < vacancy.getCount()) {
            throw new DataValidationException("Don't have enough candidates");
        }
    }
}
