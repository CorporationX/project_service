package faang.school.projectservice.filter.internshipFilterImpl;

import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

@Component
public class InternshipNameFilter implements InternshipFilter {
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
