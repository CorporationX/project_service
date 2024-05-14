package faang.school.projectservice.controller.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.exception.InternshipValidationExceptionMessage;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNSHIP_MENTOR_ID_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNSHIP_NAME_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNSHIP_PROJECT_ID_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNSHIP_STATUS_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERNS_LIST_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.EMPTY_INTERN_ID_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.INTERNSHIP_DURATION_EXCEPTION;
import static faang.school.projectservice.exception.InternshipValidationExceptionMessage.NULL_DTO_EXCEPTION;

@Component
class InternshipControllerValidation {

    public static final int INTERNSHIP_MAX_DURATION_IN_MONTHS = 3;

    public void validationDto(InternshipDto internshipDto) {
        checkForNull(internshipDto, NULL_DTO_EXCEPTION);

        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException(EMPTY_INTERNSHIP_NAME_EXCEPTION.getMessage());
        }

        checkForNull(internshipDto.getProjectId(), EMPTY_INTERNSHIP_PROJECT_ID_EXCEPTION);
        checkForNull(internshipDto.getMentorId(), EMPTY_INTERNSHIP_MENTOR_ID_EXCEPTION);

        if (CollectionUtils.isEmpty(internshipDto.getInternsIds())) {
            throw new DataValidationException(EMPTY_INTERNS_LIST_EXCEPTION.getMessage());
        }

        internshipDto.getInternsIds().forEach(id -> checkForNull(id, EMPTY_INTERN_ID_EXCEPTION));

        LocalDateTime startDate = internshipDto.getStartDate();
        LocalDateTime endDate = internshipDto.getEndDate();
        if (startDate.isBefore(endDate.minus(INTERNSHIP_MAX_DURATION_IN_MONTHS, ChronoUnit.MONTHS))) {
            throw new DataValidationException(INTERNSHIP_DURATION_EXCEPTION.getMessage());
        }
        if (internshipDto.getStatus() == null || internshipDto.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException(EMPTY_INTERNSHIP_STATUS_EXCEPTION.getMessage());
        }
    }

    public void checkForNull(Object id, InternshipValidationExceptionMessage message) {
        if (id == null) {
            throw new DataValidationException(message.getMessage());
        }
    }
}
