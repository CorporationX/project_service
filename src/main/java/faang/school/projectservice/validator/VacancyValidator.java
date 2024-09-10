package faang.school.projectservice.validator;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class VacancyValidator {
    public void validateVacancyByName(VacancyDto vacancyDto) {
        if (vacancyDto.getName() == null || vacancyDto.getName().isBlank()) {
            throw new DataValidationException("Vacancy name cannot be empty");
        }
    }

    public void validateVacancyByDescription(VacancyDto vacancyDto) {
        if (vacancyDto.getDescription() == null || vacancyDto.getDescription().isBlank()) {
            throw new DataValidationException("Vacancy description cannot be empty");
        }
    }

    public void validateVacancyByCount(VacancyDto vacancyDto) {
        if (vacancyDto.getCount() == null || vacancyDto.getCount() < 1) {
            throw new DataValidationException("Vacancy cannot have less than 1 positions");
        }
    }
}
