package faang.school.projectservice.validator;

import faang.school.projectservice.dto.internship.CreateInternshipDto;

import java.time.DateTimeException;
import java.time.LocalDate;

public class InternshipValidator {

    LocalDate start = LocalDate.from(CreateInternshipDto.startDate());
    LocalDate now = LocalDate.now();
    LocalDate end = createInternshipDto.endDate() != null ?
            LocalDate.from(createInternshipDto.endDate()) :
            start.plusMonths(3);

    public static void validateInternshipLength(CreateInternshipDto createInternshipDto) {
        LocalDate start = LocalDate.from(createInternshipDto.startDate());
        LocalDate now = LocalDate.now();
        LocalDate end = createInternshipDto.endDate() != null ?
                LocalDate.from(createInternshipDto.endDate()) :
                start.plusMonths(3);
        if (end.isAfter(start.plusMonths(3))) {
            throw new DateTimeException("Internship can't long is more three month");
        }
    }

    public static void validateInternshipDates(CreateInternshipDto createInternshipDto)
}