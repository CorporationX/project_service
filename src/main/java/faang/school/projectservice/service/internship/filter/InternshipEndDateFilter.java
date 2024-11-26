package faang.school.projectservice.service.internship.filter;

import faang.school.projectservice.dto.internship.filter.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Component
class InternshipEndDateFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getEndDatePattern() != null;
    }

    @Override
    public Stream<Internship> apply(List<Internship> internships, InternshipFilterDto filters) {
        LocalDateTime endDatePattern = filters.getEndDatePattern();

        return internships.stream()
                .filter(internship -> {
                    LocalDateTime internshipEndDate = internship.getEndDate();

                    return internshipEndDate.isBefore(endDatePattern)
                            || internshipEndDate.equals(endDatePattern);
                });
    }
}