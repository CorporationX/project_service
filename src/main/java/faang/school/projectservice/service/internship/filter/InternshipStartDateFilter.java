package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
class InternshipStartDateFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStartDatePattern() != null;
    }

    @Override
    public Stream<Internship> apply(List<Internship> internships, InternshipFilterDto filters) {
        LocalDateTime startDatePattern = filters.getStartDatePattern();

        return internships.stream()
                .filter(internship -> {
                    LocalDateTime internshipStartDate = internship.getStartDate();

                    return internshipStartDate.isAfter(startDatePattern)
                            || internshipStartDate.equals(startDatePattern);
                });
    }
}