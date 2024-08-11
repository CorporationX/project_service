package faang.school.projectservice.filter.internshipFilterImpl;

import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class InternshipStatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public boolean apply(Internship internship, InternshipFilterDto filters) {
        return Objects.equals(internship.getStatus(), filters.getStatus());
    }
}
