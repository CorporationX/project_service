package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.INTERNSHIP_DATES_WRONG_ORDER_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.INTERNSHIP_DURATION_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.INTERNSHIP_IN_THE_PAST_CREATION_EXCEPTION;

@Component
class InternshipControllerValidation {

    public static final int INTERNSHIP_MAX_DURATION_IN_MONTHS = 3;

    public void validateInternshipDuration(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();

        if (startDate.isAfter(endDate)) {
            throw new DataValidationException(INTERNSHIP_DATES_WRONG_ORDER_EXCEPTION.getMessage());
        }

        if (startDate.isBefore(endDate.minus(INTERNSHIP_MAX_DURATION_IN_MONTHS, ChronoUnit.MONTHS))) {
            String exceptionMessage = String.format(INTERNSHIP_DURATION_EXCEPTION.getMessage(),
                    INTERNSHIP_MAX_DURATION_IN_MONTHS);
            throw new DataValidationException(exceptionMessage);
        }
    }

    public void validateInternshipDates(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();

        if (startDate.isBefore(LocalDateTime.now()) || endDate.isBefore(LocalDateTime.now())) {
            throw new DataValidationException(INTERNSHIP_IN_THE_PAST_CREATION_EXCEPTION.getMessage());
        }
    }
}