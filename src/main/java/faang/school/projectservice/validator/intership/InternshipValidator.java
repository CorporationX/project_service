package faang.school.projectservice.validator.intership;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.temporal.ChronoUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class InternshipValidator {

    private static final long INTERNSHIP_MAX_DURATION = 3L;

    private void validateInternshipHaveProjectAndInterns(InternshipDto internshipDto) {
        if (internshipDto.getProjectId() == null) {
            log.error("Missing project field!");
            throw new DataValidationException("Internship project field can't be null!");
        }
        if (internshipDto.getInterns() == null || internshipDto.getInterns().isEmpty()) {
            log.error("Missing interns field!");
            throw new DataValidationException("Internship can't be created without interns!");
        }
    }

    private void validateInternshipDuration(InternshipDto internshipDto) {
        long internshipDuration = ChronoUnit.MONTHS.between(internshipDto.getStartDate(), internshipDto.getEndDate());
        if (internshipDuration >= INTERNSHIP_MAX_DURATION) {
            log.error("Internship duration is too long!");
            throw new DataValidationException("The duration of internship must be not more than "
                    + INTERNSHIP_MAX_DURATION + " months!");
        }
    }

    public void validateInternship(InternshipDto internshipDto) {
        validateInternshipDuration(internshipDto);
        validateInternshipHaveProjectAndInterns(internshipDto);
    }

    public void validateInternshipProjectAndMentorExist(Project project, TeamMember mentor) {
        if (project == null) {
            log.error("Couldn't find project in database!");
            throw new EntityNotFoundException("Project doesn't exists");
        }
        if (mentor == null) {
            log.error("Couldn't find mentor in database!");
            throw new EntityNotFoundException("Mentor doesn't exist!");
        }
    }
}
