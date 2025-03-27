package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;


@Component
public class InternshipRoleFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.getRole() != null;
    }

    @Override
    public boolean apply(Internship internship, InternshipFilterDto internshipFilterDto) {
        return internship.getRole().equals(internshipFilterDto.getRole());
    }
}
