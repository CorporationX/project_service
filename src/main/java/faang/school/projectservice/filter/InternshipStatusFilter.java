package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.InternshipStatus;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFiltersDto filters) {
        return filters.getStatusPattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFiltersDto filters) {
        return internships
                .filter(internship -> internship.getStatus().equals(filters.getStatusPattern()));
    }
}
