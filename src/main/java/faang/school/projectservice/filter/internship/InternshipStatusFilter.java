package faang.school.projectservice.filter.internship;

import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

@Component
public class InternshipStatusFilter {
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    public boolean apply(Internship internship, InternshipFilterDto filters) {
        return internship.getStatus().equals(filters.getStatus());
    }
}
