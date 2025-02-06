package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class RoleFilter implements InternshipFilter {

    @Override
    public boolean isApplicable(InternshipFilterDto filter) {
        return filter.getRole() != null && TeamRole.getAll().contains(filter.getRole());
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> elements, InternshipFilterDto filter) {
        return elements.filter(internship -> internship.getRole().equals(filter.getRole()));
    }
}
