package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;

public class InternshipStatusFilter implements InternshipFilter{
    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getInternshipStatus() != null;
    }

    @Override
    public boolean apply(Internship internship, InternshipFilterDto filters) {
        return internship.getStatus().equals(filters.getInternshipStatus());
    }
}
