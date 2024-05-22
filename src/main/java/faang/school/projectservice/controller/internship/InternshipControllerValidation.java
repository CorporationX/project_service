package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.INTERNSHIP_DURATION_EXCEPTION;

@Component
class InternshipControllerValidation {

    public static final int INTERNSHIP_MAX_DURATION_IN_MONTHS = 3;

    public void validateInternshipDuration(InternshipDto internshipDto) {
        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        if (startDate.isBefore(endDate.minus(INTERNSHIP_MAX_DURATION_IN_MONTHS, ChronoUnit.MONTHS))) {
            throw new DataValidationException(INTERNSHIP_DURATION_EXCEPTION.getMessage() + INTERNSHIP_MAX_DURATION_IN_MONTHS + " months.");
        }
    }
}
