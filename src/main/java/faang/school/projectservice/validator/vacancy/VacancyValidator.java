package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author Alexander Bulgakov
 */

@Component
@RequiredArgsConstructor
public class VacancyValidator {
    public void checkControllerDataValidator(VacancyDto dto) {
        if (null == dto.getProjectId()) {
            throw new DataValidationException("The vacancy does not belong to any project!");
        }
    }
}
