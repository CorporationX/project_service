package faang.school.projectservice.validator.internship;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import faang.school.projectservice.exception.DataValidationException;

import java.time.LocalDate;

public class InternshipValidator {

    private static final long MONTH_NUMBER = 3L;

    public static void validateInternshipLengthDate(CreateInternshipDto createInternshipDto) {
        LocalDate start = LocalDate.from(createInternshipDto.startDate());
        LocalDate end = createInternshipDto.endDate() != null ?
                LocalDate.from(createInternshipDto.endDate()) :
                start.plusMonths(MONTH_NUMBER);
        if (end.isAfter(start.plusMonths(MONTH_NUMBER))) {
            throw new DataValidationException("Internship can't be longer than three months");
        }
    }
}