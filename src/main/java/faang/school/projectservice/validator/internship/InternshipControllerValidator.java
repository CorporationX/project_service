package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.dto.internship.InternshipUpdateDto;
import faang.school.projectservice.exception.DataValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InternshipControllerValidator {
    private static final String CHECK_MESSAGE = "%s can't be empty.";
    private static final int NUMBER_MONTH = 3;

    public void checkDataBeforeCreate(InternshipDto internshipDto) {
        if (internshipDto == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Internship"));
        }
        if (internshipDto.getMentorId() == null || internshipDto.getMentorId() == 0) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Mentor"));
        }
        if (internshipDto.getInterns() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Interns users ids"));
        }
        if (internshipDto.getProjectId() == null || internshipDto.getProjectId() == 0) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Project id"));
        }
        if (internshipDto.getDescription() == null) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Description"));
        }
        if (internshipDto.getStartDate().plusMonths(NUMBER_MONTH).isAfter(internshipDto.getEndDate())) {
            throw new DataValidationException(String.format("Internship can't last longer more than %d months",
                    NUMBER_MONTH));
        }
    }

    public void checkDataBeforeUpdate(InternshipUpdateDto internshipUpdateDto) {
        checkIsIdNull(internshipUpdateDto.getId());
    }

    public void checkDataBeforeGetInternship(Long id) {
        checkIsIdNull(id);
    }

    private void checkIsIdNull(Long id) {
        if (id == null || id == 0) {
            throw new DataValidationException(String.format(CHECK_MESSAGE, "Id of internship"));
        }
    }
}
