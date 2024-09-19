package faang.school.projectservice.service.filter;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.mapper.InternshipMapper;
import faang.school.projectservice.model.Internship;
import lombok.AllArgsConstructor;

import java.util.stream.Stream;

@AllArgsConstructor
public class InternshipRoleFilter implements InternshipFilter {
    private final InternshipMapper internshipMapper;

    @Override
    public boolean isApplicable(InternshipFilterDto filters) {
        return filters.role() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internships, InternshipFilterDto filters) {
        return internships.filter(internship -> internship.getMentorId()
                .getRoles().contains(internshipMapper.teamRoleDtoToStringTeamRole(filters.role())));
    }
}