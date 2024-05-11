package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
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
        if (vacancyRepository.existsById(vacancy.getId()))
            throw new DataValidationException("Vacancy with id" + vacancy.getId() + "doesn't exist");
    }
}
