package faang.school.projectservice.controller.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.DataValidationException;
import faang.school.projectservice.exception.vacancy.ExceptionMessage;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
class ValidationVacancy {

    public void checkVacancyName(@NonNull VacancyDto vacancyDto) {
        if (vacancyDto.getName().isBlank()) {
            throw new DataValidationException(ExceptionMessage.VACANCY_DTO_NAME_IS_BLANK.getMessage());
        }
    }

    public void checkProjectId(VacancyDto vacancyDto) {
        if (vacancyDto.getProjectId() == null) {
            throw new DataValidationException(ExceptionMessage.PROJECT_ID_IS_EMPTY.getMessage());
        }
    }
}
