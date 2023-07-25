package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class VacancyValidator {
    public void validateInputBody(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.INPUT_BODY_IS_NULL);
        }
    }

    public void validateRequiredFieldsInDTO(VacancyDto vacancyDto){
        checkName(vacancyDto.getName());
        checkDescription(vacancyDto.getDescription());
    }

    private void checkName(String name) {
        if (name == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.NAME_IS_NULL);
        }
        if (name.isBlank()) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.NAME_IS_BLANK);
        }
    }

    private void checkDescription(String description) {
        if (description == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.DESCRIPTION_IS_NULL);
        }
        if (description.isBlank()) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.DESCRIPTION_IS_BLANK);
        }
    }

    private void checkProjectId(Long projectId) {
        if (projectId == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.DESCRIPTION_IS_NULL);
        }
        if (projectId < 0) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.DESCRIPTION_IS_BLANK);
        }
    }
}
