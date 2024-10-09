package faang.school.projectservice.model.filter.internship;

import faang.school.projectservice.model.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter{
    @Override
    public boolean isApplicable(InternshipFilterDto internship) {
        return internship.teamRole() != null;
    }

    @Override
    public Stream<Internship> applyFilter(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getMentorId().getRoles()
                .stream()
                .anyMatch(role -> role.equals(filters.teamRole())));
    }
}
