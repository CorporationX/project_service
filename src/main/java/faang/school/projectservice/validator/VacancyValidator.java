package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    public void validateVacancy(VacancyDto vacancy) {
        if (vacancy.getName() == null) {
            throw new DataValidationException("name of vacancy doesn't exist");
        } else if (vacancy.getName().isBlank()) {
            throw new DataValidationException("name of vacancy is blank");
        }
        if (vacancy.getCount() <= 0) {
            throw new DataValidationException("count of vacancy is wrong");
        }
    }
}
