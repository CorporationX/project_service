package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoForUpdate;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.model.VacancyStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.NEGATIVE_CREATED_BY_ID_FORMAT;
import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.NEGATIVE_PROJECT_ID_FORMAT;

@Component
public class VacancyValidator {
    public void validateInputBody(Object vacancyDto) {
        if (vacancyDto == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.INPUT_BODY_IS_NULL);
        }
    }

    public void validateRequiredFieldsInDTO(VacancyDto vacancyDto){
        checkName(vacancyDto.getName());
        checkDescription(vacancyDto.getDescription());
        checkProjectId(vacancyDto.getProjectId());
        checkCreatedId(vacancyDto.getCreatedBy());
    }

    public void validateRequiredFeildsForUpdateVacancy(VacancyDtoForUpdate vacancyDto){
        checkId(vacancyDto.getVacancyId());
        checkStatus(vacancyDto.getStatus());
    }

    private void checkId(Long id) {
        if (id == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.VACANCY_ID_IS_NULL);
        }
        if (id < 0) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.NEGATIVE_VACANCY_ID);
        }
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
            throw new VacancyValidateException(ErrorMessagesForVacancy.PROJECT_ID_IS_NULL);
        }
        if (projectId < 0) {
            String errorMessage = MessageFormat.format(NEGATIVE_PROJECT_ID_FORMAT, projectId);
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkCreatedId(Long createdId) {
        if (createdId == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.CREATED_BY_ID_IS_NULL);
        }
        if (createdId < 0) {
            String errorMessage = MessageFormat.format(NEGATIVE_CREATED_BY_ID_FORMAT, createdId);
            throw new VacancyValidateException(errorMessage);
        }
    }

    private void checkStatus(VacancyStatus status) {
        if (status == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.STATUS_IS_NULL);
        }
    }
}
