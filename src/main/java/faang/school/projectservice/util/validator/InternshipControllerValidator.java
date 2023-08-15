package faang.school.projectservice.util.validator;

import faang.school.projectservice.dto.internship.InternshipDto;
import faang.school.projectservice.util.exception.DataValidationException;
import org.springframework.stereotype.Component;

@Component
public class InternshipControllerValidator {

    public void validateToCreate(InternshipDto dto) {
        if (dto.getEndDate().isAfter(dto.getStartDate().plusMonths(3))) {
            throw new DataValidationException("Internship duration must be less than 3 months");
        }
    }
}
