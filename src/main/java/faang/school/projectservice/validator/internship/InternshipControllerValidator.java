package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.client.internship.InternshipDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipControllerValidator {
    private static final String CHECK_MESSAGE = "%s can't be empty.";

    public void checkDataBeforeCreate(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Internship"));
        }
        if (internshipDto.getMentorId() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Mentor"));
        }
        if (internshipDto.getInternsId() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Interns ids"));
        }
        if (internshipDto.getProjectId() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Project id"));
        }
        if (internshipDto.getDescription() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Description"));
        }
    }

    public void checkDataBeforeUpdate(Long id) {
        checkIsIdNull(id);
    }

    public void checkDataBeforeGetInternship(Long id) {
        checkIsIdNull(id);
    }

    private void checkIsIdNull(Long id) {
        if (id == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Id of internship"));
        }
    }
}
