package faang.school.projectservice.util.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.util.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class InternshipControllerValidator {

    public void validateToCreate(InternshipDto dto) {
        if (dto.getStartDate().isBefore(LocalDateTime.now()) || dto.getEndDate().isBefore(LocalDateTime.now())) {
            throw new DataValidationException("Start date and end date must be in the future");
        }
        if (dto.getStartDate().isAfter(dto.getEndDate())) {
            throw new DataValidationException("Start date must be before end date");
        }
        if (dto.getEndDate().isAfter(dto.getStartDate().plusMonths(3))) {
            throw new DataValidationException("Internship duration must be less than 3 months");
        }
        if (dto.getInternIds().contains(dto.getMentorId())) {
            throw new DataValidationException(String.format("Same mentor and intern with id %s", dto.getMentorId()));
        }
    }
}
