package faang.school.projectservice.filter.impl;

import faang.school.projectservice.filter.InternshipFilter;
import faang.school.projectservice.model.dto.InternshipFilterDto;
import faang.school.projectservice.model.entity.Internship;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class InternshipTeamRoleFilter implements InternshipFilter {
    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getRolePattern() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream
                .filter(internship -> internship.getInterns().stream()
                        .anyMatch(intern -> intern.getRoles().contains(internshipFilterDto.getRolePattern())));
    }
}
