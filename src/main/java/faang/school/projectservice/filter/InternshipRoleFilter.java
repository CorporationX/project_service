package faang.school.projectservice.filter;

import faang.school.projectservice.dto.client.InternshipFiltersDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements InternshipFilter{
    @Override
    public boolean isApplicable(InternshipFiltersDto filters) {
        return filters.getRolePattern() != null;
    }

    @Override
    public Stream<Internship> apply(List<Internship> internships, InternshipFiltersDto internshipFiltersDto) {
        return internships.stream()
                .filter(internship -> internship.getInterns().stream()
                        .anyMatch(intern -> intern.getRoles().stream()
                                .anyMatch(role -> role.equals(internshipFiltersDto.getRolePattern()))));
    }
}