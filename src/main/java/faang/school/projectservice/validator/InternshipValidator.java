package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
public class InternshipValidator {
    private static final int MAX_INTERNSHIP_DURATION_MONTHS = 3;

    public void validateControllerInternship(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException("There is not internship!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Internship name can not be blank or null!");
        }
    }

    public void validateServiceSaveInternship(InternshipDto internshipDto) {
        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(MAX_INTERNSHIP_DURATION_MONTHS, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship cannot last more than " + MAX_INTERNSHIP_DURATION_MONTHS + " months!");
        }
        if (internshipDto.getMentorId() == null || internshipDto.getMentorId() < 1) {
            throw new DataValidationException("There is not mentor for internship!");
        }
        if (internshipDto.getInternsId() == null || internshipDto.getInternsId().isEmpty()) {
            throw new DataValidationException("There is not interns for internship!");
        }
    }

    public void validateServiceUpdateInternship(Internship oldInternship, InternshipDto internshipDto) {
        validateServiceSaveInternship(internshipDto);
        if (oldInternship.getStatus() == null || oldInternship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException("Internship is over!");
        }
        if (oldInternship.getStatus().equals(InternshipStatus.IN_PROGRESS)) {
            throw new DataValidationException("Can't add new interns!");
        }
    }
}