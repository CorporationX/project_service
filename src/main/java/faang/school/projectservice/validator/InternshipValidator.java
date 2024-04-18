package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.ConstraintViolation;
import faang.school.projectservice.exception.MessageError;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

import static faang.school.projectservice.model.InternshipStatus.IN_PROGRESS;

@Component
@AllArgsConstructor
public class InternshipValidator {

    public void validateCreation(InternshipDto internshipDto) {
        validateDateInterval(internshipDto);
        validateInternsList(internshipDto);
    }

    public void validateUpdating(InternshipDto internshipDto) {
        validateStatus(internshipDto);
    }

    public void validateInternsList(InternshipDto internshipDto) {
        if (internshipDto.getInternsIds().isEmpty()) {
            throw new ConstraintViolation(MessageError.UNACCEPTABLE_EMPTY_INTERNS_LIST);
        }
    }

    public void validateDateInterval(InternshipDto internshipDto) {
        if (ChronoUnit.MONTHS.between(internshipDto.getStartDate(), internshipDto.getEndDate()) <= 3) {
            throw new ConstraintViolation(MessageError.UNACCEPTABLE_DATE_INTERVAL.getMessage());
        }
    }

    public void validateStatus(InternshipDto internshipDto) {
        if (internshipDto.getStatus().equals(IN_PROGRESS)) {
            throw new ConstraintViolation(MessageError.UNACCEPTABLE_INTERNSHIP_STATUS);
        }
    }
}
