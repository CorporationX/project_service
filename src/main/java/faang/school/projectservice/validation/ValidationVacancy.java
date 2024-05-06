package faang.school.projectservice.validation;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.exception.vacancy.ExceptionMessage;
import org.springframework.stereotype.Component;

@Component
public class ValidationVacancy {

    public void checkVacancy(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new DataValidationException(ExceptionMessage.VACANCY_DTO_IS_NULL.getMessage());
        }
    }

    public void checkVacancyName(VacancyDto vacancyDto) {
        if (vacancyDto.getName().isBlank()) {
            throw new DataValidationException(ExceptionMessage.NAME_IS_BLANK.getMessage());
        }
    }

    public void checkProjectId(VacancyDto vacancyDto) {
        if (vacancyDto.getProjectId() == null) {
            throw new DataValidationException(ExceptionMessage.PROJECT_ID_IS_EMPTY.getMessage());
        }
    }
}
