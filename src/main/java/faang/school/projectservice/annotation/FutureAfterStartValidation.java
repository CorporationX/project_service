package faang.school.projectservice.annotation;

import faang.school.projectservice.dto.client.InternshipToCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class FutureAfterStartValidation implements ConstraintValidator<FutureAfterStart, InternshipToCreateDto> {


    @Override
    public boolean isValid(InternshipToCreateDto value, ConstraintValidatorContext context) {
        LocalDateTime startDate = value.getStartDate();
        LocalDateTime endDate = value.getEndDate();
        return endDate.isAfter(startDate);
    }
}
