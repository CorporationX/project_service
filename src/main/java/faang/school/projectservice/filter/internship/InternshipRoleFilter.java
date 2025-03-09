package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

@Component
public class InternshipRoleFilter implements InternshipFilter{
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getRole() != null;
    }

    @Override
    public boolean filterEntity(Internship internship, InternshipFilterDto filters) {
        return internship.getRole().equals(filters.getRole());
    }
}
