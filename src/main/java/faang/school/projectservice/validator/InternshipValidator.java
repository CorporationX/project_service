package faang.school.projectservice.validator;

import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;

import java.time.temporal.ChronoUnit;

public class InternshipValidator {
    public static void validateControllerInternship(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException("There is not internship!");
        }
        if (internshipDto.getProjectId() == null || internshipDto.getProjectId() < 1) {
            throw new DataValidationException("There is not project for create internship!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Incorrect name of internship!");
        }
    }
    public static void validateServiceSaveInternship(InternshipDto internshipDto) {

        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(3, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship cannot last more than 3 months!");
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationException("There is not mentor for internship!");
        }
        if (internshipDto.getInternsId() == null) {
            throw new DataValidationException("There is not interns for internship!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Need create a name for the internship");
        }
    }

    public static void validateServiceUpdateInternship(Internship oldInternship, InternshipDto internshipDto) {
        if (oldInternship.getStatus() == null || oldInternship.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException("Internship is over!");
        }
        if (oldInternship.getInterns().size() < internshipDto.getInternsId().size()) {
            throw new DataValidationException("Can't add new interns!");
        }
    }
}