package faang.school.projectservice.filter.filterImpl;

import faang.school.projectservice.filter.internship.InternshipFilter;
import faang.school.projectservice.filter.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class StatusFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getStatus() != null;
    }

    @Override
    public boolean apply(Internship internship, InternshipFilterDto filters) {
        return Objects.equals(internship.getStatus(), filters.getStatus());
    }
}
