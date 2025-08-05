package faang.school.projectservice.service.filter.intership;

import faang.school.projectservice.apimodel.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipRoleFilter implements Filter<Internship, InternshipFilterDto> {

    @Override
    public boolean isApplicable(InternshipFilterDto dto) {
        return dto.getRoles() != null && !dto.getRoles().isEmpty();
    }

    @Override
    public Stream<Internship> filter(Stream<Internship> internships, InternshipFilterDto dto) {
        var roles = dto.getRoles().stream()
                .map(role -> TeamRole.valueOf(role.getValue()))
                .toList();

        return internships.filter(internship -> roles.contains(internship.getRole()));
    }
}