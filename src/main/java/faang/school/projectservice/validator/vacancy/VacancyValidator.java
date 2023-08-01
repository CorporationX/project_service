package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {

    // Controller Validation:
    public void updateVacancyControllerValidation(VacancyDto vacancyDto, long updaterId) {
        if (vacancyDto == null) {
            throw new VacancyValidationException("Vacancy not found!");
        }

        if (updaterId < 1) {
            throw new VacancyValidationException("Permission error!");
        }
    }

    // Service Validation:
    public void updateVacancyServiceValidation(VacancyDto vacancyDto, long updaterId) {
        if (!vacancyDto.getCreatedBy().equals(updaterId)) {
            throw new VacancyValidationException("Permission error!");
        }
    }
}
