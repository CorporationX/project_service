package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getIntern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internship, InternshipFilterDto filters) {
        return internship.filter(internship1 -> internship1.getInterns().stream()
                .anyMatch(teamMember -> teamMember.getRoles().contains(filters.getIntern())));
    }
}
