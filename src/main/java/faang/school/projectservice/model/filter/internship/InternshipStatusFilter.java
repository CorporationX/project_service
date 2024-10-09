package faang.school.projectservice.model.filter.internship;

import faang.school.projectservice.model.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto internship) {
        return internship.internshipStatus() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getStatus()
                .equals(filters.internshipStatus()));
    }
}
