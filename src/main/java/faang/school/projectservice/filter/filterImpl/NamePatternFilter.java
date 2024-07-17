package faang.school.projectservice.filter.filterImpl;

import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

@Component
public class NamePatternFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getName() != null;
    }

    @Override
    public boolean apply(Internship internship, InternshipFilterDto filters) {
        return internship.getName().toLowerCase()
                .contains(filters.getName().toLowerCase());
    }
}
