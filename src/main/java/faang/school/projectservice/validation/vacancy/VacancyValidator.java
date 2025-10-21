package faang.school.projectservice.validation.vacancy;

import faang.school.projectservice.dto.vacancy.UpdateVacancyDto;
import faang.school.projectservice.model.Vacancy;

public interface VacancyValidator {
    void validateCreate(long userId, long projectId);

    void validateUpdate(long userId, Vacancy existingVacancy, UpdateVacancyDto updateVacancyDto);

}
