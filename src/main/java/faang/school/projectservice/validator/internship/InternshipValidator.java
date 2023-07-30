package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;

import java.time.temporal.ChronoUnit;

public class InternshipValidator {
    public static void internshipControllerValidation(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException("Internship is null!");
        }
        if (internshipDto.getName() == null || internshipDto.getName().isBlank()) {
            throw new DataValidationException("Internship name can not be blank or null!");
        }
        if (internshipDto.getProjectId() == null || internshipDto.getProjectId() < 1) {
            throw new DataValidationException("Internship relation project error!");
        }
    }

    public static void createInternshipServiceValidation(InternshipDto internshipDto) {
        if (internshipDto.getInterns() == null) {
            throw new DataValidationException("No interns!");
        }
        if (internshipDto.getEndDate().isAfter(internshipDto.getStartDate().plus(3, ChronoUnit.MONTHS))) {
            throw new DataValidationException("Internship's duration is too long!");
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationException("Internship has no mentor!");
        }
    }

    public static void updateInternshipServiceValidation(Internship old, InternshipDto internshipDto) {
        createInternshipServiceValidation(internshipDto);

        if (old.getStatus() == null || old.getStatus().equals(InternshipStatus.COMPLETED)) {
            throw new DataValidationException("Internship already over!");
        }
        if (old.getInterns().size() < internshipDto.getInterns().size()) {
            throw new DataValidationException("Can't add new intern!");
        }
    }
}
