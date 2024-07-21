package faang.school.projectservice.annotation;

import faang.school.projectservice.dto.client.InternshipToCreateDto;
import faang.school.projectservice.dto.client.InternshipToUpdateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class DurationOfInternshipValidation implements ConstraintValidator<DurationOfInternship, Object> {
    private final static int MONTH_AMOUNT = 3;
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof InternshipToCreateDto) {
            LocalDateTime start = ((InternshipToCreateDto) value).getStartDate();
            LocalDateTime end = ((InternshipToCreateDto) value).getEndDate();

            LocalDateTime dateBetweenStartAndEnd = start.plusMonths(MONTH_AMOUNT);
            return dateBetweenStartAndEnd.isAfter(end);
        }

        if (value instanceof InternshipToUpdateDto) {
            LocalDateTime start = ((InternshipToUpdateDto) value).getStartDate();
            LocalDateTime end = ((InternshipToUpdateDto) value).getEndDate();

            if (start != null && end != null) {
                LocalDateTime dateBetweenStartAndEnd = start.plusMonths(MONTH_AMOUNT);
                return dateBetweenStartAndEnd.isAfter(end);
            }
        }
        return true;
    }
}
