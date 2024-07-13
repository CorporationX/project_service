package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.exception.Exceptions;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class Validator {
    private final Exceptions exception;

    public boolean checkLongFieldIsNullAndZero(Long internship) {
        checkFieldIsNull(internship == null);
        return !(internship <= 0);
    }

    public boolean checkStringIsNullAndEmpty(String internship) {
        checkFieldIsNull(internship == null);
        return internship.trim().isEmpty();
    }

    public boolean checkLocalDateTimeIsNull(LocalDateTime internshipDate) {
        checkFieldIsNull(internshipDate == null);
        return false;
    }

    public boolean checkFieldIsNull(boolean internship) {
        if (internship) exception.validateInputValuesIsNull();
        return false;
    }

    public void checkInternshipDto(InternshipDto internshipDto) {
        if (checkStringIsNullAndEmpty(internshipDto.getName())
                || checkFieldIsNull(internshipDto.getProject() == null)
                || checkFieldIsNull(internshipDto.getMentorId() == null)
                || checkLocalDateTimeIsNull(internshipDto.getStartDate())
                || internshipDto.getStartDate().isBefore(LocalDateTime.now().minusDays(90))) {

            exception.validateInputValuesNotValidate();

        }
    }
}
