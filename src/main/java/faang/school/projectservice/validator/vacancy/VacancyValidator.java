package faang.school.projectservice.validator.vacancy;

import faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy;
import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;

public class VacancyValidator {
    public static void validateInputBody(VacancyDto vacancyDto) {
        if (vacancyDto == null) {
            throw new VacancyValidateException(ErrorMessagesForVacancy.INPUT_BODY_IS_NULL);
        }
    }

    public static void validateRequiredFields(VacancyDto vacancyDto){

    }
}
