package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.CreateInternshipDto;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;

@Component
public class InternshipValidator {

    public static void validateInternshipLength(CreateInternshipDto createInternshipDto) {
        LocalDate start = LocalDate.from(createInternshipDto.startDate());
        LocalDate end = createInternshipDto.endDate() != null ?
                LocalDate.from(createInternshipDto.endDate()) :
                start.plusMonths(3);
        if (end.isAfter(start.plusMonths(3))) {
            throw new DateTimeException("Internship can't be longer than three months");
        }
    }
}