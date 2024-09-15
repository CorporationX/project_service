package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
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
