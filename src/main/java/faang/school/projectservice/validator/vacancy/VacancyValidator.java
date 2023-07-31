package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    public void deleteVacancyControllerVAlidation(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new VacancyValidationException("Vacancy not found!");
        }
    }
}
