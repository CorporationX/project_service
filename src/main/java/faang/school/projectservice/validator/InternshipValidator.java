package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class InternshipValidator {
    public void validateControllerInternship(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException("There is not internship!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Incorrect name of internship!");
        }
    }
    public void validateServiceSaveInternship(InternshipDto internshipDto) {

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(3, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship cannot last more than 3 months!");
        }
        if (internshipDto.getMentorId() == null || internshipDto.getMentorId() < 1) {
            throw new DataValidationException("There is not mentor for internship!");
        }
        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new DataValidationException("There is not interns for internship!");
        }
    }
}
