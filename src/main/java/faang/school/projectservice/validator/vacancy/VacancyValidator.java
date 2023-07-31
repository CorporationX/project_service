package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    public void deleteVacancyControllerValidation(VacancyDto vacancyDto, long creatorId) {
        if (vacancyDto == null) {
            throw new VacancyValidationException("Vacancy not found!");
        }

        if (creatorId < 1) {
            throw new VacancyValidationException("Permission error!");
        }
    }

    public void deleteVacancyServiceValidation(VacancyDto vacancyDto, long creatorId) {
        if (vacancyDto.get)
    }
}
