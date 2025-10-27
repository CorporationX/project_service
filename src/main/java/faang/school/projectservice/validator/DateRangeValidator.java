package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Duration;

public class DateRangeValidator implements ConstraintValidator<ValidDateRange, CreateInternshipDto> {

    @Override
    public boolean isValid(CreateInternshipDto dto, ConstraintValidatorContext context) {
        if (dto.startDate() == null || dto.endDate() == null) {
            return false;
        }
        if (!dto.startDate().isBefore(dto.endDate())) {
            return false;
        }
        Duration duration = Duration.between(dto.startDate(), dto.endDate());
        return duration.toDays() <= 90;
    }
}